package org.eignego.akl

trait DevelopmentEnvironment {

  def setup(): Unit = {
    val defaults: Map[String, String] = {
      val dockerLocalHost = "192.168.59.103"
      Map(
        "CASSANDRA_JOURNAL_LOCAL_CPS" → dockerLocalHost,
        "CASSANDRA_JOURNAL_GLOBAL_CPS" → dockerLocalHost,
        "CASSANDRA_SNAPSHOT_LOCAL_CPS" → dockerLocalHost,
        "CASSANDRA_SNAPSHOT_GLOBAL_CPS" → dockerLocalHost,
        "KAFKA_BROKERS" → s"$dockerLocalHost:9092",
        "ZOOKEEPER_HOST" → s"$dockerLocalHost:2181"
      )
    }

    val props = System.getProperties
    defaults.foreach { case (k, v) ⇒ if (!System.getProperties.contains(k)) System.setProperty(k, v) }
  }

}
