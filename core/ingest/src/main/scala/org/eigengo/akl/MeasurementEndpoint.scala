package org.eigengo.akl

import akka.actor.{ActorRef, Props, Actor}
import akka.io.Tcp
import akka.util.ByteString

/**
 * MP companion
 */
object MeasurementEndpoint {
  val props: Props = Props[MeasurementEndpoint]
}

/**
 * Receives TCP/UDP data
 */
class MeasurementEndpoint extends Actor {

  private def connected(decoder: ActorRef): Receive = {
    case Tcp.Received(data) ⇒
      decoder ! data
      sender() ! Tcp.Write(ByteString.empty)

    case Tcp.PeerClosed ⇒ context.stop(self)
  }

  override def receive: Receive = {
    case Tcp.Received(data) ⇒
      // The first payload is UTF-8 encoded data
      val deviceId = DeviceId(data.decodeString("UTF-8").trim)

      val decoder = context.actorOf(MeasurementDecoder.props(deviceId))
      context.become(connected(decoder))
      sender() ! Tcp.Write(ByteString.empty)
  }

}
