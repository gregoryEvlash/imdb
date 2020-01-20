package com.imdb

import sbt._

object Dependencies {

  val jodaTime = "joda-time" % "joda-time" % "2.10.5"
  val scalatest = "org.scalatest" %% "scalatest" % "3.0.1" % "test"

  val cats = "org.typelevel" %% "cats-core" % "2.0.0"
  val catsEffect = "org.typelevel" %% "cats-effect" % "2.0.0"

  val log = Seq(
    "ch.qos.logback" % "logback-classic" % "1.2.3",
    "com.typesafe.scala-logging" %% "scala-logging" % "3.9.2"
  )

  val json = Seq(
    "io.circe" %% "circe-core" % "0.12.3",
    "io.circe" %% "circe-generic" % "0.12.3",
    "io.circe" %% "circe-parser" % "0.12.3",
    "de.heikoseeberger" %% "akka-http-circe" % "1.30.0"
  )

  val akka = Seq(
    "com.typesafe.akka" %% "akka-actor" % "2.6.1",
    "com.typesafe.akka" %% "akka-http" % "10.1.11",
    "com.typesafe.akka" %% "akka-testkit" % "2.6.1" % Test,
    "com.typesafe.akka" %% "akka-stream" % "2.6.1",
    "com.lightbend.akka" %% "akka-stream-alpakka-csv" % "1.1.2"
  )

  val database = Seq(
    "com.h2database" % "h2" % "1.4.200"
  )

  val slick = Seq(
    "com.typesafe.slick" %% "slick" % "3.2.3",
    "com.typesafe.slick" %% "slick-hikaricp" % "3.2.3"
  )

  val all = Seq(jodaTime, scalatest, cats, catsEffect) ++ log ++ akka ++ json ++ database ++ slick
}
