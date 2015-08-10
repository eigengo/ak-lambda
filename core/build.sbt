import sbt._
import Keys._

name := "ak-lambda"

// Common code, but not protocols
lazy val common = project.in(file("common"))

// Protocol code: messages in the system.
lazy val ingestProtocol = project.in(file("ingest-protocol"))

// Data ingestion code
lazy val ingest = project.in(file("ingest"))
  .dependsOn(ingestProtocol, common)

lazy val speedProtocol = project.in(file("speed-protocol"))
  .dependsOn(common)

lazy val speedRules = project.in(file("speed-rules"))
  .dependsOn(speedProtocol, ingestProtocol, common)

lazy val speedFacts = project.in(file("speed-facts"))
  .dependsOn(speedProtocol, common)

lazy val speed = project.in(file("speed"))
  .dependsOn(speedProtocol, common, speedRules, speedFacts)

// lazy val output = project...

// The main aggregate
lazy val root = (project in file(".")).aggregate(ingest, speed)

fork in Test := false

fork in IntegrationTest := false

parallelExecution in Test := false

publishLocal := {}

publish := {}
