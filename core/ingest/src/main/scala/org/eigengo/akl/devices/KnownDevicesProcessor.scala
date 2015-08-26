package org.eigengo.akl.devices

import akka.actor._
import akka.persistence.PersistentActor
import org.eigengo.akl._
import scala.collection._

import scalaz.{NonEmptyList, Success, ValidationNel}

/**
 * Companion for the persistent actor
 */
object KnownDevicesProcessor {

  /** The KDP props */
  private val props: Props = Props[KnownDevicesProcessor]

  /** Given an ``ActorRefFactory``, make an ``ActorRef`` */
  def actorOf(arf: ActorRefFactory): ActorRef = arf.actorOf(props, "known-devices-processor")

  /** ADT for device source */
  sealed trait KnownDevicesSource
  case object KnownDevicesSource {

    /**
     * The devices for the given ``clientId`` are in a CSV represented by the ``source``.
     * @param clientId the client identity
     * @param source the bytes of a CSV file
     */
    case class Csv(clientId: ClientId, source: Array[Byte]) extends KnownDevicesSource

    /**
     * A single device to be added.
     * @param knownDevice the single fully constructed device
     */
    case class Single(knownDevice: KnownDevice) extends KnownDevicesSource
  }

  /**
   * Registers all devices in the given ``knownDevicesSource``.
   *
   * @param knownDevicesSource the source containing the devices
   */
  case class RegisterDevices(knownDevicesSource: KnownDevicesSource)

  /**
   * Companion for the device registration
   */
  object RegisterDevices {
    import KnownDevicesSource._

    /**
     * The given ``row`` at ``rowIndex`` cannot be imported, because of ``message``.
     *
     * @param row the row
     * @param rowNumber the row number
     * @param detail the failure detail
     */
    case class InvalidRowFormat(row: String, rowNumber: Int, detail: NonEmptyList[ValidationFailed]) extends ValidationFailed

    /**
     * Empty row
     */
    case object EmptyRow extends ValidationFailed

    /** Reads & validates the CSV file in ``data``, and constructs ``KnownDevice`` instances from it. */
    private def validateCsv(clientId: ClientId, data: Array[Byte]): ValidationNel[ValidationFailed, immutable.Seq[KnownDevice]] = {
      import scalaz.Validation.FlatMap._
      import scalaz.syntax.applicative._
      import scalaz.syntax.validation._

      /** Validates a single row from the CSV file. */
      def validateCsvRow(clientId: ClientId)(rowWithIndex: (Seq[String], Int)): ValidationNel[ValidationFailed, KnownDevice] = {
        val (row, rowNumber) = rowWithIndex
        if (row.isEmpty) InvalidRowFormat(row.mkString(","), rowNumber, NonEmptyList(EmptyRow)).failureNel
        else DeviceId(row.head).map { deviceId ⇒
          KnownDevice(clientId, deviceId, DeviceConfiguration.empty)
        }.leftMap { err ⇒ NonEmptyList(InvalidRowFormat(row.mkString(","), rowNumber, err)) }
      }

      val z = immutable.Seq.empty[KnownDevice].successNel[ValidationFailed]
      CsvParser(data).flatMap { rows ⇒
        val validatedRows = rows.zipWithIndex.map(validateCsvRow(clientId))
        validatedRows.foldLeft(z) { case (acc, v) ⇒ (acc |@| v)(_ :+ _) }
      }
    }

    def validate(source: KnownDevicesSource): ValidationNel[ValidationFailed, immutable.Seq[KnownDevice]] = source match {
      case Single(kd) ⇒ Success(immutable.Seq(kd))
      case Csv(c, d)  ⇒ validateCsv(c, d)
    }
  }

}

/**
 * Processes the device registration requests.
 */
class KnownDevicesProcessor extends PersistentActor with PersistentActorValidation {
  import KnownDevicesProcessor._
  import BadRobot._

  override def receiveRecover: Receive = Actor.emptyBehavior

  override def receiveCommand: Receive = {
    case RegisterDevices(source) ⇒ sender() ! RegisterDevices.validate(source).map(persist(_)(constUnit))
  }

  override val persistenceId: String = "known-devices"
}
