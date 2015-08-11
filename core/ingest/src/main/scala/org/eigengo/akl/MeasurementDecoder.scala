package org.eigengo.akl

import akka.actor.{ActorRef, Props, Actor}
import akka.persistence.PersistentActor
import akka.util.ByteString
import com.softwaremill.react.kafka.ReactiveKafka

/**
 * Decoder CO
 */
object MeasurementDecoder {
  def props(deviceId: DeviceId): Props = Props(classOf[KafkaMeasurementDecoder], deviceId.id)
}

/**
 * Decodes data from the given device
 * @param deviceId the device identity
 */
abstract class AbstractMeasurementDecoder(deviceId: DeviceId) extends PersistentActor {
  def publisher: ActorRef

  /** adds persistenceId method to DeviceId */
  private implicit class DeviceIdPersistenceId(deviceId: DeviceId) {
    def persistenceId(base: String) = s"$base-$deviceId"
  }

  override def receiveRecover: Receive = Actor.emptyBehavior

  override def receiveCommand: Receive = {
    case data: ByteString ⇒
      println(s"Received $data from $deviceId")
      publisher ! data
  }

  override val persistenceId: String = deviceId.persistenceId("measurement-decoder")
}

class KafkaMeasurementDecoder(deviceId: DeviceId) extends AbstractMeasurementDecoder(deviceId) with KafkaPublisher

