name := "UrlShortner"

version := "0.1"

scalaVersion := "2.13.8"

val catsVersion = "2.6.1"
val loggingVersion = "3.9.4"
val zioVersion = "1.0.9"
val akkaHttpVersion = "10.1.12"
val akkaHttpCirceVersion = "1.32.0"
val scalaTestVersion = "3.2.9"
val circeVersion = "0.14.0"
val scalaCheckVersion = "1.15.4"
val rediscalaVersion = "1.9.0"

libraryDependencies := Seq(
  "org.typelevel" %% "cats-core" % catsVersion,
  "dev.zio" %% "zio" % zioVersion,
  "com.typesafe.akka" %% "akka-http" % akkaHttpVersion,
  "de.heikoseeberger" %% "akka-http-circe" % akkaHttpCirceVersion,
  "io.circe" %% "circe-core" % circeVersion,
  "io.circe" %% "circe-generic" % circeVersion,
  "io.circe" %% "circe-parser" % circeVersion,
  "com.github.etaty" %% "rediscala" % rediscalaVersion,
  "org.scalatest" %% "scalatest" % scalaTestVersion % Test,
  "org.scalacheck" %% "scalacheck" % scalaCheckVersion % Test

)