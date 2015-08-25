package org.eigengo.akl

import java.nio.file.{Files, Paths}

import org.scalatest.{FlatSpec, Matchers}

import scalaz.{Failure, Success}

class KnownDevicesProcessorTest extends FlatSpec with Matchers {
  import org.eigengo.akl.devices.KnownDevicesProcessor._
  import RegisterDevices._

  def source(cpr: String): Array[Byte] = Files.readAllBytes(Paths.get(getClass.getResource(cpr).toURI))

  "Malformed CSV source" should "fail import" in {
    val Failure(Validation.MalformedCsvSource) = validate(KnownDevicesSource.Csv(ClientId.one, Array.empty))
  }

  "Valid CSV source" should "import listed devices" in {
    val Success(kds) = validate(KnownDevicesSource.Csv(ClientId.one, source("/devices-only.csv")))
    println(kds)

  }


}
