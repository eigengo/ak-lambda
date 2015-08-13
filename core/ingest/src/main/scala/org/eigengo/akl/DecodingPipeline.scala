package org.eigengo.akl

import akka.actor.ActorSystem
import akka.stream.{SinkShape, UniformFanInShape, UniformFanOutShape, ActorMaterializer}
import akka.stream.scaladsl._
import akka.util.ByteString
import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils.ConfigFile
import com.typesafe.config.ConfigFactory
import org.eignego.akl.RK
import org.reactivestreams.Subscriber

import scala.concurrent.Future
import scala.util.{Failure, Success}

object DecodingPipeline extends App {
  implicit val system = ActorSystem("x", ConfigFactory.defaultReference())
  implicit val materializer = ActorMaterializer()
  import FlowGraph.Implicits._

  def sink1[A]: Sink[A, Future[Unit]] = Sink.foreach[A](x ⇒ println(s"Sink1 $x"))
  def sink2[A]: Sink[A, Future[Unit]] = Sink.foreach[A](x ⇒ println(s"Sink1 $x"))

  val gc = FlowGraph.closed() { implicit b ⇒
    val input = b.add(Flow[String])

    val out1 = b.add(Sink.foreach(println))
    val out2 = b.add(Sink.foreach(println))

    UniformFanInShape[String, String](input.outlet, out1, out2)
  }
  gc.run()


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
