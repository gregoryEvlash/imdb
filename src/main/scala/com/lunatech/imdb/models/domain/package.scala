package com.lunatech.imdb.models

package object domain {

  sealed trait IMDBServiceError
  sealed trait IMDBServiceResult

  case class TitleSearchResult(films: List[Product]) extends IMDBServiceResult

  type IMDBServiceResponse = Either[IMDBServiceError, IMDBServiceResult]

  case class PersonInfo()

  case class Product(id: String,
                     title: String,
                     originalTitle: String,
                     year: String,
                     cast: List[PersonInfo],
                     crew: List[PersonInfo])

}
