package com.lunatech.imdb.service

import java.io.{File, InputStreamReader}

import com.github.tototoshi.csv.CSVReader
import com.typesafe.scalalogging.LazyLogging
import com.lunatech.imdb.models.Performance
import com.lunatech.imdb.parser.{PerformanceInfoParser, TSVParser}
import net.tixxit.delimited.{DelimitedError, DelimitedFormat, DelimitedParser, Row}

import scala.io.Source
import scala.util.Try

object FileService extends LazyLogging{

  val DEFAULT_ENCODING = "UTF-8"
  val TSVish = DelimitedFormat.Guess
    .withSeparator("\t") // tab-delimited values
    .withRowDelim("\n")  // newline delimited rows
  val parser: DelimitedParser = DelimitedParser(TSVish)

  def parseTSVFile[T](path: String)(implicit p: TSVParser[T]): Seq[T] = {
    parser.parseFile(new File(path)).flatMap{ x =>
      x.fold(
        err => {
          logger.warn(s"Unable to parse row ${err.message}")
          None
        },
        row => {
          val result = p.parse(row.toList)
          if(result.isEmpty)
            logger.warn(s"Unable to parse row to entity (${row.toList})")
          result
        }
      )
    }
  }
}