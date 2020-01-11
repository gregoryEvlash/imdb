package com.imdb.service

import akka.NotUsed
import akka.stream.alpakka.csv.scaladsl.CsvParsing
import akka.stream.alpakka.csv.scaladsl.CsvParsing.Tab
import akka.stream.scaladsl.Flow
import akka.util.ByteString
import com.imdb.parser.TSVParser
import com.typesafe.scalalogging.LazyLogging

trait DataLoaderHelper extends LazyLogging{

  type Read = Either[Throwable, List[String]]

  val nameBasic = "nameBasic"
  val titleRating = "titleRating"
  val titleBasic = "titleBasic"
  val titlePrincipal = "titlePrincipal"
  val nameTitle = "nameTitle"

  val doUpload = "upload"

  private val emptyChar: Byte = '\0'

  protected val csvScanner: Flow[ByteString, List[ByteString], NotUsed] =
    CsvParsing.lineScanner(Tab, quoteChar = emptyChar, maximumLineLength = Int.MaxValue)

  protected val parseFlow: Flow[List[ByteString], List[Read], NotUsed] =
    Flow.fromFunction[List[ByteString], List[Read]](parseToRead)

  protected def convertFlow[T: TSVParser]: Flow[List[Read], List[T], NotUsed] =
    Flow.fromFunction[List[Read], List[T]](convertToEntity(_))

  def convertToEntity[T](batch: List[Read])(implicit p: TSVParser[T]): List[T] = {
    batch.flatMap { x =>
      x.fold(err => {
        logger.warn(s"Unable to parse row ${err.getMessage}")
        None
      }, row => {
        val result = p.parse(row)
        if (result.isEmpty)
          logger.warn(s"Unable to parse row to entity ($row)")
        result
      })
    }
  }

  protected def parseToRead(x: List[ByteString])= List(util.Try(x.map(_.utf8String)).toEither)

  protected def argsToParamMap(args: Array[String]): Map[String, String] = {
    def deriveValue(name: String): (String, String) = {
      if(args.indexOf(name) > -1)
        name -> args.apply(args.indexOf(name) + 1)
      else
        "" -> ""
    }

    Map(
      doUpload -> args.indexOf(doUpload).toString,
      deriveValue(nameBasic),
      deriveValue(titleRating),
      deriveValue(titleBasic),
      deriveValue(titlePrincipal),
      deriveValue(nameTitle)
    )
  }
}
