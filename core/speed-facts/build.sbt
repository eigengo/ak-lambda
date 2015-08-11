import Dependencies._

Build.Settings.project

name := "speed-facts"

libraryDependencies ++= Seq(
  // Core Akka
  akka.actor,
  akka.cluster,
  akka.contrib,
  akka.persistence,
  akka.leveldb,
  scalaz.core,
  spray.can,
  spray.routing,
  spray.json,
  reactiveKafka,
  // Testing
  scalatest % "test",
  scalacheck % "test"
)
