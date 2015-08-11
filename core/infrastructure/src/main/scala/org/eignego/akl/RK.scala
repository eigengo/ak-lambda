package org.eignego.akl

import akka.actor.ActorSystem
import com.softwaremill.react.kafka.{ConsumerProperties, ProducerProperties, ReactiveKafka}
import kafka.serializer.{StringDecoder, StringEncoder}
import org.reactivestreams.{Publisher, Subscriber}

object RK {
  // TODO: Move to Akka extension to avoid the gymnastics with ``System.Property()...``
  private val kafkaBrokers = System.getProperty("KAFKA_BROKERS")
  private val zookeeperHost = System.getProperty("ZOOKEEPER_HOST")

  println(kafkaBrokers)

  val producerProperties = ProducerProperties(
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

  private lazy val kafka = new ReactiveKafka()
  def publish[A](props: ProducerProperties[A])(implicit as: ActorSystem): Subscriber[A] = kafka.publish(props)
  def subscribe[A](props: ConsumerProperties[A])(implicit as: ActorSystem): Publisher[A] = kafka.consume(props)

}
