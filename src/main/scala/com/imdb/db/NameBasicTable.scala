package com.imdb.db

import com.imdb.models.dao.NameBasicDAO
import com.imdb.models.imdb.NCONST
import slick.jdbc.H2Profile.api._
import slick.lifted.Tag

case class NameBasicTable(tag: Tag)
    extends Table[NameBasicDAO](tag, DBCore.nameBasicTableName) {

  def nconst = column[NCONST]("nconst", O.PrimaryKey, O.Length(12))
  def primaryName = column[String]("primaryName")
  def birthYear = column[String]("birthYear")
  def deathYear = column[Option[String]]("deathYear")
  def primaryProfession = column[String]("primaryProfession")
  def knownForTitles = column[String]("knownForTitles")

  def idx_name = index("idx_primaryName", primaryName)

  def * =
    (
      nconst,
      primaryName,
      birthYear,
      deathYear,
      primaryProfession,
      knownForTitles
    ).mapTo[NameBasicDAO]
}
