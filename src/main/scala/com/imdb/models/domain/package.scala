package com.imdb.models

import com.imdb.models.imdb.{NCONST, TCONST}

package object domain {

  case class Page(limit: Int, offset: Int)

  object Page{
    val DEFAULT_LIMIT = 10
    val DEFAULT_OFFSET = 0
    val default = Page(DEFAULT_LIMIT, DEFAULT_OFFSET)
  }

  sealed trait IMDBServiceError
  sealed trait IMDBServiceResult

  type IMDBServiceResponse = Either[IMDBServiceError, IMDBServiceResult]

  case class PersonNotFount(name: String) extends IMDBServiceError
  case class CustomError(message: String) extends IMDBServiceError

  case class Film(filmTconst: TCONST,
                  filmPrimaryTitle: String,
                  filmOriginalTitle: String,
                  filmStartYear: String,
                  averageRating: Double,
                  genre: String
                 )

  case class PersonInfo(tconst: TCONST,
                        nconst: NCONST,
                        personPrimaryName: String,
                        personBirthYear: String,
                        category: String)

  case class Product(id: String,
                     title: String,
                     originalTitle: String,
                     year: String,
                     rating: Double,
                     genre: String,
                     castsCrews: Seq[PersonInfo])


  case class TitleSearchResult(films: Seq[Product]) extends IMDBServiceResult
  case class GenresSearchResult(films: Seq[Film]) extends IMDBServiceResult
  case class SixDegreesResult(degree: Int) extends IMDBServiceResult
}
