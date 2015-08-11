package org.eigengo.akl

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.Source
import com.typesafe.config.ConfigFactory
import org.eignego.akl.{DevelopmentEnvironment, RK}

object Main extends App with DevelopmentEnvironment  {
  setup()
  
  implicit val system = ActorSystem("speed-rules", ConfigFactory.load("application.conf"))

  implicit val materializer = ActorMaterializer()

  Source(RK.subscribe(RK.consumerProperties)).runForeach(x ⇒ println(s"**** Got $x"))

//  val kafka = new ReactiveKafka()
//  val publisher = RK.subscribe(RK.consumerProperties)
//  val subscriber = RK.publish(RK.producerProperties)
//  Source(() ⇒ Seq("xx", "yy", "zz").iterator).map { x ⇒ println(x); x }.to(Sink(subscriber)).run()
//  Source(publisher).map { x ⇒ println(x); x.toUpperCase } .to(Sink(subscriber)).run()

  println("xxx")
}
