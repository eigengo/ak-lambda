package org.eigengo.akl

/**
 * A common supertype for all failed validations
 */
trait ValidationFailed

/**
 * Companion holding typical validation failures
 */
object ValidationFailed {
  /** It's not mean to work yet! */
  case object NotImplementedYet extends ValidationFailed
  /** The ``data`` is not a valid UUID string */
  case class NotValidUUID(data: String) extends ValidationFailed
}
