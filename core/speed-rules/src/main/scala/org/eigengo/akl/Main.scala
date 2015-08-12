package org.eigengo.akl

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Sink, Source}
import com.typesafe.config.ConfigFactory
import org.eignego.akl.{DevelopmentEnvironment, RK}

object Main extends App with DevelopmentEnvironment  {
  setup()
  
  implicit val system = ActorSystem("speed-rules", ConfigFactory.load("application.conf"))

  implicit val materializer = ActorMaterializer()

  private def printStats: String ⇒ Unit = {
    var counter: Int = 0

    { (s: String) ⇒
      counter += 1
      if (counter % 1000 == 0) print(".")
      ()
    }
  }

  Source(RK.subscribe(RK.consumerProperties)).runForeach(printStats)

//  import scala.concurrent.duration._
//  for (x ← 0 to 100) {
//    Source(1.second, 10.microseconds, "aofhjaskjhfaksjfgalskjbgakdjfhgsdljhfgsdkjlfghsd").to(Sink(RK.publish(RK.producerProperties))).run()
//  }


//  val kafka = new ReactiveKafka()
//  val publisher = RK.subscribe(RK.consumerProperties)
//  val subscriber = RK.publish(RK.producerProperties)
//  Source(() ⇒ Seq("xx", "yy", "zz").iterator).map { x ⇒ println(x); x }.to(Sink(subscriber)).run()
//  Source(publisher).map { x ⇒ println(x); x.toUpperCase } .to(Sink(subscriber)).run()
}
