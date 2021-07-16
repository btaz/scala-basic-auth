package net

//import cats._
//import cats.Monad
import cats.effect._
import cats.implicits._
import cats.data._
import org.http4s._
import org.http4s.dsl.io._
//import org.http4s.headers.Authorization
import org.http4s.server._
import org.http4s.implicits._
import org.http4s.server.blaze.BlazeServerBuilder

import scala.concurrent.ExecutionContext.global

//import scala.concurrent.ExecutionContext.Implicits.global
//import net.Test.routes

object BasicAuth extends IOApp {

  case class User(id: Long, name: String)

  object AnyError extends Error

  val authUser: Kleisli[OptionT[IO, *], Request[IO], User] =
    Kleisli(_ => OptionT.liftF(IO(
      User(123L, "bob")
    )))

//  val authUser2: Kleisli[IO, Request[IO], Either[String,User]] = Kleisli({ request =>
//    IO(Right(User(123L, "bob")))
//    val header = request.headers.get(Authorization).toRight("Couldn't find an Authorization header")
//    header match {
//      case Left => IO.pure(AnyError)
//      case Right => IO.pure(Right(header))
//    }
//  })

  val middleware: AuthMiddleware[IO, User] =
    AuthMiddleware(authUser)

  val serviceRoutes = HttpRoutes.of[IO] {
    case GET -> Root / "hello" / name =>
      Ok(s"Hello, $name.")

    case GET -> Root / "yolo" / name =>
      Ok(s"YOLO, $name.")
  }

  val authedRoutes = AuthedRoutes.of[User, IO] {
    case GET -> Root / "secrets" as user =>
      Ok(s"Secrets! $user.name")

    case GET -> Root / "documents" as user =>
      Ok(s"Documents! $user.name")
  }

  val combinedRoutes = serviceRoutes <+> middleware(authedRoutes)

  override def run(args: List[String]): IO[ExitCode] =
    BlazeServerBuilder[IO](global)
      .bindHttp(8080, "localhost")
      .withHttpApp(combinedRoutes.orNotFound)
      .serve
      .compile
      .drain
      .as(ExitCode.Success)
}
