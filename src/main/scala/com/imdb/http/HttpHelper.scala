package com.imdb.http

import akka.http.scaladsl.model._
import akka.http.scaladsl.server.{Directive, Directives, Route}
import com.imdb.json.response._
import com.imdb.models.domain.{CustomError, IMDBServiceResponse, Page}
import com.typesafe.scalalogging.LazyLogging
import io.circe.syntax._

import scala.concurrent.Future
import scala.util.{Failure, Success, Try}

trait HttpHelper extends LazyLogging with Directives{

  type Resp = (StatusCode, String)

  val timeoutResponse = HttpResponse(
    StatusCodes.EnhanceYourCalm,
    entity = "Unable to serve response within time limit, please enhance your calm.")


  /**
    * Convert response from service to response for answer
    *
    * @param value Response from service
    * @return
    */
  def handle(value: Future[Any]): Route = {
    onComplete(value.mapTo[IMDBServiceResponse]) {
      case Success(v) =>
        logger.info(s"Making response $v")
        complete(
          toResponse(either(v))
        )
      case Failure(ex) =>
        logger.error(ex.toString)
        complete(
          toResponse(error(ex))
        )
    }
  }

  /**
    * Convert service response to status code and message
    *
    * @param value Either error or good response from service
    * @return status code and message
    */
  protected def either(value: IMDBServiceResponse): (StatusCode, String) = {
    value.fold[Resp](
      err  => StatusCodes.BadRequest -> err.asJson.noSpaces,
      resp => StatusCodes.OK -> resp.asJson.noSpaces
    )
  }

  /**
    * Handle and log unexpected message
    *
    * @param value anything
    * @return status code and message
    */

  protected def error(value: Any): (StatusCode, String) = {
    logger.warn(s"Unexpected response $value")
    StatusCodes.InternalServerError -> CustomError(value.toString).asJson.noSpaces
  }

  /**
    * Convert tuple to response
    *
    * @param tpl code and message
    * @return
    */
  protected def toResponse(tpl: (StatusCode, String)): HttpResponse = {
    HttpResponse(
      status = tpl._1,
      entity = HttpEntity(ContentType(MediaTypes.`application/json`), tpl._2)
    )
  }

  protected def witLimitOffset: Directive[(Option[String], Option[String])] = {
    parameters("limit".?, "offset".?)
  }

  protected def withPage: Directive[Tuple1[Page]] = {
    witLimitOffset.tmap {
      case (limitO, offsetO) =>
        val limit  = convertToInt(limitO, Page.DEFAULT_LIMIT)
        val offset = convertToInt(offsetO, Page.DEFAULT_OFFSET)
        Page(limit, offset)
    }
  }

  private def convertToInt(value: Option[String], default: Int) = {
    Try(value.map(_.toInt)).toOption.flatten.getOrElse(default)
  }

}
