package com.lunatech.imdb

import com.lunatech.imdb.models.imdb.TitleRating
import com.lunatech.imdb.parser._
import com.lunatech.imdb.service.FileService

object Application {


  def main(args: Array[String]): Unit = {

    val path  = "/home/gregory/Downloads/imdb data/title.ratings.tsv"

    val result = FileService.parseTSVFile[TitleRating](path)

    println(result.size)

  }
}
