package org.eigengo.akl.devices

import akka.actor.{ActorRef, ActorSystem, Props}
import akka.contrib.pattern.ClusterSharding
import akka.persistence.PersistentView
import org.eigengo.akl._

import scalaz.\/

/**
 * Companion for the known devices shard
 */
object KnownDevices {
  import akka.contrib.pattern.ShardRegion.{IdExtractor, ShardResolver}

  case class GetKnownDevice(clientId: ClientId, deviceId: DeviceId)

  def actorOf(system: ActorSystem): ActorRef = ClusterSharding(system).start(
      typeName = "known-devices-view",
      entryProps = Some(props),
      idExtractor = idExtractor,
      shardResolver = shardResolver)
  
  private val props: Props = Props[KnownDevices]

  /** Maps the message to the given actor identity. Here, we group by clientId. */
  private val idExtractor: IdExtractor = {
    case m@GetKnownDevice(clientId, deviceId)             ⇒ (clientId.toString, ClientGetKnownDevice(deviceId))
  }

  /** Evenly distributes the shards across the cluster. */
  private val shardResolver: ShardResolver = {
    case m@GetKnownDevice(_, _) ⇒ s"${m.clientId.hashCode() % 10}"
  }

  /** Gets the device configuration for the given device id. */
  private case class ClientGetKnownDevice(deviceId: DeviceId)

}

/**
 * Known devices actor groups the
 */
class KnownDevices extends PersistentView {
  import KnownDevices._
  import BadRobot._
  private var knownDevices: Map[DeviceId, DeviceConfiguration] = Map.empty

  private lazy val clientId = ClientId(self.path.name).!

  override lazy val viewId: String = s"known-devices-view-$clientId"

  override val persistenceId: String = "known-devices"

  override def receive: Receive = {
    case ClientGetKnownDevice(deviceId) ⇒
      sender() ! knownDevices.get(deviceId).fold[QueryFailed \/ DeviceConfiguration](\/.left(QueryFailed.NoSuchDeviceId(deviceId)))(\/.right)
    case KnownDevice(`clientId`, deviceId, configuration) ⇒
      knownDevices = knownDevices + (deviceId → configuration)
  }
}
