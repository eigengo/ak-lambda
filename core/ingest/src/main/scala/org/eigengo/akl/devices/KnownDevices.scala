package org.eigengo.akl.devices

import akka.actor.{ActorRef, ActorSystem, Props}
import akka.contrib.pattern.ClusterSharding
import akka.persistence.PersistentView
import org.eigengo.akl.{QueryFailed, DeviceConfiguration, DeviceId, KnownDevice}

import scalaz.\/

/**
 * Companion for the known devices shard
 */
object KnownDevices {
  import akka.contrib.pattern.ShardRegion.{IdExtractor, ShardResolver}

  def actorOf(system: ActorSystem): ActorRef = ClusterSharding(system).start(
      typeName = "known-devices-view",
      entryProps = Some(props),
      idExtractor = idExtractor,
      shardResolver = shardResolver)
  
  private val props: Props = Props[KnownDevices]

  /** Maps the message to the given actor identity. Here, we group by clientId. */
  private val idExtractor: IdExtractor = {
    case kd@KnownDevice(clientId, deviceId, configuration) ⇒ (clientId.id.toString, ClientKnownDevice(deviceId, configuration))
  }

  /** Evenly distributes the shards across the cluster. */
  private val shardResolver: ShardResolver = {
    case kd@KnownDevice(_, _, _) ⇒ kd.shard
  }

  private implicit class KnownDeviceShard(kd: KnownDevice) {
    val shard: String = s"${kd.deviceId.hashCode() % 10}"
  }

  /** Add device */
  private case class ClientKnownDevice(deviceId: DeviceId, configuration: DeviceConfiguration)

  /** Gets the device configuration for the given device id. */
  private case class GetKnownDevice(deviceId: DeviceId)

}

/**
 * Known devices actor groups the
 */
class KnownDevices extends PersistentView {
  import KnownDevices._
  private var knownDevices: Map[DeviceId, DeviceConfiguration] = Map.empty

  override def viewId: String = "known-devices-view"

  override def persistenceId: String = "known-devices"

  override def receive: Receive = {
    case ClientKnownDevice(deviceId, configuration) ⇒
      knownDevices = knownDevices + (deviceId → configuration)
    case GetKnownDevice(deviceId) ⇒
      sender() ! knownDevices.get(deviceId).fold[QueryFailed \/ DeviceConfiguration](\/.left(QueryFailed.NoSuchDeviceId(deviceId)))(\/.right)
  }
}
