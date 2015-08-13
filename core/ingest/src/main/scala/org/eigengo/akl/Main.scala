package org.eigengo.akl

import java.net.InetSocketAddress

import akka.actor.ActorSystem
import akka.io.{IO, Tcp}
import com.typesafe.config.ConfigFactory
import org.eignego.akl.DevelopmentEnvironment

object Main extends App with DevelopmentEnvironment {
  setup()
  implicit val system = ActorSystem("ingest", ConfigFactory.load("application.conf"))

  val transport = IO(Tcp)
  val endpoint = system.actorOf(MeasurementsEndpoint.props)
  transport ! Tcp.Bind(endpoint, localAddress = new InetSocketAddress("0.0.0.0", 8080))

//  DecodingPipeline.server(system, "0.0.0.0", 8080)

//  implicit val materializer = ActorMaterializer()
//  import scala.concurrent.duration._
//  for (x ← 0 to 100) {
//    Source(1.second, 10.microseconds, "aofhjaskjhfaksjfgalskjbgakdjfhgsdljhfgsdkjlfghsd").to(Sink(RK.publish(RK.producerProperties))).run()
//  }

  //Source(() ⇒ Iterator("some such", "none at all")).to(Sink(RK.publish(RK.producerProperties))).run()

}
