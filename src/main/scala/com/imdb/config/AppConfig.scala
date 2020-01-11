package com.imdb.config

import com.typesafe.config.Config

class AppConfig(val timeoutMinutes: Int, val threads: Int)

object AppConfig {
  def apply(conf: Config): AppConfig = {
    val c = conf.getConfig("app")
    new AppConfig(c.getInt("timeoutMinutes"), c.getInt("threads"))
  }
}
