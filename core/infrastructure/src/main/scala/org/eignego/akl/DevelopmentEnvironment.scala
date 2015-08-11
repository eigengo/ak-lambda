package org.eignego.akl

trait DevelopmentEnvironment {

  def setup(): Unit = {
    val props = System.getProperties
    if (!props.containsKey("CASSANDRA_JOURNAL_CPS")) System.setProperty("CASSANDRA_JOURNAL_CPS", "192.168.59.103")
    if (!props.containsKey("CASSANDRA_SNAPSHOT_CPS")) System.setProperty("CASSANDRA_SNAPSHOT_CPS", "192.168.59.103")
  }

  setup()

}
