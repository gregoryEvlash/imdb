package com.imdb.db

import cats.effect.ConcurrentEffect
import cats.implicits._
import com.imdb.config.DBConf
import com.imdb.db.DBCore._
import com.imdb.models.dao._
import com.imdb.models.imdb._
import slick.dbio.{Effect, NoStream}
import slick.jdbc.H2Profile.api._
import slick.lifted.TableQuery
import slick.sql.FixedSqlAction
import com.imdb.utils._

import scala.concurrent.ExecutionContext
import scala.reflect.runtime.universe._

class DBCore[F[_]](private val db: Database)(implicit F: ConcurrentEffect[F], ec: ExecutionContext) {

  def run[R](a: DBIOAction[R, NoStream, Nothing]): F[R] =
    F.fromFuture(F.delay {
      db.run[R](a)
    })

  val titleRatings = TableQuery[TitleRatingTable]
  val titlePrincipals = TableQuery[TitlePrincipalTable]
  val titleBasic = TableQuery[TitleBasicTable]
  val nameBasic = TableQuery[NameBasicTable]

  def initTables(): F[Unit] =
    for {
      tableNamesExists <- run(sql"""show tables""".as[String])
      necessaryTables = tableNames.filterNot(tableNamesExists.contains)
      actions = DBIO.seq(necessaryTables.map(createTableExp): _*)
      result <- run(actions)
    } yield result

  def uploadEntities[T <: DomainEntity](entities: List[T])(implicit tag: TypeTag[T]): F[Unit] = {
    typeOf[T] match {
      case t if t =:= typeOf[TitlePrincipal] =>
        uploadPrincipals(entities.asInstanceOf[List[TitlePrincipal]])

      case t if t =:= typeOf[TitleRating] =>
        uploadRatings(entities.asInstanceOf[List[TitleRating]])

      case t if t =:= typeOf[TitleBasic] =>
        uploadTitleBasic(entities.asInstanceOf[List[TitleBasic]])

      case t if t =:= typeOf[NameBasic] =>
        uploadNameBasic(entities.asInstanceOf[List[NameBasic]])
    }
  }

  private def createTableExp(name: String): FixedSqlAction[Unit, NoStream, Effect.Schema] =
    name match {
      case `titleRatingTableName`    => titleRatings.schema.create
      case `titlePrincipalTableName` => titlePrincipals.schema.create
      case `nameBasicTableName`      => nameBasic.schema.create
      case `titleBasicTableName`     => titleBasic.schema.create
    }

  private def uploadPrincipals(entities: List[TitlePrincipal]): F[Unit] = {
    val dao = entities.map(DAO.fromEntity)
    val upload = DBIO.seq(titlePrincipals ++= dao)
    run(upload)
  }

  private def uploadRatings(entities: List[TitleRating]): F[Unit] = {
    val dao = entities.map(DAO.fromEntity)
    val upload = DBIO.seq(titleRatings ++= dao)
    run(upload)
  }

  private def uploadTitleBasic(entities: List[TitleBasic]): F[Unit] = {
    val dao = entities.map(DAO.fromEntity)
    val upload = DBIO.seq(titleBasic ++= dao)
    run(upload)
  }

  private def uploadNameBasic(entities: List[NameBasic]): F[Unit] = {
    val dao = entities.map(DAO.fromEntity)
    val upload = DBIO.seq(nameBasic ++= dao)
    run(upload)
  }
}

object DBCore {
  val titleRatingTableName = "TitleRating"
  val titlePrincipalTableName = "TitlePrincipal"
  val nameBasicTableName = "NameBasic"
  val titleBasicTableName = "TitleBasic"

  val tableNames = List(
    titleRatingTableName,
    titlePrincipalTableName,
    nameBasicTableName,
    titleBasicTableName
  )

  def apply[F[_]](dbConf: DBConf)(implicit F: ConcurrentEffect[F], ec: ExecutionContext): F[DBCore[F]] = {
    F.delay {
      new DBCore(Database.forConfig(dbConf.db))
    }
  }
}
