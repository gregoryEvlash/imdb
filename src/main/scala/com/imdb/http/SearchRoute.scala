package com.imdb.http

import akka.http.scaladsl.server.{Directives, Route}
import cats.effect.ConcurrentEffect
import cats.implicits._
import com.imdb.config.ConfigProvider
import com.imdb.service.{KevinBaconService, SearchService}
import com.typesafe.scalalogging.LazyLogging
import com.imdb.utils._

import scala.concurrent.duration._

trait SearchRoute[F[_]] extends LazyLogging with HttpHelper with Directives {

  val searchService: SearchService[F]
  val kevinBaconService: KevinBaconService[F]

  implicit val timeout = ConfigProvider.httpConf.timeoutMinutes.minutes

  private val mainPrefix = "imdb"
  private val videos = "videos"
  private val actors = "actors"
  private val sixDegrees = "6degrees"

  def routes(implicit F: ConcurrentEffect[F]): Route =
    pathPrefix(mainPrefix) {
      get {
        pathPrefix(videos) {
          pathEnd {
            withPage { page =>
              parameterMap { params =>
                logger.info(
                  s"Get video search request by params: [${params.mkString(", ")}]"
                )
                handle {
                  F.toFuture(searchService.defaultSearch(params, page))
                }
              }
            }
          }
        } ~ pathPrefix(actors / sixDegrees / Segment) { actor =>
          withRequestTimeout(timeout, request => timeoutResponse) {
            logger.info(
              s"""Get "Six degrees of Kevin Bacon" request for "$actor" """
            )
            handle {
              F.toFuture(kevinBaconService.sixDegreesSearch(actor))
            }
          }
        }
      }
    }

}
