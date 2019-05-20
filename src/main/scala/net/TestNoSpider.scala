package net

import cats._
import cats.data._
import org.http4s._
import EntityEncoder.stringEncoder
import org.http4s.server.AuthMiddleware
import cats.effect._
import org.http4s.implicits._
import org.http4s.dsl.Http4sDsl
import org.http4s.server.blaze.BlazeServerBuilder
import org.http4s.util.CaseInsensitiveString
import scala.language.higherKinds

object TestNoSpider extends IOApp {

  sealed trait Error
  case object AnyError extends Error

  private def authUser[F[_]: Monad]: Kleisli[OptionT[F, ?], Request[F], Header] = Kleisli { request: Request[F] =>
    val result: Option[Header] = request.headers.get(CaseInsensitiveString("Authorization"))
    result match {
      case None => OptionT.none[F, Header]
      case Some(h) => OptionT.pure[F](h)
    }
  }

  private def authMiddleware[F[_]: Sync]: AuthMiddleware[F, Header] =
    AuthMiddleware.noSpider(authUser[F],_ => Sync[F].pure(Response[F](status = Status.NotFound)))

  def authedService[F[_]: Monad]: AuthedService[Header, F] = {
    object dsl extends Http4sDsl[F]
    import dsl._
    AuthedService {
      case GET -> Root / "welcome" as header => Ok(s"Header is $header")
    }
  }

  def routes[F[_]: Monad: Sync]: HttpRoutes[F] =
    authMiddleware[F].apply(authedService[F])

  override def run(args: List[String]): IO[ExitCode] =
    BlazeServerBuilder[IO]
      .withHttpApp(routes[IO].orNotFound)
      .serve
      .compile
      .drain
      .map(_ => ExitCode.Success)

}