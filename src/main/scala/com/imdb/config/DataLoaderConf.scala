package com.imdb.config

import com.typesafe.config.Config

class DataLoaderConf(val batchSize: Int, val threads: Int)
object DataLoaderConf {
  def apply(conf: Config): DataLoaderConf = {
    val c = conf.getConfig("loader")
    new DataLoaderConf(c.getInt("batchSize"), c.getInt("threads"))
  }
}