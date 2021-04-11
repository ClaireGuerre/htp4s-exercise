package co.exercise

import cats.effect.{ExitCode, IO, IOApp}
import co.exercise.module.Server
import org.http4s.client.blaze.BlazeClientBuilder

import java.util.concurrent.Executors
import scala.concurrent.ExecutionContext

object App extends IOApp {
  def run(args: List[String]): IO[ExitCode] = {
    val resources = BlazeClientBuilder[IO](
      ExecutionContext.fromExecutorService(Executors.newCachedThreadPool)
    ).resource

    resources.use { _ =>
      Server.initialize.compile.lastOrError
    }
  }
}

