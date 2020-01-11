package com.lunatech.imdb.db

import com.lunatech.imdb.models.imdb.{NCONST, NameBasic, TCONST}

import scala.concurrent.Future

class PersonInfoDB {

  def searchByIds(nconsts: List[NCONST]): Future[List[NameBasic]] = {
    val query = s"""select * from PersonInfo where nconst in (${nconsts.mkString(", ")}) """

    Future.successful(Nil) // FIXME
  }

}
