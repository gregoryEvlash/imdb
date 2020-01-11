package com.lunatech.imdb.service

import com.lunatech.imdb.db.{DB, PersonInfoDB}
import com.lunatech.imdb.models.domain._
import com.lunatech.imdb.models.imdb._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class SearchService(db: DB) {


  val defaultTitleSearchParams = List("primaryTitle", "originalTitle")

  val pageLimit = 10

//  Requirement #1 (easy):
//
//    IMDb copycat: Present the user with endpoint for allowing them to search by movie’s primary title or original title.
//    The outcome should be related information to that title, including cast and crew.


  def searchByTitle(text: String, searchBy: List[String]): Future[IMDBServiceResponse] = {
    val searchByPrimary  = db.titleBasicDB.searchIdsByTextField(text, "primaryTitle")
    val searchByOriginal = db.titleBasicDB.searchIdsByTextField(text, "originalTitle")

    val foundByTitle: Future[Set[TCONST]] = for{
      byPrimary  <- searchByPrimary
      byOriginal <- searchByOriginal
    } yield {
      (byPrimary ++ byOriginal).toSet
    }

    for{
      titleIds        <- foundByTitle
      filmsByRating   <- db.titleRatingDB.getMostRatedTitle(titleIds)
      filmIdsByRating = filmsByRating.map(_.tconst)
      involved        <- db.titlePrincipalDB.searchPersonsByTitles(filmIdsByRating)
      persons         <- db.personalInfoDB.searchByIds(involved)
      films           <- db.titleBasicDB.searchByIds(filmIdsByRating)
    } yield {
      Right(
        TitleSearchResult(convertToFilmInfo(films, persons))
      )
    }

  }

  def convertToFilmInfo(films: List[TitleBasic], persons: List[NameBasic]): List[Product] = ???
//
//  Requirement #2 (easy):
//
//    Top rated movies: Given a query by the user, you must provide what are the top rated movies for a genre (If the user searches horror, then it should show a list of top rated horror movies).


//
//    Requirement #3 (difficult):
//    Shortest path in graph
//    Six degrees of Kevin Bacon: Given a query by the user, you must provide what’s the degree of separation between the person (e.g. actor or actress) the user has entered and Kevin Bacon.

}
