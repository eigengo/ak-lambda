package org.eigengo.akl

sealed trait SensorValue {
  type SensorLocation = String

  def location: SensorLocation
}

object SensorValue {
  case class Moisture(location: String, value: Double) extends SensorValue
  case class Temperature(location: String, value: Double) extends SensorValue
}
