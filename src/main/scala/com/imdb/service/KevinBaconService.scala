package com.imdb.service

import cats.effect.ConcurrentEffect
import cats.effect.concurrent.Ref
import cats.implicits._
import com.imdb.db.{DBCore, QueryBuilder}
import com.imdb.models.domain.{IMDBServiceResponse, PersonNotFount}
import com.imdb.models.imdb.NCONST
import com.typesafe.scalalogging.LazyLogging

import scala.collection.JavaConverters._
import scala.concurrent.ExecutionContext

/*
  I agree with you that this impl and code little bit overcomplicated, and should be at least refactored.
  But i already tired and have no more time
 */

class KevinBaconService[F[_]](db: DBCore[F], builder: QueryBuilder[F])(
  implicit ec: ExecutionContext, F: ConcurrentEffect[F]
) extends SearchServiceUtil
    with LazyLogging with KevinBaconUtils[F] {

  val visited: Ref[F, Visited] = Ref.unsafe(new Visited())
  val q: Ref[F, Q] = Ref.unsafe(new Q())

  def search(target: String): F[IMDBServiceResponse] = {
    val initDegreeLevel = 0
    for{
      kevinColleagues <- findColleagues(Seq(kevinBaconId))
      result          <- checkIfContainsOtherwiseToQ(initDegreeLevel, target, kevinColleagues)(
        startProcessNewLayer(nextDegreeLevel(initDegreeLevel), target))
    } yield found(result, target)
  }

  def sixDegreesSearch(actorName: String): F[IMDBServiceResponse] =
    for{
      _      <- q.get.map(_.clear())
      _      <- visited.get.map(_.clear())
      actor  <- db.run(builder.findActorByName(actorName))
      result <- actor.headOption.map(_.nconst).fold(personNotFoundError(actorName))(search)
    } yield result

  private def personNotFoundError(name: String): F[IMDBServiceResponse] = F.pure(Left(PersonNotFount(name)))

  private def findColleagues(nconst: Seq[NCONST]): F[Seq[NCONST]] =  db.run(builder.colleagues(nconst))

  /*
    if que is empty or degree more than 6 means person is unreachable
   */
  private def checkOnLeftData(queue: Q, degree: Int)(orElse: => F[Int]): F[Int] = {
    if(queue.isEmpty || degree > 6)
      F.pure(notFoundDegree)
    else
      orElse
  }

  /*
    if the next BATCH users from queue are already visited we going on next iteration of batch,
    otherwise we call next function "performNextBatch"
   */
  private def checkVisitedNextBatch(queue: Q, visited: Visited, degree: Int)(f: => F[Int], orElse: List[String] => F[Int]): F[Int] = {
    val next = batch(queue, Nil, BATCH)
    val notVisited = next.filterNot(visited.containsKey)
    if (notVisited.isEmpty)
      f
    else
      orElse(notVisited)
  }

  /*
     mark as visited
     find their colleagues in DB
     check whether they contains target, if not put in the queue, and continue check next batch of colleagues on same level
   */
  private def performNextBatch(notVisited: List[String], visited: Visited, degree: Int, target: String, leftForNextLayer: Int): F[Int] = {
    for {
      _          <- F.delay(notVisited.foreach(visited.put(_, true)))
      colleagues <- findColleagues(notVisited)
      result     <- checkIfContainsOtherwiseToQ(nextDegreeLevel(degree) ,target, colleagues)(
                      checkLayerColleagues(leftForNextLayer - notVisited.size, degree, target))
    } yield result
  }

  /*
   if there is no colleagues on this degree left we go to the next level of graph,
   otherwise check next batch of colleagues
   */
  private def checkLayerColleagues(leftForNextLayer: Int, degree: Int, target: String): F[Int] = {
    if (leftForNextLayer <= 0)  // if target in layer not found go to second layer
      startProcessNewLayer(nextDegreeLevel(degree), target)
    else
      for {
        qf     <- q.get
        vf     <- visited.get
        result <- checkOnLeftData(qf, degree){
          checkVisitedNextBatch(qf, vf, degree)(
            checkLayerColleagues(leftForNextLayer - BATCH, degree, target),
            performNextBatch(_, vf, degree, target, leftForNextLayer)
          )
        }
      } yield result
  }

  private def startProcessNewLayer(degree: Int, target: String): F[Int] = {
    for{
      qf    <- q.get
      result <- checkLayerColleagues(qf.size(), degree, target)
    } yield result
  }

  /*
   check whether new set of colleagues contains target
   if yes return degree, otherwise put them to Q
   */

  private def checkIfContainsOtherwiseToQ(degree:Int, target: String, colleagues: Seq[String])(f: => F[Int]): F[Int] = {
    val notChecked = colleagues.toSet
    val contains = notChecked.contains(target)
    if(!contains) {
      for{
        _      <- q.get.map(_.addAll(notChecked.asJava))
        result <- f
      } yield result
    } else F.pure(degree)
  }

}

object KevinBaconService {
  def apply[F[_]](db: DBCore[F], builder: QueryBuilder[F])
                 (implicit F: ConcurrentEffect[F], ec: ExecutionContext): F[KevinBaconService[F]] =
    F.delay {
      new KevinBaconService(db, builder)
    }
}
