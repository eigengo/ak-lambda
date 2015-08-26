package org.eigengo.akl

import java.util.UUID

import scalaz._

/**
 * Identifies client
 * @param id the client identifier
 */
case class ClientId(id: UUID) extends AnyVal {
  override def toString = id.toString
}

object ClientId {
  val one = ClientId(UUID.fromString("B7A45781-B4DE-473A-99EB-20ED487DE8DE"))

  def apply(data: String): ValidationNel[ValidationFailed, ClientId] = {
    Validation.fromTryCatchNonFatal(ClientId(UUID.fromString(data))).leftMap(_ ⇒ ValidationFailed.NotValidUUID(data)).toValidationNel
  }

}
