package com.lunatech.imdb.db

import com.lunatech.imdb.models.imdb.{NCONST, TCONST}

import scala.concurrent.Future

class CrewDB {

  def searchByTitle(id: TCONST): Future[List[(NCONST, NCONST)]] = {
    val query = s"""select directors, writers from TitleBasic where tconst == "$id" """

    Future.successful(Nil) // FIXME
  }

}
