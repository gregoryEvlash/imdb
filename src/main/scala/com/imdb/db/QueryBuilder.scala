package com.imdb.db

import cats.effect.ConcurrentEffect
import com.imdb.models.dao._
import com.imdb.models.domain.Page
import com.imdb.models.imdb._
import slick.dbio.Effect
import slick.jdbc.H2Profile.api._
import slick.sql.FixedSqlStreamingAction

import scala.concurrent.ExecutionContext

class QueryBuilder[F[_]](db: DBCore[F])(
  implicit executionContext: ExecutionContext
) {
  import db._

  type TQuery = Query[Rep[TCONST], TCONST, Seq]

  def findActorByName(name: String): FixedSqlStreamingAction[Seq[NameBasicDAO], NameBasicDAO, Effect.Read] =
    nameBasic.filter(_.primaryName === name).result // perhaps like

  def colleagues(nconsts: Seq[NCONST]): FixedSqlStreamingAction[Seq[NCONST], NCONST, Effect.Read] =
    titlePrincipals
      .filter(_.nconst inSet nconsts)
      .map(_.tconst)
      .distinct
      .join(titlePrincipals)
      .on(_ === _.tconst)
      .map(_._2.nconst)
      .distinct
      .result

  def castByFilmsQuery(titles: Seq[TCONST]): Query[TitlePrincipalTable, TitlePrincipalDAO, Seq] =
    titlePrincipals
      .filter(_.tconst inSetBind titles)

  def personalInfoByPrincipal(
    query: Query[TitlePrincipalTable, TitlePrincipalDAO, Seq]
  ): Query[(TitlePrincipalTable, NameBasicTable), (TitlePrincipalDAO, NameBasicDAO), Seq] = {
    query
      .join(nameBasic)
      .on(_.nconst === _.nconst)
  }

  def searchFilmsByTitlesQuery(text: String): TQuery =
    titleBasic
      .filter { title =>
        (title.originalTitle like s"%$text%") || (title.primaryTitle like s"%$text%")
      }
      .map(_.tconst)
      .distinct

  def searchTitleIdsByGenresQuery(genre: String): TQuery =
    titleBasic
      .filter {
        _.genres like s"%$genre%"
      }
      .map(_.tconst)
      .distinct

  def searchRatingsByTitlesQuery(query: TQuery, page: Page): Query[TitleRatingTable, TitleRatingDAO, Seq] =
    query
      .join(titleRatings)
      .on(_ === _.tconst)
      .map(_._2)
      .sortBy(_.averageRating.desc)
      .drop(page.offset)
      .take(page.limit)

  def filmsByIdsQ(ids: Seq[TCONST]) =
    titleBasic
      .filter(_.tconst inSet ids)

  def combineQueries(queries: Seq[Option[TQuery]]): TQuery =
    queries.flatten
      .reduce[TQuery] {
        case (a, b) =>
          a.join(b).on(_ === _).map(_._1)
      }

  def buildQueryByParams(params: Map[String, String]): TQuery = {
    val titleRequest = params.get("title").map { searchFilmsByTitlesQuery }
    val genreRequest = params.get("genre").map { searchTitleIdsByGenresQuery }
    val queries = Seq(titleRequest, genreRequest).flatten

    if (queries.nonEmpty) {
      combineQueries(Seq(titleRequest, genreRequest))
    } else {
      titleBasic.map(_.tconst)
    }
  }
}

object QueryBuilder {
  def apply[F[_]](db: DBCore[F])(implicit F: ConcurrentEffect[F], ec: ExecutionContext): F[QueryBuilder[F]] = {
    F.delay {
      new QueryBuilder(db)
    }
  }
}
