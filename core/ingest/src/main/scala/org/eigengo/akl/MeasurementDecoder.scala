package org.eigengo.akl

import akka.actor.{Props, Actor}
import akka.persistence.PersistentActor
import akka.util.ByteString

/**
 * Decoder CO
 */
object MeasurementDecoder {
  def props(deviceId: DeviceId): Props = Props(classOf[MeasurementDecoder], deviceId.id)
}

/**
 * Decodes data from the given device
 * @param deviceId the device identity
 */
class MeasurementDecoder(deviceId: DeviceId) extends PersistentActor {
  /** adds persistenceId method to DeviceId */
  private implicit class DeviceIdPersistenceId(deviceId: DeviceId) {
    def persistenceId(base: String) = s"$base-$deviceId"
  }

  override def receiveRecover: Receive = Actor.emptyBehavior

  override def receiveCommand: Receive = {
    case data: ByteString ⇒ println(s"Received $data from $deviceId")
  }

  override val persistenceId: String = deviceId.persistenceId("measurement-decoder")
}
