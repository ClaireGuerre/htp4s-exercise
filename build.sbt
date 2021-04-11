
name := "http4s-Exercise"

version := "0.1"

scalaVersion := "2.12.12"

lazy val versions = new {
  val cats = "1.6.0"
  val catsEffect = "2.4.1"
  val circe = "0.13.0"
  val http4s  = "0.21.21"
  val logback           = "1.2.3"
  val scalaLogging      = "3.9.3"
}

lazy val root = (project in file("."))
  // .enablePlugins(BuildInfoPlugin, JavaServerAppPackaging)
  .settings(
    Defaults.itSettings,
    libraryDependencies ++= Seq(
      "org.typelevel"                %% "cats-core"                       % versions.cats,
      "org.typelevel"                %% "cats-effect"                     % versions.catsEffect,
      "io.circe"                     %% "circe-generic"                   % versions.circe,
      "io.circe"                     %% "circe-parser"                    % versions.circe,
      "io.circe"                     %% "circe-generic-extras"            % versions.circe,
      "org.http4s"                   %% "http4s-blaze-client"             % versions.http4s,
      "org.http4s"                   %% "http4s-async-http-client"        % versions.http4s,
      "org.http4s"                   %% "http4s-blaze-server"             % versions.http4s,
      "org.http4s"                   %% "http4s-circe"                    % versions.http4s,
      "org.http4s"                   %% "http4s-dsl"                      % versions.http4s,
      "org.http4s"                   %% "http4s-prometheus-metrics"       % versions.http4s,
      "ch.qos.logback"                % "logback-classic"                 % versions.logback,
      "com.typesafe.scala-logging"   %% "scala-logging"                   % versions.scalaLogging,
    )
  )
