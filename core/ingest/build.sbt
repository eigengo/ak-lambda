import Dependencies._

Build.Settings.project

name := "ingest"

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
  slf4j.slf4j_simple,
  // Testing
  scalatest % "test",
  scalacheck % "test"
)
