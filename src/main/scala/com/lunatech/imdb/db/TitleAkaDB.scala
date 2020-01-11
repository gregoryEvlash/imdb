package com.lunatech.imdb.db

import com.lunatech.imdb.models.imdb.{TCONST, TitleAka}

import scala.concurrent.Future

class TitleAkaDB {

  // index on tconst, isOriginalTitle

  def searchByIds(tconsts: List[String]): Future[List[TitleAka]] = {
    val query = s"""select * from TitleAka where tconst in (${tconsts.mkString(", ")}) and isOriginalTitle == 1"""

    Future.successful(Nil) // FIXME
  }

}
