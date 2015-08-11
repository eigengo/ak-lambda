package org.eigengo.akl

import java.net.InetSocketAddress

import akka.actor.ActorSystem
import akka.io.{IO, Tcp}
import com.typesafe.config.ConfigFactory
import org.eignego.akl.DevelopmentEnvironment

object Main extends App with DevelopmentEnvironment {
  implicit val system = ActorSystem("ingest", ConfigFactory.load("application.conf"))

  val transport = IO(Tcp)
  val endpoint = system.actorOf(MeasurementsEndpoint.props)
  transport ! Tcp.Bind(endpoint, localAddress = new InetSocketAddress("0.0.0.0", 8080))

}
