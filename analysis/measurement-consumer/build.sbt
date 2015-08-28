import Dependencies._

Build.Settings.project

enablePlugins(sbtdocker.DockerPlugin)

name := "ingest"

libraryDependencies ++= Seq(
  // Core Akka
  spark.core,
  spark.streaming,
  spark.streaming_kafka,
  cassandra_driver,
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

    add(jarFile, jarTargetPath)
    workDir(appDirPath)
    entryPoint("java", "-jar", jarTargetPath)
  }
}

buildOptions in docker := BuildOptions(cache = false)
