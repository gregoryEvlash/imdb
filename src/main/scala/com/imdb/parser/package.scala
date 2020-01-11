package com.imdb

import com.imdb.models.imdb._

import scala.util.Try

package object parser {

  sealed trait TSVParser[T]{
    def parse(s: List[String]): Option[T]
  }

  val NOT_AVAILABLE = """\N"""

  def convert[T](f: => T): Option[T] = {
    Try(f).toOption
  }

  def checkOnNull(s: String): Option[String] =
    Some(s).filterNot(_ == NOT_AVAILABLE)

  implicit val titleRatingParser: TSVParser[TitleRating] = new TSVParser[TitleRating] {
    override def parse(s: List[String]): Option[TitleRating] = {
      s match{
        case List(tconst, averageRating, numVotes) =>
          for{
            rating <- Try(averageRating.toDouble).toOption
            votes  <- Try(numVotes.toLong).toOption
          } yield TitleRating(tconst, rating, votes)
        case _ => None
      }
    }
  }

  implicit val titleBasicParser: TSVParser[TitleBasic] = new TSVParser[TitleBasic] {
    override def parse(s: List[String]): Option[TitleBasic] = {
      s match{
        case List(tconst, titleType, primaryTitle, originalTitle, isAdult, startYear, endYear, runtimeMinutes, genres) =>
          for{
            isAdultInt           <- convert(isAdult.toInt)
            runtimeMinutesDouble <- convert(runtimeMinutes.toDouble)
          } yield
            TitleBasic(
              tconst
              , titleType
              , primaryTitle
              , originalTitle
              , isAdultInt
              , startYear
              , endYear
              , runtimeMinutesDouble
              , genres
            )
        case _ => None
      }
    }
  }

  implicit val titlePrincipalParser: TSVParser[TitlePrincipal] = new TSVParser[TitlePrincipal] {
    override def parse(s: List[String]): Option[TitlePrincipal] = {
      s match{
        case List(tconst, ordering, nconst, category, job, characters) =>
          for{
            orderingInt <- convert(ordering.toInt)
          } yield
            TitlePrincipal(
              tconst
              , orderingInt
              , nconst
              , category
              , checkOnNull(job)
              , checkOnNull(characters)

            )
        case _ => None
      }
    }
  }

  implicit val nameBasicParser: TSVParser[NameBasic] = new TSVParser[NameBasic] {
    override def parse(s: List[String]): Option[NameBasic] = {
      s match{
        case List(nconst, primaryName, birthYear, deathYear, primaryProfession, knownForTitles) =>
          Some(
            NameBasic(
              nconst
              , primaryName
              , birthYear
              , checkOnNull(deathYear)
              , primaryProfession
              , knownForTitles
            )
          )
        case _ => None
      }
    }
  }

}



