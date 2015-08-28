package org.eigengo.akl.analysis

import kafka.serializer.StringDecoder
import org.apache.spark.SparkConf
import org.apache.spark.streaming._
import org.apache.spark.streaming.StreamingContext
import org.apache.spark.streaming.kafka.KafkaUtils
import scala.concurrent.duration._

object Main {
  def main(args: Array[String]) {
    // Create context with 2 second batch interval
    val sparkConf = new SparkConf().setAppName("DirectKafkaWordCount")
    val ssc = new StreamingContext(sparkConf, Seconds(2))

    // Create direct kafka stream with brokers and topics
    val topicsSet = Set("TODO")
    val brokers = "broker1-host:port"
    val kafkaParams = Map[String, String]("metadata.broker.list" -> brokers)

    // TODO: Define Type for the messages in kafka and implement the decoder
    val directKafkaStream = KafkaUtils.createDirectStream[String, Nothing, StringDecoder, MeasurementDecoder](
      ssc, kafkaParams, topicsSet)
  }
}
