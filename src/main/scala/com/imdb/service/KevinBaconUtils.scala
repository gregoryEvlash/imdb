package com.imdb.service

import java.util.concurrent.{ConcurrentHashMap, ConcurrentLinkedQueue}

import com.imdb.models.domain.{IMDBServiceResponse, PersonUnreachable, SixDegreesResult}
import com.imdb.models.imdb.NCONST

import scala.annotation.tailrec

trait KevinBaconUtils[F[_]] {

  protected final val kevinBaconId: NCONST = "nm0000102"
  val notFoundDegree: Int = -1
  val BATCH = 100

  type Visited = ConcurrentHashMap[String, Boolean]
  type Q = ConcurrentLinkedQueue[String]


  def found(degree: Int, target: String): IMDBServiceResponse = {
    if (degree < 0)
      Left(PersonUnreachable(target))
    else
      Right(SixDegreesResult(degree))
  }

  def nextDegreeLevel(n: Int): Int = n + 1

  @tailrec
  final def batch(queue: Q, acc: List[String], counter: Int): List[String] = {
    if(queue.isEmpty || counter == 0 ){
      acc
    } else {
      val next = queue.poll
      batch(queue, next :: acc , counter -1)
    }
  }

}
