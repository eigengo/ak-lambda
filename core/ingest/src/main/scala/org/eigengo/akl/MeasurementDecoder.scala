package org.eigengo.akl

import akka.actor.{Actor, Props}
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

/**
 * Decodes data from the given device.
 * @param deviceId the device identity
 */
abstract class AbstractMeasurementDecoder(deviceId: DeviceId) extends Actor {

  def publish(value: String): Unit

  private var buffer: ByteString = ByteString.empty

  /** Checks that the given ``buffer`` is a completely decodeable */
  private def isComplete(buffer: ByteString): Boolean = buffer.length >= 1000

  /** Updates the current state with the new ``rawData``. Mutates ``buffer`` and makes side-effecting call to ``publish``. */
  private def receiveData(rawData: ByteString): Unit = {
    buffer = buffer ++ rawData

    if (isComplete(buffer)) {
      // send out to Kafka
      publish(buffer.utf8String)

      // reset our state to be ready to receive more messages
      buffer = ByteString.empty
    }
  }

  override def receive: Receive = {
    case chunk: ByteString ⇒ receiveData(chunk)
  }

}

// TODO: This is not a great integration of Akka Streams!
class KafkaMeasurementDecoder(deviceId: DeviceId) extends AbstractMeasurementDecoder(deviceId) with ActorPublisher[String] {
  val producer = new KafkaProducer(RK.producerProperties)

  def publish(value: String): Unit = {
    producer.send(value)
  }
}
