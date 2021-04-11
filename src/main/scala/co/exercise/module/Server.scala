package co.exercise.module

import cats.effect.{ConcurrentEffect, ContextShift, ExitCode, IO, Timer}
import co.exercise.controller.TestController
import fs2.Stream
import org.http4s.{Http, Response}
import org.http4s.server.Router
import org.http4s.server.blaze.BlazeServerBuilder
import org.http4s.server.middleware.CORS

import java.util.concurrent.Executors
import scala.concurrent.ExecutionContext
import scala.concurrent.ExecutionContext.global

object Server {
  def initialize(implicit F: ConcurrentEffect[IO], timer: Timer[IO]
  ): Stream[IO, ExitCode] = {

    implicit val cs: ContextShift[IO] = IO.contextShift(global)


    val controllers = List(
      new TestController
    )

    val router: IO[Http[IO, IO]] =
        IO(
          Router[IO](
            "/" -> Router[IO](controllers.map("/" -> _.routes): _*)

          ).mapF[IO, Response[IO]](x => x.value.map(_.getOrElse(Response.notFound[IO])))
        )


    val routerWithCORS: IO[Http[IO, IO]] = router.map(CORS(_))

    for {
      router <- Stream.eval[IO, Http[IO, IO]](routerWithCORS)
      exitCode <- BlazeServerBuilder(ExecutionContext.global)
        .withHttpApp(router)
        .withConnectorPoolSize(32)
        .withExecutionContext(
          ExecutionContext.fromExecutorService(Executors.newFixedThreadPool(32))
        )
        .serve
    } yield exitCode
  }
}

