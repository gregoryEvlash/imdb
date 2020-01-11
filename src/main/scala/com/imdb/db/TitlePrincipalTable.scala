package com.imdb.db

import com.imdb.models.dao.TitlePrincipalDAO
import com.imdb.models.imdb.NCONST
import slick.jdbc.H2Profile.api._
import slick.lifted.Tag

case class TitlePrincipalTable(tag: Tag)
    extends Table[TitlePrincipalDAO](tag, DBCore.titlePrincipalTableName) {

  def tconst = column[String]("tconst", O.Length(12))
  def ordering = column[Int]("ordering")
  def nconst = column[NCONST]("nconst", O.Length(12))
  def category = column[String]("category")
  def job = column[Option[String]]("job")
  def characters = column[Option[String]]("characters")

  def idx_nconst = index("idx_nconst", nconst)
  def idx_tconst = index("idx_tconst", tconst)

  def * =
    (tconst, ordering, nconst, category, job, characters)
      .mapTo[TitlePrincipalDAO]
}
