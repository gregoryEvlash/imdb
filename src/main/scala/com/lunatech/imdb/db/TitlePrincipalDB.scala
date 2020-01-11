package com.lunatech.imdb.db

import com.lunatech.imdb.models.imdb.{NCONST, TCONST}

import scala.concurrent.Future

class TitlePrincipalDB {

  def searchPersonsByTitles(tconsts: List[TCONST]): Future[List[NCONST]] = {
    val query = s"""select distinct nconst from TitlePrincipal where tconst in (${tconsts.mkString(", ")}) """

    Future.successful(Nil) // FIXME
  }

}
