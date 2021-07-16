/*

package net

import cats._
import cats.data._
import org.http4s._
import EntityEncoder.stringEncoder
import org.http4s.server.AuthMiddleware
import cats.effect._
import cats.implicits._
import org.http4s._
import org.http4s.implicits._
import org.http4s.dsl.Http4sDsl
import org.http4s.server.blaze.BlazeServerBuilder
import org.http4s.util.CaseInsensitiveString
import scala.concurrent.duration._
import scala.language.higherKinds

object TestResponseHeader503 extends IOApp {

  sealed trait Error
  case object AnyError extends Error

  private def authUser[F[_]: Monad]: Kleisli[F, Request[F], Either[Error, Header]] = Kleisli { request: Request[F] =>
    val result: Option[Header] = request.headers.get(CaseInsensitiveString("Authorization"))
    result match {
      case None => Monad[F].pure(Left(AnyError))
      case Some(h) => Monad[F].pure(Right(h))
    }
  }

  private def onAuthFailure[F[_]: Monad]: AuthedService[Error, F] = Kleisli { req: AuthedRequest[F, Error]  =>
    object dsl extends Http4sDsl[F]
    import dsl._

    // Only apply authentication to "/welcome"
    req.req match {
      case GET -> Root / "welcome" =>
        req.authInfo match {
          case AnyError => OptionT.pure[F](
            Response[F](
              status = Status.Unauthorized
            )
          )
        }
      case _ => OptionT.none[F, Response[F]]
    }

  }

  private def authMiddleware[F[_]: Monad]: AuthMiddleware[F, Header] =
    AuthMiddleware(authUser, onAuthFailure)

  def authedService[F[_]: Monad]: AuthedService[Header, F] = {
    object dsl extends Http4sDsl[F]
    import dsl._
    AuthedService {
      case GET -> Root / "welcome" as header => Ok(s"Header is $header")
    }
  }

  def routes[F[_]: Monad: Sync]: HttpRoutes[F] =
    authMiddleware[F].apply(authedService[F])

  def slow[F[_] : Sync : Timer]: HttpRoutes[F] = 
    HttpRoutes.of {
      case _ => Timer[F].sleep(1.second) >> Sync[F].pure(Response[F](status = Status.Ok))
    }

  override def run(args: List[String]): IO[ExitCode] =
    BlazeServerBuilder[IO]
      .withHttpApp(slow[IO].orNotFound)
      .withResponseHeaderTimeout(5.milliseconds)
      .serve
      .compile
      .drain
      .map(_ => ExitCode.Success)

}

*/