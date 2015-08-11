import sbt._

Build.Settings.root

name := "ak-lambda"

// Common code, but not protocols
lazy val common = project.in(file("common"))
  .settings(Build.Settings.project)

// Protocol code: messages in the system.
lazy val ingestProtocol = project.in(file("ingest-protocol"))
  .settings(Build.Settings.project)
  .dependsOn(common)

// Data ingestion code
lazy val ingest = project.in(file("ingest"))
  .settings(Build.Settings.project)
  .dependsOn(ingestProtocol, common)

lazy val speedProtocol = project.in(file("speed-protocol"))
  .settings(Build.Settings.project)
  .dependsOn(common)

lazy val speedRules = project.in(file("speed-rules"))
  .settings(Build.Settings.project)
  .dependsOn(speedProtocol, ingestProtocol, common)

lazy val speedFacts = project.in(file("speed-facts"))
  .settings(Build.Settings.project)
  .dependsOn(speedProtocol, common)

lazy val speed = project.in(file("speed"))
  .settings(Build.Settings.project)
  .dependsOn(speedProtocol, common, speedRules, speedFacts)

// lazy val output = project...

// The main aggregate
lazy val root = (project in file("."))
  .settings(Build.Settings.project)
  .aggregate(ingest, speed)

fork in Test := false

fork in IntegrationTest := false

parallelExecution in Test := false

publishLocal := {}

publish := {}
