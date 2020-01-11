package com.imdb.service

import cats.effect.ConcurrentEffect
import cats.implicits._
import com.imdb.db.{DBCore, QueryBuilder}
import com.imdb.models.dao._
import com.imdb.models.domain._
import com.imdb.models.imdb.TCONST
import com.typesafe.scalalogging.LazyLogging
import slick.jdbc.H2Profile.api._

import scala.concurrent.ExecutionContext

class SearchService[F[_]](db: DBCore[F], builder: QueryBuilder[F])(
  implicit F: ConcurrentEffect[F],
  ec: ExecutionContext
) extends SearchServiceUtil
    with LazyLogging {

  def defaultSearch(params: collection.immutable.Map[String, String],
                    page: Page): F[IMDBServiceResponse] = {

    val query = builder.buildQueryByParams(params)
    val result = for {
      ratings <- db.run(builder.searchRatingsByTitlesQuery(query, page).result)
      films <- getFilmsInfo(ratings)
      participants <- getParticipantsInfo(ratings.map(_.tconst))
    } yield {
      convertToFilmInfo(films, participants).sortBy(_.rating).reverse
    }

    withErrorHandling(
      result.map[IMDBServiceResponse](films => Right(TitleSearchResult(films)))
    )

  }

  private def getFilmsInfo(ratings: Seq[TitleRatingDAO]): F[Seq[Film]] = {
    db.run(builder.filmsByIdsQ(ratings.map(_.tconst)).result)
      .map { films =>
        for {
          r <- ratings
          f <- films.find(_.tconst == r.tconst)
        } yield {
          r -> f
        }
      }
      .map(convertFilmDataToEntity)
  }

  private def getParticipantsInfo(films: Seq[TCONST]): F[Seq[PersonInfo]] = {
    val principalsQuery = builder.castByFilmsQuery(films)
    db.run(builder.personalInfoByPrincipal(principalsQuery).result)
      .map(convertCastDataToEntity)
  }

  private def withErrorHandling(f: F[IMDBServiceResponse]): F[IMDBServiceResponse] = {
    f.recover {
      case t =>
        logger.error(t.getMessage)
        Left(CustomError("There were internal error"))
    }
  }
}

object SearchService {
  def apply[F[_]](db: DBCore[F], builder: QueryBuilder[F])(
    implicit F: ConcurrentEffect[F], ec: ExecutionContext): F[SearchService[F]] =
    F.delay {
      new SearchService(db, builder)
    }
}
