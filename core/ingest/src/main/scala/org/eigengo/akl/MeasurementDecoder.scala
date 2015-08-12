package org.eigengo.akl

import akka.actor.{Actor, Props}
import akka.persistence.PersistentActor
import akka.stream.ActorMaterializer
import akka.stream.actor.ActorPublisher
import akka.stream.scaladsl.{Sink, Source}
import akka.util.ByteString
import org.eignego.akl.RK

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
  def publish(value: String): Unit

  /** adds persistenceId method to DeviceId */
  private implicit class DeviceIdPersistenceId(deviceId: DeviceId) {
    def persistenceId(base: String) = s"$base-$deviceId"
  }

  override def receiveRecover: Receive = Actor.emptyBehavior

  override def receiveCommand: Receive = {
    case data: ByteString ⇒ publish(data.toString())
  }

  override val persistenceId: String = deviceId.persistenceId("measurement-decoder")
}

// TODO: This is not a great integration of Akka Streams!
class KafkaMeasurementDecoder(deviceId: DeviceId) extends AbstractMeasurementDecoder(deviceId) with ActorPublisher[String] {
  private implicit val materializer = ActorMaterializer()
  val publish = RK.publish(RK.producerProperties)(context.system)
  Source(ActorPublisher(self)).to(Sink(publish)).run()

  def publish(value: String): Unit = {
    //onNext(value)
  }
}
