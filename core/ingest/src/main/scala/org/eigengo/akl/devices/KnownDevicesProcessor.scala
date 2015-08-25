package org.eigengo.akl.devices

import akka.actor._
import akka.persistence.PersistentActor
import org.eigengo.akl.{ClientId, KnownDevice, PersistentActorValidation, ValidationFailed}

import scalaz.{Failure, Success, Validation}

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

    def validate(source: KnownDevicesSource): Validation[ValidationFailed, Seq[KnownDevice]] = source match {
      case Single(kd) ⇒ Success(Seq(kd))
      case Csv(_, _ ) ⇒ Failure(ValidationFailed.NotImplementedYet)
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
