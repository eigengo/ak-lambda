package org.eigengo.akl.devices

import akka.persistence.PersistentView
import org.eigengo.akl.KnownDevice

object KnownDevices {
  import akka.contrib.pattern.ShardRegion.{IdExtractor, ShardResolver}

  private implicit class KnownDeviceShard(kd: KnownDevice) {
    val id: String    = kd.clientId.id.toString
    val shard: String = s"${kd.deviceId.hashCode() % 10}"
  }

  val idExtractor: IdExtractor = {
    case kd@KnownDevice(_, _, _) ⇒ (kd.id, kd)
  }

  val shardResolver: ShardResolver = {
    case kd@KnownDevice(_, _, _) ⇒ kd.shard
  }

}

class KnownDevices extends PersistentView {

  override def viewId: String = "known-devices-view"

  override def persistenceId: String = "known-devices"

  override def receive: Receive = {
    case KnownDevice(_, _, _) ⇒
  }
}
