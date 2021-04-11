package co.exercise.controller

import cats.effect.{IO, Sync}
import co.exercise.controller.model._
import io.circe.{Decoder, Encoder, Printer}
import org.http4s.circe.{jsonEncoderWithPrinterOf, jsonOf}
import org.http4s.dsl.Http4sDsl
import org.http4s.{EntityDecoder, EntityEncoder, HttpRoutes, ParseFailure}

class TestController extends Http4sDsl[IO] {

  val printer: Printer = Printer.noSpaces.copy(dropNullValues = true)

  implicit def circeEntityEncoder[F[_] : Sync, A: Encoder]: EntityEncoder[F, A] =
    jsonEncoderWithPrinterOf[F, A](printer)

  implicit def circeEntityDecoder[F[_] : Sync, A: Decoder]: EntityDecoder[F, A] = jsonOf[F, A]

  def routes: HttpRoutes[IO] = HttpRoutes.of[IO] {
    case GET -> Root / month / year =>
      for {
        m <- IO.fromEither(Month.fromString(month))
        y <- IO(Integer.getInteger(year))
        nbDays <- (m, y) match {
          case (January, _) => IO(31)
          case (March, _) => IO(31)
          case (May, _) => IO(31)
          case (July, _) => IO(31)
          case (August, _) => IO(31)
          case (October, _) => IO(31)
          case (December, _) => IO(31)
          case (April, _) => IO(30)
          case (June, _) => IO(30)
          case (September, _) => IO(30)
          case (November, _) => IO(30)
          case (February, y) => if (y % 4 == 0) IO(28) else IO(29)
        }
        response <- Ok(s"There are $nbDays days in $month $year")
      } yield response

  }
}