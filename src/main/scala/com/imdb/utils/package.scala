package com.imdb

import cats.effect.{ConcurrentEffect, Effect}

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

package object utils {
  implicit class PimpedEffect[F[_]](F: Effect[F]) {

    def fromFuture[A](future: F[Future[A]])(implicit ec: ExecutionContext): F[A] =
      F.async { cb =>
        F.toIO(future).unsafeRunSync() onComplete {
          case Success(a) => cb(Right(a))
          case Failure(t) => cb(Left(t))
        }
      }

    def toFuture[A](f: F[A])(implicit  F: ConcurrentEffect[F]): Future[A] =
      F.toIO(f).unsafeToFuture()
  }
}
