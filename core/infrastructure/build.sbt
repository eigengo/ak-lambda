import Dependencies._

name := "infrastructure"

libraryDependencies ++= Seq(
  // Core Akka
  akka.actor,
  reactiveKafka,
  // Testing
  scalatest % "test",
  scalacheck % "test"
)
