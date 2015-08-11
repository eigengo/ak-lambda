package org.eignego.akl

import akka.actor.{Actor, ActorRef}
import com.softwaremill.react.kafka.{ConsumerProperties, ProducerProperties, ReactiveKafka}
import kafka.serializer.{StringDecoder, StringEncoder}


private[akl] object CommonKafka {
  // TODO: Move to Akka extension to avoid the gymnastics with ``System.getenv()...``
  private lazy val kafkaBrokers = System.getProperty("KAFKA_BROKERS")
  private lazy val zookeeperHost = System.getProperty("ZOOKEEPER_HOST")
  private lazy val kafka = new ReactiveKafka()

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

//  kafka.consume(consumerProperties)
}

trait KafkaPublisher {
  this: Actor ⇒

  lazy val publisher: ActorRef = context.actorOf(CommonKafka.consumerActorProps)
}
