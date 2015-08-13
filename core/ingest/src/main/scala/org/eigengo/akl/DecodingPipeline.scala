package org.eigengo.akl

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl._
import akka.util.ByteString
import org.eignego.akl.RK
import org.reactivestreams.Subscriber

import scala.concurrent.Future
import scala.util.{Failure, Success}

object DecodingPipeline {

  def server(system: ActorSystem, address: String, port: Int): Unit = {
//
//    def graphFlow: Flow[ByteString, Unit, Unit] = ???
//
//    implicit val sys = system
//    import system.dispatcher
//    implicit val materializer = ActorMaterializer()
//
//    val publish: Subscriber[String] = RK.publish(RK.producerProperties)
//    val ks = Sink(publish)
//    val slowSink = Flow[ByteString].map(_.toString()).to(ks)
//
//    val fff = FlowGraph.closed() { implicit builder ⇒
//      val out = builder.add(ks)
//
//    }
//    fff
//    Flow[ByteString].via(Flow. RK.publish(RK.producerProperties))
//
//
//    val identitySink: Sink[Tcp.IncomingConnection, Future[Unit]] = Sink.foreach[Tcp.IncomingConnection](_.handleWith(Flow[ByteString]))
//
//    val connectionFlow: Flow[ByteString, ByteString, Unit] = Flow[ByteString]
//    val identitySink2: Sink[Tcp.IncomingConnection, Future[Unit]] = Sink.foreach[Tcp.IncomingConnection](_.handleWith(connectionFlow))
//    val printlnSink: Sink[Tcp.IncomingConnection, Future[Unit]] = Sink.foreach[Tcp.IncomingConnection] { conn ⇒
//      conn.flow.join(Flow[ByteString].map(_ ⇒ ())).run()
//    }
//
//    val connections = Tcp().bind(address, port)
//    val binding = connections.to(identitySink).run()
//
//    binding.onComplete {
//      case Success(b) =>
//        println("Server started, listening on: " + b.localAddress)
//      case Failure(e) =>
//        println(s"Server could not bind to $address:$port: ${e.getMessage}")
//        system.shutdown()
//    }

  }

}
