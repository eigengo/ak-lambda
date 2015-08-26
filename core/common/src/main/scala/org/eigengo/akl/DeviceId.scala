package org.eigengo.akl

import java.util.UUID

import akka.util.ByteString

import scalaz.{Validation, ValidationNel}

/**
 * Identifies device identifier
 * @param id the device identifier
 */
case class DeviceId(id: UUID) extends AnyVal
object DeviceId {

  def apply(data: String): ValidationNel[ValidationFailed, DeviceId] = {
    Validation.fromTryCatchNonFatal(DeviceId(UUID.fromString(data))).leftMap(_ ⇒ ValidationFailed.NotValidUUID(data)).toValidationNel
  }

  def apply(data: ByteString): ValidationNel[ValidationFailed, DeviceId] = {
    apply(data.utf8String.trim)
  }

}
