package org.eigengo.akl.devices

import akka.actor._
import akka.persistence.PersistentActor
import org.eigengo.akl._

import scalaz.{ValidationNel, Failure, Success, Validation}

/**
 * Companion for the persistent actor
 */
object KnownDevicesProcessor {

  private val props: Props = Props[KnownDevicesProcessor]

  def actorOf(arf: ActorRefFactory): ActorRef = arf.actorOf(props, "known-devices-processor")

  sealed trait KnownDevicesSource
  case object KnownDevicesSource {
    case class Csv(clientId: ClientId, source: Array[Byte]) extends KnownDevicesSource
    case class Single(knownDevice: KnownDevice) extends KnownDevicesSource
  }

  case class RegisterDevices(knownDevicesSource: KnownDevicesSource)

  object RegisterDevices {
    import KnownDevicesSource._

    case class InvalidRowFormat(row: String, rowNumber: Int, message: String) extends ValidationFailed

    private def readKnownDevice(clientId: ClientId)(rowWithIndex: (Seq[String], Int)): ValidationNel[ValidationFailed, KnownDevice] = {
      import scalaz.syntax.validation._
      val (row, rowNumber) = rowWithIndex
      if (row.isEmpty) InvalidRowFormat(row.mkString(","), rowNumber, "Empty row").failureNel
      else for {
        deviceId ← DeviceId(row.head)
        configuration = DeviceConfiguration.empty
      } yield KnownDevice(clientId, deviceId, configuration)
    }

    private def readCsv(clientId: ClientId, data: Array[Byte]): ValidationNel[ValidationFailed, Seq[KnownDevice]] = {
      import scalaz.syntax.applicative._
      import scalaz.syntax.validation._
      import scalaz.Validation.FlatMap._

      val z: ValidationNel[ValidationFailed, Seq[KnownDevice]] = Seq.empty[KnownDevice].successNel[ValidationFailed]
      CsvParser(data).flatMap { rows ⇒
        val validatedRows = rows.zipWithIndex.map(readKnownDevice(clientId))
        validatedRows.foldLeft(z) { case (acc, v) ⇒ (acc |@| v)(_ :+ _) }
      }
    }

    def validate(source: KnownDevicesSource): ValidationNel[ValidationFailed, Seq[KnownDevice]] = source match {
      case Single(kd) ⇒ Success(Seq(kd))
      case Csv(c, d)  ⇒ readCsv(c, d)
    }
  }

}

/**
 * Receives
 */
class KnownDevicesProcessor extends PersistentActor with PersistentActorValidation {
  import KnownDevicesProcessor._

  override def receiveRecover: Receive = Actor.emptyBehavior

  override def receiveCommand: Receive = {
    case RegisterDevices(source) ⇒
      sender() ! RegisterDevices.validate(source).map(succ ⇒ persistAsync(succ)(constUnit))
  }

  override def persistenceId: String = "known-devices"
}
