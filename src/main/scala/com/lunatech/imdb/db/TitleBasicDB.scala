package com.lunatech.imdb.db

import com.lunatech.imdb.models.imdb.{TCONST, TitleBasic}

import scala.concurrent.Future

class TitleBasicDB {

  def searchIdsByTextField(text: String, fieldName: String): Future[List[TCONST]] = {
    val query = s"""select distinct tconst from TitleBasic where $fieldName LIKE "%$text%" """

    Future.successful(Nil) // FIXME
  }

  def searchByIds(tconsts: List[TCONST]): Future[List[TitleBasic]] = {
    val query = s"""select * from TitleBasic where tconst in (${tconsts.mkString(", ")}) """

    Future.successful(Nil) // FIXME
  }

}
