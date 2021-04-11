package co.exercise.controller.model

import org.http4s.ParseFailure

sealed trait Month {
  def name: String
}

object January extends Month {
  def name: String = "January"
}

object February extends Month {
  def name: String = "February"
}

object March extends Month {
  def name: String = "March"
}

object April extends Month{
  def name: String = "April"
}

object May extends Month{
  def name: String = "May"
}

object June extends Month{
  def name: String = "June"
}

object July extends Month {
  def name: String = "July"
}

object August extends Month{
  def name: String = "August"
}

object September extends Month{
  def name: String = "September"
}

object October extends Month {
  def name: String = "October"
}

object November extends Month {
  def name: String = "November"
}

object December  extends Month {
  def name: String = "December"
}

object Month {
  def fromString(monthStr: String): Either[ParseFailure, Month] = {
    monthStr.toLowerCase match {
      case "january" => Right(January)
      case "february" => Right(February)
      case "march" => Right(March)
      case "april" => Right(April)
      case "may" => Right(May)
      case "june" => Right(June)
      case "july" => Right(July)
      case "august" => Right(August)
      case "september" => Right(September)
      case "october" => Right(October)
      case "november" => Right(November)
      case "december" => Right(December)
      case (s) => Left(ParseFailure(s"wrong value $s", s))
    }
  }
}

