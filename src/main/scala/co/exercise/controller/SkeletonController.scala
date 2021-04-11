package co.exercise.controller

import cats.effect.{IO, Sync}
import io.circe.{Decoder, Encoder, Printer}
import org.http4s.circe.{jsonEncoderWithPrinterOf, jsonOf}
import org.http4s.{EntityDecoder, EntityEncoder, HttpRoutes}
import org.http4s.dsl.Http4sDsl

class SkeletonController extends Http4sDsl[IO] {

  val printer: Printer = Printer.noSpaces.copy(dropNullValues = true)

  implicit def circeEntityEncoder[F[_] : Sync, A: Encoder]: EntityEncoder[F, A] =
    jsonEncoderWithPrinterOf[F, A](printer)

  implicit def circeEntityDecoder[F[_] : Sync, A: Decoder]: EntityDecoder[F, A] = jsonOf[F, A]

  def routes: HttpRoutes[IO] = HttpRoutes.of[IO] {
    case GET -> Root / "exercise" / month / year => ????


  }
}