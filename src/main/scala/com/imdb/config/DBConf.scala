package com.imdb.config

import com.typesafe.config.Config

class DBConf(val db: String)

object DBConf {
  def apply(conf: Config): DBConf = {
    val c = conf.getConfig("database")
    new DBConf(c.getString("db"))
  }
}
