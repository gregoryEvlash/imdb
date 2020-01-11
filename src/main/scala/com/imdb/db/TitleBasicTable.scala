package com.imdb.db

import com.imdb.models.dao.TitleBasicDAO
import com.imdb.models.imdb.TCONST
import slick.jdbc.H2Profile.api._
import slick.lifted.Tag

case class TitleBasicTable(tag: Tag) extends Table[TitleBasicDAO](tag, DBCore.titleBasicTableName){

  def tconst = column[TCONST]("tconst", O.PrimaryKey, O.Length(12))
  def titleType = column[String]("titleType")
  def primaryTitle = column[String]("primaryTitle")
  def originalTitle = column[String]("originalTitle")
  def isAdult = column[Int]("isAdult")
  def startYear = column[String]("startYear")
  def endYear = column[String]("endYear")
  def runtimeMinutes = column[Double]("runtimeMinutes")
  def genres = column[String]("genres")

  // index on titles nad genres
  def idx_genres = index("idx_genres", genres)
  def idx_primaryTitle = index("idx_primaryTitle", primaryTitle)
  def idx_originalTitle = index("idx_originalTitle", originalTitle)

  def * = (tconst, titleType, primaryTitle, originalTitle, isAdult, startYear, endYear, runtimeMinutes, genres).mapTo[TitleBasicDAO]
}