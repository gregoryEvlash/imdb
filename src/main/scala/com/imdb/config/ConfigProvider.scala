package com.imdb.config

import com.typesafe.config.ConfigFactory

object ConfigProvider {

  private lazy val conf = ConfigFactory.load()

  lazy val httpConf = HttpConf.apply(conf)
  lazy val dbConf = DBConf.apply(conf)
  lazy val dataLoaderConf = DataLoaderConf.apply(conf)
  lazy val appConf = AppConfig.apply(conf)

}
