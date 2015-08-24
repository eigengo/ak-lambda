package org.eigengo.akl

import java.net.InetSocketAddress
import java.nio.file.{Paths, Files}

import akka.actor.ActorSystem
import akka.io.{IO, Tcp}
import akka.util.ByteString
import com.typesafe.config.ConfigFactory
import org.eigengo.akl.devices.KnownDevicesProcessor.{KnownDevicesSource, RegisterDevices}
import org.eigengo.akl.devices.{KnownDevices, KnownDevicesProcessor}
import org.eignego.akl.DevelopmentEnvironment

import scala.io.Source

object Main extends App with DevelopmentEnvironment {
  import collection.JavaConversions._
  setup()

  val config = ConfigFactory.load("application.conf")
  val ports = config.getIntList("akka.cluster.jvm-ports")
  ports.foreach(startup)

  def startup(port: Integer): Unit = {
    val config = ConfigFactory.load("application.conf")
    implicit val system = ActorSystem("ingest", ConfigFactory.parseString(s"akka.remote.netty.tcp.port=$port").withFallback(config))

    import akka.actor.ActorDSL._
    implicit val printlnSender = actor(new Act {
      become {
        case x ⇒ println(">>>> " + x)
      }
    })

    val transport = IO(Tcp)
    val endpoint = system.actorOf(MeasurementsEndpoint.props)
    transport ! Tcp.Bind(endpoint, localAddress = new InetSocketAddress("0.0.0.0", 8080 + port))

    val dp = KnownDevicesProcessor.actorOf(system)
    val ds = KnownDevices.actorOf(system)

    //dp ! RegisterDevices(KnownDevicesSource.Csv(ClientId.one, Files.readAllBytes(Paths.get(Main.getClass.getResource("/devices-1.csv").toURI))))
  }
}
