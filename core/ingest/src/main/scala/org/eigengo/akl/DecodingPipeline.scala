package org.eigengo.akl

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl._
import akka.util.ByteString

import scala.util.{Failure, Success}

object DecodingPipeline {

  def server(system: ActorSystem, address: String, port: Int): Unit = {
    implicit val sys = system
    import system.dispatcher
    implicit val materializer = ActorMaterializer()

    val handler = Sink.foreach[Tcp.IncomingConnection] { conn =>
      val topHeadSink = Sink.head[Int]
      val bottomHeadSink = Sink.head[Int]
      val sharedDoubler = Flow[Int].map(_ * 2)

      val x = FlowGraph.closed(topHeadSink, bottomHeadSink)((_, _)) { implicit builder =>
        (topHS, bottomHS) =>
        import FlowGraph.Implicits._
        val broadcast = builder.add(Broadcast[Int](2))
        Source.single(1) ~> broadcast.in

        broadcast.out(0) ~> sharedDoubler ~> topHS.inlet
        broadcast.out(1) ~> sharedDoubler ~> bottomHS.inlet
      }


      conn.handleWith(Flow[ByteString])
    }

    val connections = Tcp().bind(address, port)
    val binding = connections.to(handler).run()

    binding.onComplete {
      case Success(b) =>
        println("Server started, listening on: " + b.localAddress)
      case Failure(e) =>
        println(s"Server could not bind to $address:$port: ${e.getMessage}")
        system.shutdown()
    }

  }

}
