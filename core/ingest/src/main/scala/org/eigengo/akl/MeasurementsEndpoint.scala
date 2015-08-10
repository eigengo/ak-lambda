package org.eigengo.akl

import akka.actor.{Actor, Props}
import akka.io.Tcp

object MeasurementsEndpoint {
  val props: Props = Props[MeasurementsEndpoint]
}

/**
 * Accepts connections on TCP / UDP and establishes an actor that will handle
 * the data from the connected device
 */
class MeasurementsEndpoint extends Actor {
  
  override def receive: Receive = {
    case Tcp.Connected(_, _) ⇒
      sender() ! Tcp.Register(context.actorOf(MeasurementEndpoint.props))
  }
  
}
