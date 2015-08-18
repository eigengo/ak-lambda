import Dependencies._

name := "common"

libraryDependencies ++= Seq(
  // Core Akka
  akka.actor,
  akka.persistence,
  scalaz.core
)
