package org.eigengo.akl

import akka.actor.Props
import akka.persistence.PersistentActor
import akka.stream.actor.ActorPublisher
import akka.util.ByteString
import kafka.producer.KafkaProducer
import org.eignego.akl.RK

/**
 * Decoder CO
 */
object MeasurementDecoder {
  def props(deviceId: DeviceId): Props = Props(classOf[KafkaMeasurementDecoder], deviceId.id)
}

object AbstractMeasurementDecoder {
  case class RawData(data: ByteString)
  case object PeerClosed
}

/**
 * Decodes data from the given device
 * @param deviceId the device identity
 */
abstract class AbstractMeasurementDecoder(deviceId: DeviceId) extends PersistentActor {
  import AbstractMeasurementDecoder._

  def publish(value: String): Unit

  private var buffer: ByteString = ByteString.empty

  /** adds persistenceId method to DeviceId */
  private implicit class DeviceIdPersistenceId(deviceId: DeviceId) {
    def persistenceId(base: String) = s"$base-$deviceId"
  }

  /** Checks that the given ``buffer`` is a completely decodeable */
  private def isComplete(buffer: ByteString): Boolean = buffer.length >= 1000

  /** Updates the current state with the new ``rawData``. Mutates ``buffer`` and makes side-effecting call to ``publish``. */
  private def receiveData(rawData: RawData): Unit = {
    buffer = buffer ++ rawData.data

    if (isComplete(buffer)) {
      // send out to Kafka
      publish(buffer.utf8String)

      // reset our state to be ready to receive more messages
      buffer = ByteString.empty
      // remove the journalled entries
      deleteMessages(lastSequenceNr, permanent = true)
    }
  }

  override def receiveRecover: Receive = {
    case r@RawData(_) ⇒ receiveData(r)
  }

  override def receiveCommand: Receive = {
    case chunk: ByteString ⇒ persistAsync(RawData(chunk))(receiveData)
    case PeerClosed        ⇒ // Peer disconnected, but we may have undecoded messages. What to do now?
  }

  override val persistenceId: String = deviceId.persistenceId("measurement-decoder")
}

// TODO: This is not a great integration of Akka Streams!
class KafkaMeasurementDecoder(deviceId: DeviceId) extends AbstractMeasurementDecoder(deviceId) with ActorPublisher[String] {
  val producer = new KafkaProducer(RK.producerProperties)

  def publish(value: String): Unit = {
    producer.send(value)
  }
}
