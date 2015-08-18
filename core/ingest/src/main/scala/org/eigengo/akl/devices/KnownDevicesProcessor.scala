package org.eigengo.akl.devices

import akka.actor.Actor
import akka.persistence.PersistentActor
import akka.util.ByteString
import org.eigengo.akl.{ClientId, KnownDevice, PersistentActorValidation, ValidationFailed}

import scalaz.{Failure, Success, Validation}

object KnownDevicesProcessor {

  sealed trait KnownDevicesSource
  case object KnownDevicesSource {
    case class Csv(clientId: ClientId, source: ByteString) extends KnownDevicesSource
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
    case RegisterDevices(source) ⇒ RegisterDevices.validate(source).fold(error ⇒ persistAsync(error)(constUnit), succ ⇒ persistAsync(succ)(constUnit))
  }

  override def persistenceId: String = "known-devices"
}
