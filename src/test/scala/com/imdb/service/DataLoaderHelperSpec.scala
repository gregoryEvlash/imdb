package com.imdb.service

import akka.util.ByteString
import com.imdb.TestDataUtil
import com.imdb.models.imdb.{TCONST, TitleBasic}
import org.scalatest.{Matchers, WordSpec}

import scala.util.Random.nextDouble
import scala.util.Try

class DataLoaderHelperSpec extends WordSpec with Matchers with TestDataUtil with DataLoaderHelper {

  "DataLoaderHelper" should {

    "parse args to map" in {

      val (a, b, c, d) = (s, s, s, s)
      val args = Array(nameBasic, a, titleRating, b, doUpload, s, titleBasic, c, titlePrincipal, d, s)

      val resultMap = argsToParamMap(args)

      resultMap(nameBasic)      shouldBe a
      resultMap(titleRating)    shouldBe b
      resultMap(titleBasic)     shouldBe c
      resultMap(titlePrincipal) shouldBe d

      Try(resultMap(doUpload).toInt).toOption.isDefined shouldBe true

      resultMap.size shouldBe 6
    }

    "read data properly" in {
      val isAdult = ByteString.fromString(1.toString)
      val runtimeMinutes = ByteString.fromString(nextDouble().toString)

      val data = List(bs, bs, bs, bs, isAdult, bs, bs, runtimeMinutes, bs)

      val parsedResult = parseToRead(data)

      parsedResult.nonEmpty shouldBe true
      parsedResult.head.getOrElse(List.empty).nonEmpty shouldBe true

      val entityResult = convertToEntity[TitleBasic](parsedResult)
      entityResult.nonEmpty shouldBe true
    }
  }

}
