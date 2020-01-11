package com.imdb.db

import com.imdb.models.dao.TitleRatingDAO
import slick.jdbc.H2Profile.api._
import slick.lifted.Tag

case class TitleRatingTable(tag: Tag)
    extends Table[TitleRatingDAO](tag, DBCore.titleRatingTableName) {

  def tconst = column[String]("tconst", O.PrimaryKey, O.Length(12))
  def averageRating = column[Double]("averageRating")
  def numVotes = column[Long]("numVotes")

  def idx_rating = index("idx_rating", averageRating)

  def * = (tconst, averageRating, numVotes).mapTo[TitleRatingDAO]
}
