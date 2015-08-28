import sbt._
import Keys._

object Dependencies {

  object akka {
    val version = "2.3.12"
    // Core Akka
    val actor                 = "com.typesafe.akka"      %% "akka-actor"                    % version
    val cluster               = "com.typesafe.akka"      %% "akka-cluster"                  % version
    val contrib               = "com.typesafe.akka"      %% "akka-contrib"                  % version intransitive()
    val persistence           = "com.typesafe.akka"      %% "akka-persistence-experimental" % version intransitive()
    val persistence_cassandra = "com.github.krasserm"    %% "akka-persistence-cassandra"    % "0.3.9" intransitive()
    val streams               = "com.typesafe.akka"      %% "akka-stream-experimental"      % "1.0"

    object http {
      private val version = "1.0"
      val core     = "com.typesafe.akka" %% "akka-http-core-experimental"       % version
      val http     = "com.typesafe.akka" %% "akka-http-experimental"            % version
      val json     = "com.typesafe.akka" %% "akka-http-spray-json-experimental" % version
      val testkit = "com.typesafe.akka"  %% "akka-http-testkit-experimental"    % version
    }

    val testkit               = "com.typesafe.akka"      %% "akka-testkit"                  % version
  }

/*
  object spray {
    val version = "1.3.3"

    val can     = "io.spray" %% "spray-can"                    % version
    val routing = "io.spray" %% "spray-routing-shapeless2"     % version
    val client  = "io.spray" %% "spray-client"                 % version
    val json    = "io.spray" %%  "spray-json"                  % "1.3.1"

    val testkit = ("io.spray" %% "spray-testkit"            % version)
      .exclude("org.scalamacros", "quasiquotes_2.10.3")
  }
*/

  object scalaz {
    val core = "org.scalaz" %% "scalaz-core" % "7.1.2"
  }
//
//  object slf4j {
//    val version = "1.7.12"
//
//    val slf4j_simple     = "org.slf4j"              % "slf4j-simple" % version
//    val slf4j_api        = "org.slf4j"              % "slf4j-api"    % version
//  }
//
  val typesafeConfig   = "com.typesafe"           % "config"        % "1.3.0"

  val reactiveKafka    = "com.softwaremill"       %% "reactive-kafka" % "0.7.0"

  // Datastax Cassandra Client
  val cassandra_driver = "com.datastax.cassandra" % "cassandra-driver-core" % "2.1.7" //exclude("io.netty", "netty-transport") exclude("io.netty", "netty-handler") exclude("io.netty", "netty-codec")

  // Spark Client
  object spark {
    private val version = "1.4.1"
    val streaming_kafka = "org.apache.spark"      %% "spark-streaming-kafka" % version
    val streaming       = "org.apache.spark"      %% "spark-streaming"       % version
    val core            = "org.apache.spark"      %% "spark-core"            % version
  }

  // Testing
  val scalatest        = "org.scalatest"          %% "scalatest"      % "2.2.4"
  val scalacheck       = "org.scalacheck"         %% "scalacheck"     % "1.12.4"

}
