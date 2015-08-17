import Dependencies._

Build.Settings.project

enablePlugins(sbtdocker.DockerPlugin)

name := "ingest"

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

docker <<= (docker dependsOn assembly)

// Define a Dockerfile
dockerfile in docker := {
  val jarFile = (assemblyOutputPath in assembly).value
  val appDirPath = "/app"
  val jarTargetPath = s"$appDirPath/${jarFile.name}"

  new Dockerfile {
    from("java")
    // Expose port 8080
    expose(8080)

    add(jarFile, jarTargetPath)
    workDir(appDirPath)
    entryPoint("java", "-jar", jarTargetPath)
  }
}

buildOptions in docker := BuildOptions(cache = false)
