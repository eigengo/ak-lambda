package org.eigengo.akl

import java.util.Date

/**
 * Measurement
 *
 * @param storageZone the desired storage zone
 * @param timestamp the timestamp
 * @param values the measured values
 */
case class Measurement(storageZone: StorageZone, timestamp: Date, values: Seq[SensorValue])
