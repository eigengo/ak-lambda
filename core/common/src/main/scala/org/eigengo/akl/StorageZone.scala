package org.eigengo.akl

/**
 * Defines where the data ends up
 */
sealed trait StorageZone

/**
 * Values of StorageZone
 */
object StorageZone {
  /** US data centres */
  case object US extends StorageZone
  /** EU data centres */
  case object EU extends StorageZone
  /** Asia data centres */
  case object Asia extends StorageZone
}
