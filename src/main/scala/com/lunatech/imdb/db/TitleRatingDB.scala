package com.lunatech.imdb.db

import com.lunatech.imdb.models.imdb.{NCONST, TCONST, TitleRating}

import scala.concurrent.Future

class TitleRatingDB {

  // todo limit offset
  def getMostRatedTitle(tconsts: Set[TCONST]): Future[List[TitleRating]] = {
    val query = s"""select * from TitleRating where tconst in (${tconsts.mkString(", ")}) ORDER BY averageRating DESC"""

    Future.successful(Nil) // FIXME
  }

}
