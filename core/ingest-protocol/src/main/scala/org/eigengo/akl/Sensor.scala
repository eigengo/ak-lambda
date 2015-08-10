package org.eigengo.akl

sealed trait Sensor

object Sensor {
  case class Moisture(location: String) extends Sensor
  case class Temperature(location: String) extends Sensor
}
