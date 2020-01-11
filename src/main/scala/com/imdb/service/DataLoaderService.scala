package com.imdb.service

import java.nio.file.Paths

import cats.implicits._
import akka.actor.ActorSystem
import akka.stream.IOResult
import akka.stream.scaladsl.{FileIO, _}
import cats.effect.ConcurrentEffect
import com.imdb.config.DataLoaderConf
import com.imdb.db.DBCore
import com.imdb.models.imdb._
import com.imdb.parser.TSVParser
import com.typesafe.scalalogging.LazyLogging
import com.imdb.utils._

import scala.concurrent.ExecutionContext
import scala.reflect.runtime.universe._

class DataLoaderService[F[_]](dataLoaderConf: DataLoaderConf, db: DBCore[F])
                       (implicit val ec: ExecutionContext, F: ConcurrentEffect[F], ac: ActorSystem) extends LazyLogging with DataLoaderHelper {

  def uploadData[T <: DomainEntity : TSVParser : TypeTag](path: String): F[IOResult] = {
    val sink = Sink.foreachAsync[List[T]](dataLoaderConf.threads){
      s => F.toFuture(db.uploadEntities(s))
    }

    F.fromFuture(
      F.delay(
        FileIO.fromPath(Paths.get(path))
        .via(csvScanner)
        .via(parseFlow)
        .grouped(dataLoaderConf.batchSize)
        .map(_.flatten.toList)
        .via(convertFlow)
        .to(sink)
        .run()
      )
    )
  }

  def uploadFilesData(args: Array[String]): F[Unit] = {
    val params = argsToParamMap(args)
    if (params(doUpload) != "-1") {
      for {
        _ <- params.get(titleRating).map(uploadData[TitleRating]).getOrElse(F.unit)
        _ = logger.info(s"$titleRating was uploaded")
        _ <- params.get(nameBasic).map(uploadData[NameBasic]).getOrElse(F.unit)
        _ = logger.info(s"$nameBasic was uploaded")
        _ <- params.get(titleBasic).map(uploadData[TitleBasic]).getOrElse(F.unit)
        _ = logger.info(s"$titleBasic was uploaded")
        _ <- params.get(titlePrincipal).map(uploadData[TitlePrincipal]).getOrElse(F.unit)
        _ = logger.info(s"$titlePrincipal was uploaded")
      } yield ()
    }
    else F.unit
  }

}


object DataLoaderService {
  def apply[F[_]](dataLoaderConf: DataLoaderConf, db: DBCore[F])(
    implicit ac: ActorSystem, F: ConcurrentEffect[F], ec: ExecutionContext): F[DataLoaderService[F]] = {
    F.delay{
      new DataLoaderService(dataLoaderConf, db)
    }
  }
}
