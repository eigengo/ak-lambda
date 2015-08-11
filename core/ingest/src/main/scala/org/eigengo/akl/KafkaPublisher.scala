package org.eigengo.akl

import akka.actor.{Actor, ActorRef}
import com.softwaremill.react.kafka.{ConsumerProperties, ProducerProperties, ReactiveKafka}
import kafka.serializer.{StringDecoder, StringEncoder}

object KafkaPublisher {
  lazy val kafkaBrokers = System.getenv().getOrDefault("KAFKA_BROKERS", "192.168.59.104:32769")
  lazy val zookeeperHost = System.getenv().getOrDefault("ZOOKEEPER_HOST", "192.168.59.104:32768")
  lazy val kafka = new ReactiveKafka()

  lazy val producerProperties = ProducerProperties(
    brokerList = kafkaBrokers,
    topic = "ingest",
    clientId = "groupName",
    encoder = new StringEncoder()
  )

  val consumerProperties = ConsumerProperties(
    brokerList = kafkaBrokers,
    zooKeeperHost = zookeeperHost,
    topic = "ingest",
    groupId = "groupName",
    decoder = new StringDecoder()
  )

  lazy val producerActorProps = kafka.producerActorProps(producerProperties)
  lazy val consumerActorProps = kafka.consumerActorProps(consumerProperties)
}

trait KafkaPublisher {
  this: Actor ⇒

  lazy val publisher: ActorRef = context.actorOf(KafkaPublisher.consumerActorProps)
}
