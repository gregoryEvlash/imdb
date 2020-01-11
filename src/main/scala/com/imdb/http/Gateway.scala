package com.imdb.http

import akka.actor.{Actor, ActorSystem, Props}
import akka.http.scaladsl.Http
import cats.effect.ConcurrentEffect
import com.imdb.config.HttpConf
import com.imdb.service.{KevinBaconService, SearchService}

import scala.collection.mutable.ListBuffer
import scala.concurrent.ExecutionContextExecutor

class Gateway[F[_]](
  val config: HttpConf,
  val searchService: SearchService[F],
  val kevinBaconService: KevinBaconService[F]
)(implicit m: ActorSystem, F: ConcurrentEffect[F])
    extends Actor
    with SearchRoute[F] {

  implicit val dis: ExecutionContextExecutor = context.dispatcher

  private val bindings: ListBuffer[Http.ServerBinding] = ListBuffer.empty

  override def receive: Receive = {
    case boundEvent: Http.ServerBinding =>
      logger.info(s"Gateway started at: ${boundEvent.localAddress.toString}")
      bindings += boundEvent
  }

  override def preStart(): Unit = bind()
  override def postStop(): Unit = bindings foreach (_.unbind())

  private def bind(): Unit = {
    Http().bindAndHandle(routes, config.host, config.port)
  }

}

object Gateway {
  def props[F[_]](
    config: HttpConf,
    searchService: SearchService[F],
    kevinBaconService: KevinBaconService[F]
  )(implicit m: ActorSystem, F: ConcurrentEffect[F]): Props =
    Props(new Gateway(config, searchService, kevinBaconService))
}
