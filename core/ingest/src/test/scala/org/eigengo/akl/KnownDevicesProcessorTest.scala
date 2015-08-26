package org.eigengo.akl

import java.nio.file.{Files, Paths}
import java.util.UUID

import org.scalatest.{FlatSpec, Matchers}

import scalaz.{NonEmptyList, Failure, Success}

class KnownDevicesProcessorTest extends FlatSpec with Matchers {
  import org.eigengo.akl.devices.KnownDevicesProcessor._
  import RegisterDevices._
  import ValidationFailed._

  def source(cpr: String): Array[Byte] = Files.readAllBytes(Paths.get(getClass.getResource(cpr).toURI))

  "Malformed CSV source" should "fail import" in {
    val Failure(NonEmptyList(CsvParser.EmptyCsv)) = validate(KnownDevicesSource.Csv(ClientId.one, Array.empty))
  }

  "Invalid CSV source" should "fail import" in {
    val Failure(NonEmptyList(
      InvalidRowFormat("@0B771ED-AF36-4E25-820B-5ABFAC61627E", 0, NonEmptyList(NotValidUUID("@0B771ED-AF36-4E25-820B-5ABFAC61627E"))),
      InvalidRowFormat("#0B771ED-AF36-4E25-820B-5ABFAC61627E", 1, NonEmptyList(NotValidUUID("#0B771ED-AF36-4E25-820B-5ABFAC61627E")))
    )) = validate(KnownDevicesSource.Csv(ClientId.one, source("/bad-device-ids.csv")))
  }

  "Valid CSV source" should "import listed devices" in {
    val Success(List(kd)) = validate(KnownDevicesSource.Csv(ClientId.one, source("/one-device-only.csv")))
    kd.deviceId.id should be (UUID.fromString("A0B771ED-AF36-4E25-820B-5ABFAC61627E"))
  }

}
