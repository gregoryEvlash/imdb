package com.imdb.models

import com.imdb.TestDataUtil
import com.imdb.models.dao.DAO
import com.imdb.models.imdb._
import org.scalatest.{Matchers, WordSpec}

import scala.util.Random.{nextDouble, nextInt}

class DaoSpec  extends WordSpec with Matchers with TestDataUtil {

  "DAO" should {

    "convert TitleRating properly" in {
      val gauge = TitleRating(s, nextDouble, nextInt)
      val result = DAO.fromEntity(gauge)

      result.tconst        shouldBe gauge.tconst
      result.averageRating shouldBe gauge.averageRating
      result.numVotes      shouldBe gauge.numVotes
    }

    "convert TitlePrincipal properly" in {

      val gauge = TitlePrincipal(s, nextInt, s, s, Some(s), Some(s))
      val result = DAO.fromEntity(gauge)

      result.tconst     shouldBe gauge.tconst
      result.ordering   shouldBe gauge.ordering
      result.nconst     shouldBe gauge.nconst
      result.category   shouldBe gauge.category
      result.job        shouldBe gauge.job
      result.characters shouldBe gauge.characters
    }

    "convert TitleBasic properly" in {
      val gauge = TitleBasic(s, s, s, s, 1, s, s, nextDouble(), List(s, s).mkString(","))
      val basic = DAO.fromEntity(gauge)

      basic.tconst         shouldBe gauge.tconst
      basic.titleType      shouldBe gauge.titleType
      basic.primaryTitle   shouldBe gauge.primaryTitle
      basic.originalTitle  shouldBe gauge.originalTitle
      basic.isAdult        shouldBe gauge.isAdult
      basic.startYear      shouldBe gauge.startYear
      basic.endYear        shouldBe gauge.endYear
      basic.runtimeMinutes shouldBe gauge.runtimeMinutes

    }

    "convert NameBasic properly" in {

      val gauge = NameBasic(s, s, s, Some(s), s, List(s, s).mkString(","))
      val basic = DAO.fromEntity(gauge)

      basic.nconst            shouldBe gauge.nconst
      basic.primaryName       shouldBe gauge.primaryName
      basic.birthYear         shouldBe gauge.birthYear
      basic.deathYear         shouldBe gauge.deathYear
      basic.primaryProfession shouldBe gauge.primaryProfession
    }

  }

}
