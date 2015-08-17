import Dependencies._

Build.Settings.project

name := "speed-facts"

libraryDependencies ++= Seq(
  // Core Akka
  akka.actor,
  akka.cluster,
  akka.contrib,
  akka.persistence,
  akka.persistence_cassandra,
  akka.streams,
  akka.http.http,
  cassandra_driver,
  scalaz.core,
  reactiveKafka,
  // Testing
  scalatest % "test",
  scalacheck % "test"
)
