package org.eigengo.akl

import java.util.UUID

import akka.util.ByteString

import scalaz.\/

/**
 * Identifies device identifier
 * @param id the device identifier
 */
case class DeviceId(id: UUID) extends AnyVal
object DeviceId {

  def parse(data: ByteString): ValidationFailed \/ DeviceId = {
    val id = data.utf8String.trim
    \/.fromTryCatchNonFatal(DeviceId(UUID.fromString(id))).leftMap(_ ⇒ ValidationFailed.NotValidUUID(id))
  }

}
