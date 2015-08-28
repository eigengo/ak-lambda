package org.eigengo.akl.analysis

import kafka.serializer.Decoder
import kafka.utils.VerifiableProperties

class MeasurementDecoder(props: VerifiableProperties = null) extends Decoder[String] {
  val encoding =
    if(props == null)
      "UTF8"
    else
      props.getString("serializer.encoding", "UTF8")

  def fromBytes(bytes: Array[Byte]): String = {
    new String(bytes, encoding)
  }
}