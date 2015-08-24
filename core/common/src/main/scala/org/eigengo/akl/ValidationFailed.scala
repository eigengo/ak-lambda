package org.eigengo.akl

trait ValidationFailed
object ValidationFailed {
  case object NotImplementedYet extends ValidationFailed
  case class NotValidUUID(data: String) extends ValidationFailed
}
