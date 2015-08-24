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

    case Tcp.PeerClosed ⇒
      context.stop(self)
  }

  private def invalidDevice(cause: ValidationFailed): Unit = {
    sender() ! Tcp.Close
    context.stop(self)
  }

  private def validDevice(deviceId: DeviceId): Unit = {
    // The following chunks are the device data to be decoded
    val decoder = context.actorOf(MeasurementDecoder.props(deviceId))
    context.become(connected(decoder))
  }

  override def receive: Receive = {
    case Tcp.Received(data) ⇒
      DeviceId.parse(data).fold(invalidDevice, validDevice)
  }

}
