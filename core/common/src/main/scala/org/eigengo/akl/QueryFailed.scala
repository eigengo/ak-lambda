package org.eigengo.akl

trait QueryFailed
object QueryFailed {
  
  case class NoSuchDeviceId(deviceId: DeviceId) extends QueryFailed
  
}
