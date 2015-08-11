package org.eigengo.akl

import akka.actor.{ActorRef, ActorSystem, Props}
import akka.persistence.journal.leveldb.{SharedLeveldbJournal, SharedLeveldbStore}
import com.typesafe.config.ConfigFactory

object Main extends App {
  implicit val system = ActorSystem("ingest", ConfigFactory.load("application.conf"))

}
