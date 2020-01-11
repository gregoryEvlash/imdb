package com.lunatech.imdb

import com.lunatech.imdb.models.imdb._

import scala.util.Try

package object parser {

  sealed trait TSVParser[T]{
    def parse(s: List[String]): Option[T]
  }

  val NOT_AVAILABLE = """\N"""

  def convert[T](f: => T): Option[T] = {
    Try(f).toOption
  }

  def split(s: String): List[String] = s.split(",").map(_.trim).toList

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

  implicit val titleAkaParser: TSVParser[TitleAka] = new TSVParser[TitleAka] {
    override def parse(s: List[String]): Option[TitleAka] = {
      s match{
        case List(titleId, ordering, title, region, language, types, attributes, isOriginalTitle) =>
          for{
            orderingInt   <- convert(ordering.toInt)
            isOriginalInt <- convert(isOriginalTitle.toInt)
          } yield
            TitleAka(
              titleId
              , orderingInt
              , title
              , region
              , language
              , split(types)
              , split(attributes)
              , isOriginalInt
            )
        case _ => None
      }
    }
  }

  implicit val titleBasicParser: TSVParser[TitleBasic] = new TSVParser[TitleBasic] {
    override def parse(s: List[String]): Option[TitleBasic] = {
      s match{
        case List(titleId, titleType, primaryTitle, originalTitle, isAdult, startYear, endYear, runtimeMinutes, genres) =>
          for{
            isAdultInt           <- convert(isAdult.toInt)
            runtimeMinutesDouble <- convert(runtimeMinutes.toDouble)
          } yield
            TitleBasic(
              titleId
              , titleType
              , primaryTitle
              , originalTitle
              , isAdultInt
              , startYear
              , endYear
              , runtimeMinutesDouble
              , split(genres)
            )
        case _ => None
      }
    }
  }
}



