package com.imdb.parser

import com.imdb.TestDataUtil
import com.imdb.models.imdb._
import org.scalatest.{Matchers, WordSpec}

import scala.util.Random._

class ParserSpec extends WordSpec with Matchers with TestDataUtil {

  "Parser" should {

    "check for null" in {
      checkOnNull(NOT_AVAILABLE) shouldBe None
    }

    "read title rating" in {
      val gauge = List(s, nextDouble.toString, nextInt.toString)
      implicitly[TSVParser[TitleRating]].parse(gauge).isDefined shouldBe true
    }

    "read title basic" in {

      val genres = s"$s, $s"
      val gauge = List(s, s, s, s, "1", s, s, nextDouble().toString, genres)
      val result = implicitly[TSVParser[TitleBasic]].parse(gauge)

      result.isDefined shouldBe true
      result.get.genres.nonEmpty shouldBe true
    }

    "read title principal" in {
      val gauge = List(s, nextInt.toString, s, s, s, s)
      implicitly[TSVParser[TitlePrincipal]].parse(gauge).isDefined shouldBe true
    }

    "read name basic" in {
      val titles = s"$s, $s"
      val gauge = List(s, s, s, s, s, titles)
      val result = implicitly[TSVParser[NameBasic]].parse(gauge)

      result.isDefined shouldBe true
      result.get.knownForTitles.nonEmpty shouldBe true
    }

  }
}
