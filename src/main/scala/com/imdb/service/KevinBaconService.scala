package com.imdb.service

import java.util.concurrent.{ConcurrentHashMap, ConcurrentLinkedQueue}

import cats.effect.ConcurrentEffect
import cats.effect.concurrent.Ref
import cats.implicits._
import com.imdb.db.{DBCore, QueryBuilder}
import com.imdb.models.domain.{IMDBServiceResponse, PersonNotFount, SixDegreesResult}
import com.imdb.models.imdb.NCONST
import com.typesafe.scalalogging.LazyLogging
import org.joda.time.DateTime

import scala.annotation.tailrec
import scala.collection.JavaConverters._
import scala.concurrent.ExecutionContext

// todo refactor dat weird service
class KevinBaconService[F[_]](db: DBCore[F], builder: QueryBuilder[F])(
  implicit ec: ExecutionContext,
  F: ConcurrentEffect[F]
) extends SearchServiceUtil
    with LazyLogging {

  private final val kevinBaconId: NCONST = "nm0000102"
  val notFoundDegree: Int = -1

  val BATCH = 100

  val visited = Ref.unsafe(new ConcurrentHashMap[String, Boolean]())
  val q = Ref.unsafe(new ConcurrentLinkedQueue[String]())

  def findColleagues(nconst: Seq[NCONST]): F[Seq[NCONST]] = {
    db.run(builder.colleagues(nconst))
  }

  def FS(degree: Int): F[Int] = F.pure(degree)
  def ff[T](any: T): F[T] = F.delay(any)
  def personNotFoundError(name: String): F[IMDBServiceResponse] = F.pure(Left(PersonNotFount(name)))
  def found(degree: Int): IMDBServiceResponse = Right(SixDegreesResult(degree))

  def nextDegreeLevel(n: Int): Int = n + 1
  def decreesLevelSize(n: Int): Int = n -1

  @tailrec
  private def batch(queue: ConcurrentLinkedQueue[String], acc: List[String], counter: Int): List[String] = {
    if(queue.isEmpty || counter == 0 ){
      acc
    } else {
      val next = queue.poll
      batch(queue, next :: acc , counter -1)
    }
  }

  def checkOnLeftData(queue: ConcurrentLinkedQueue[String], degree: Int)(orElse: => F[Int]): F[Int] = {
    if(queue.isEmpty || degree > 6)
      FS(notFoundDegree)  // if target not found -> unreachable
    else {
      orElse
    }
  }

  def withCheckedNextBatch(queue: ConcurrentLinkedQueue[String], visited: ConcurrentHashMap[String, Boolean], degree: Int)(f: => F[Int], orElse: List[String] => F[Int]) = {
    val next = batch(queue, Nil, BATCH)
    val notVisited = next.filterNot(visited.containsKey)
    if (notVisited.isEmpty) {
      f
    } else {
      orElse(notVisited)
    }
  }

  def performNextBatch(notVisited: List[String], visited: ConcurrentHashMap[String, Boolean], degree: Int, target: String, leftForNextLayer: Int): F[Int] = {
    for {
      _ <- F.delay(notVisited.foreach(visited.put(_, true)))
      colleagues <- findColleagues(notVisited)
      result <- checkIfContainsOtherwiseToQ(nextDegreeLevel(degree) ,target, colleagues)(checkLayerColl(leftForNextLayer - notVisited.size, degree, target))
    } yield {
      result
    }
  }

  // if data is empty or degree more than 6 return not found otherwise perform further
  def checkLayerColl(leftForNextLayer: Int, degree: Int, target: String): F[Int] = {
    if (leftForNextLayer <= 0) { // if target in layer not found go to second layer
      recur(nextDegreeLevel(degree), target)
    } else {
      for {
        qf <- q.get
        vf <- visited.get
        result <- checkOnLeftData(qf, degree){
          withCheckedNextBatch(qf, vf, degree)(
            checkLayerColl(leftForNextLayer - BATCH, degree, target),
            performNextBatch(_, vf, degree, target, leftForNextLayer)
          )
        }
      } yield {
        result
      }
    }
  }

  def recur(degree: Int, target: String): F[Int] = {
    for{
     qf <- q.get
    result <- checkLayerColl(qf.size(), degree, target)
    } yield result
  }

  def checkIfContainsOtherwiseToQ(degree:Int, target: String, colleagues: Seq[String])(f: => F[Int]): F[Int] = {

    val notChecked = colleagues.toSet
    val contains = notChecked.contains(target)
    if(!contains) {
      for{
        _      <- q.get.map(_.addAll(notChecked.asJava))
        result <- f
      } yield result
    } else {
      FS(degree)
    }
  }

  def search(target: String): F[IMDBServiceResponse] = {
    val initDegreeLevel = 0
    for{
      _               <- F.delay(println(DateTime.now()))
      kevinColleagues <- findColleagues(Seq(kevinBaconId))
      _               <- F.delay(println(DateTime.now()))
      result <- checkIfContainsOtherwiseToQ(initDegreeLevel, target, kevinColleagues)(
        recur(nextDegreeLevel(initDegreeLevel), target)
      )
    } yield {
      found(result)
    }
  }

// write not by single but fro whole set off ids
  def sixDegreesSearch(actorName: String): F[IMDBServiceResponse] = {
    for{
      _ <- q.get.map(_.clear())
      _ <- visited.get.map(_.clear())
      _ <- ff(println("NAME"))
      _ <- ff(println(DateTime.now()))
      actor <-  db.run(builder.findActorByName(actorName))
      _ <- ff(println(DateTime.now()))
      _ <- ff(println("NAME"))
      result <- actor.headOption.map(_.nconst).fold(personNotFoundError(actorName))(search)
    } yield {
      println("found")
      println(DateTime.now())
      println(result)
      result
    }
  }

}

object KevinBaconService {
  def apply[F[_]](db: DBCore[F], builder: QueryBuilder[F])(
    implicit F: ConcurrentEffect[F],
    ec: ExecutionContext
  ): F[KevinBaconService[F]] = {
    F.delay {
      new KevinBaconService(db, builder)
    }
  }
}
