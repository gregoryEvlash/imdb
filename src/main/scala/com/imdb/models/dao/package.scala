package com.imdb.models

import com.imdb.models.imdb._

package object dao {

  sealed trait DAO

  case class NameBasicDAO(nconst: NCONST,
                          primaryName: String,
                          birthYear: String,
                          deathYear: Option[String],
                          primaryProfession: String,
                          knownForTitles: String)
      extends DAO

  case class TitleBasicDAO(tconst: TCONST,
                           titleType: String,
                           primaryTitle: String,
                           originalTitle: String,
                           isAdult: Int,
                           startYear: String,
                           endYear: String,
                           runtimeMinutes: Double,
                           genres: String)
      extends DAO

  case class TitlePrincipalDAO(tconst: TCONST,
                               ordering: Int,
                               nconst: NCONST,
                               category: String,
                               job: Option[String],
                               characters: Option[String])
      extends DAO

  case class TitleRatingDAO(tconst: TCONST,
                            averageRating: Double,
                            numVotes: Long)
      extends DAO

  case class NameTitleDAO(nconst: NCONST, tconst: TCONST) extends DAO

  object DAO {
    def fromEntity(e: TitleRating): TitleRatingDAO = {
      import e._
      TitleRatingDAO(tconst, averageRating, numVotes)
    }

    def fromEntity(e: NameTitle): NameTitleDAO = {
      import e._
      NameTitleDAO(tconst, nconst)
    }

    def fromEntity(e: TitlePrincipal): TitlePrincipalDAO = {
      import e._
      TitlePrincipalDAO(tconst, ordering, nconst, category, job, characters)
    }

    def fromEntity(e: TitleBasic): TitleBasicDAO = {
      import e._
      TitleBasicDAO(
        tconst,
        titleType,
        primaryTitle,
        originalTitle,
        isAdult,
        startYear,
        endYear,
        runtimeMinutes,
        genres
      )
    }

    def fromEntity(e: NameBasic): NameBasicDAO = {
      import e._
      NameBasicDAO(
        nconst,
        primaryName,
        birthYear,
        deathYear,
        primaryProfession,
        knownForTitles
      )
    }
  }

}
