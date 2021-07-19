package net

//import cats._
//import cats.Monad
import cats.effect._
import cats.implicits._
import cats.data._
import org.http4s._
import org.http4s.dsl.io._
import org.http4s.headers.Authorization
//import org.http4s.headers.Authorization
import org.http4s.server._
import org.http4s.implicits._
import org.http4s.server.blaze.BlazeServerBuilder

import scala.concurrent.ExecutionContext.global

//import scala.concurrent.ExecutionContext.Implicits.global
//import net.Test.routes

object BasicAuthExample extends IOApp {

  object AuthenticatedUser
  case class User(userName: String)

  object AnyError extends Error

  def validCredentials(credentials: BasicCredentials): Boolean = {
    credentials.username == "foo" && credentials.password == "bar"
  }

  def basicAuthCredentials(request: Request[IO]): Option[BasicCredentials] = {
    val header = request.headers.get(Authorization)
    header match {
      case Some(h) => basicAuthDecoder(h.value)
      case None => None
    }
  }

  def basicAuthDecoder(header: String): Option[BasicCredentials] = {
    val base64 = header.split(" ").last
    Some(BasicCredentials(base64))
  }

  val authUser: Kleisli[IO, Request[IO], Either[String,User]] =
    Kleisli(request => {
      val basicCredentials = basicAuthCredentials(request)
      basicCredentials match {
        case None                           => IO(Left("No Authorization header provided"))
        case Some(c) if validCredentials(c) => IO(Right(User(c.username)))
        case _                              => IO(Left("Invalid Authorization credentials"))
      }
    })

  val onFailure: AuthedRoutes[String, IO] =
    Kleisli(req => OptionT.liftF(Forbidden(req.context)))

  val middleware: AuthMiddleware[IO, User] =
    AuthMiddleware(authUser, onFailure)

  val serviceRoutes = HttpRoutes.of[IO] {
    case GET -> Root / "hello" / name =>
      Ok(s"Hello, $name")

    case GET -> Root / "yolo" / name =>
      Ok(s"YOLO, $name.")
  }

  val authedRoutes = AuthedRoutes.of[User, IO] {
    case GET -> Root / "secrets" as user =>
      Ok(s"Secrets! $user")

    case GET -> Root / "documents" as user =>
      Ok(s"Documents! $user")
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
