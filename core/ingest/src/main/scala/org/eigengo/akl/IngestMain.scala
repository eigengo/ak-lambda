package org.eigengo.akl

import java.net.InetSocketAddress

import akka.actor.{Props, ActorRef, ActorSystem}
import akka.io.{IO, Tcp}
import akka.persistence.journal.leveldb.{SharedLeveldbStore, SharedLeveldbJournal}
import com.typesafe.config.ConfigFactory

object IngestMain extends App {
  private var store: Option[ActorRef] = None
  implicit val system = ActorSystem("ingest", ConfigFactory.load("ingest.conf"))

  def startupJournal(): Unit = {
    def getOrStartStore(system: ActorSystem): ActorRef = store.getOrElse {
      val ref = system.actorOf(Props[SharedLeveldbStore], "store")
      store = Some(ref)
      ref
    }

    SharedLeveldbJournal.setStore(getOrStartStore(system), system)
  }

  startupJournal()

  val transport = IO(Tcp)
  val endpoint = system.actorOf(MeasurementsEndpoint.props)
  transport ! Tcp.Bind(endpoint, localAddress = new InetSocketAddress("0.0.0.0", 8080))

}
