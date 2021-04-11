/*
HTTP4S

Tool for building purely functional API

Default web server: blaze

Calling .serve that returns a Stream[F, ExitCode] on ServerBuilder
will run the app until the jvm is killed

Server resource is started in the background
so the server will run until we cancel the fiber
*/

import cats.effect.{ContextShift, IO}
import org.http4s.server.blaze.BlazeServerBuilder
import org.http4s.Method.GET
import org.http4s.{Method, Request, Uri}
import org.http4s.circe.{JsonDecoder, jsonDecoder}
import org.http4s.dsl.Http4sDsl
import org.http4s.implicits.http4sKleisliResponseSyntaxOptionT

import scala.concurrent.ExecutionContext

/**
 * Quick recall:
 * 3 types of thread pool within an application
 * - a bounded thread pool (by the number of CPUSs) for CPU-intensive work
 * - an unbounded thread pool for executing blocking I/O calls
 * - a bounded thread pool for non-blocking I/O callbacks
 *
 * global is an execution context backed on a work stealing scheduler:
 * each processor in a computer system has a queue of work items
 * to perform. Each work item consists of a series of instructions,
 * to be executed sequentially, but in the course of its execution,
 * a work item may also spawn new work items that can feasibly be
 * executed in parallel with its other work.
 */

val global = ExecutionContext.global

/**
 * Implicits needed (provided by IOApp) to build a server:
 * ---------- ContextShift
 * ---------- Timer
 */

implicit val cs: ContextShift[IO] = IO.contextShift(global)
/**
 * What is that?
 *
 *
 * ContextShift is a pure representation of a threadpool:
 *
 * trait ContextShift[F[_]] {
 *
 *    def shift: F[Unit]
 *
 *    def evalOn[A](ec: ExecutionContext)(fa: F[A]): F[A]
 *
 *  }
 *
 * IOApp provides an instance which is backed by the default compute pool it provides.
 */

implicit val timer = IO.timer(global)
BlazeServerBuilder[IO](global).serve

/*
* Build an API with http4s = Construct HTTPRoutes[F]
*
* ----- What is HTTPRoutes[F]?
* an alias for Kleisli[OptionT[F, *], Request, Response] where:
* ----------- OptionT[F, *] is a monad transformer for F[Option[*]], which means OptionT[F, A] = F[Option[A]]
* ----------- Kleisli is a wrapper used to compose functions that return a monadic value
* Kleisli[F[_], A, B] wraps function A => F[B]
* At the end, this is a very formalized form to write the type of Request[F] => F[Option[Response[F]]] for any F
* */

import cats.data.Kleisli
val parse: Kleisli[Option,String,Int] =
  Kleisli((s: String) => if (s.matches("-?[0-9]+")) Some(s.toInt) else None)
println(parse("1"))
println(parse("?"))
/*
* ----------- F[_] is an effectful operation
*
* ---- How is it achieved?
* By pattern matching the requests
*/
import io.circe.Decoder
import io.circe.Encoder
import io.circe.Printer
implicit def http4sMethodSyntax(method: Method): Http4sDsl.MethodOps =
  new Http4sDsl.MethodOps(method)
import cats.effect.IO
import cats.effect.Sync
import org.http4s.circe.jsonEncoderWithPrinterOf
import org.http4s.circe.jsonOf
import org.http4s.dsl.Http4sDsl
import org.http4s.EntityDecoder
import org.http4s.EntityEncoder
import org.http4s.HttpRoutes
import io.circe.Json

class Controller extends Http4sDsl[IO] {

  val printer: Printer = Printer.noSpaces.copy(dropNullValues = true)

  implicit def circeEntityEncoder[F[_] : Sync, A: Encoder]: EntityEncoder[F, A] =
    jsonEncoderWithPrinterOf[F, A](printer)

  implicit def circeEntityDecoder[F[_] : Sync, A: Decoder]: EntityDecoder[F, A] = jsonOf[F, A]

  def routes: HttpRoutes[IO] = HttpRoutes.of[IO] {
    case GET -> Root / "salut" / name =>
      Ok(s"Salut $name")
    case GET -> Root / "bye" / name =>
      Ok(s"Bye $name")
  }
}

/**
 * HTTPRoutes can be tested without a server by constructing a request and call
 * service.orNotFound.run(request)
 */

val baseUrl = Uri.fromString("/").right.get
val request = Request[IO](method = GET, uri = baseUrl / "salut" / "Bibi")
val response = (new Controller).routes.orNotFound.run(request).unsafeRunSync()
println(response.as[Json].unsafeRunSync().as[String])

/**
 * Implicit EntityDecoder[T] is needed in order to return T object in the response
 */
















