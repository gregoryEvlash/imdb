package com.imdb

import cats.effect.{ConcurrentEffect, Effect}

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

package object utils {
  implicit class EffectImpl[F[_]](F: Effect[F]) {

    def fromFuture[T](future: F[Future[T]])(implicit ec: ExecutionContext): F[T] =
      F.async { cb =>
        F.toIO(future).unsafeRunSync().onComplete {
          case Success(a) => cb(Right(a))
          case Failure(t) => cb(Left(t))
        }
      }

    def toFuture[T](f: F[T])(implicit  F: ConcurrentEffect[F]): Future[T] =
      F.toIO(f).unsafeToFuture()
  }
}
