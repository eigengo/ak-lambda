package org.eigengo.akl

import java.util.UUID

/**
 * Identifies client
 * @param id the client identifier
 */
case class ClientId(id: UUID) extends AnyVal

object ClientId {
  val one = ClientId(UUID.fromString("B7A45781-B4DE-473A-99EB-20ED487DE8DE"))
}
