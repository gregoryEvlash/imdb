package com.imdb

import akka.util.ByteString

import scala.util.Random.nextString
import scala.concurrent.{Await, Future}
import scala.concurrent.duration._

trait TestDataUtil {

  def s: String = nextString(10)

  def bs: ByteString = ByteString.fromString(s)

  def await[T](f: Future[T]): T = Await.result(f, 55.seconds)
}
