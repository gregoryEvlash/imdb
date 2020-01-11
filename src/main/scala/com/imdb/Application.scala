package com.imdb

import akka.actor.ActorSystem
import cats.effect.{IO, IOApp, _}
import com.imdb.db.{DBCore, QueryBuilder}
import com.imdb.http.Gateway
import com.imdb.service.{DataLoaderService, KevinBaconService, SearchService}

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._

object Application extends IOApp {

  def run(args: List[String]): IO[ExitCode] = {
    import com.imdb.config.ConfigProvider._

    implicit val timeout: FiniteDuration = appConf.timeoutMinutes.minutes
    implicit val system: ActorSystem = ActorSystem()
    implicit val ec: ExecutionContext = ExecutionContext.fromExecutor(
      new java.util.concurrent.ForkJoinPool(appConf.threads)
    )

    for {
      db            <- DBCore[IO](dbConf)
      builder       <- QueryBuilder(db)
      loader        <- DataLoaderService(dataLoaderConf, db)
      _             <- db.initTables()
      _             <- loader.uploadFilesData(args.toArray)
      searchService <- SearchService(db, builder)
      kevinBacon    <- KevinBaconService(db, builder)
      _             <- IO.delay(system.actorOf(Gateway.props(httpConf, searchService, kevinBacon)))
    } yield ExitCode.Success
  }
}
