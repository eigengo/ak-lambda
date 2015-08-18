package org.eigengo.akl

/**
 * Holds the configuration for the connected device
 * @param services the services the device provides
 */
// TODO: This is just a skeleton, consider *proper* configuration
case class DeviceConfiguration(services: Seq[String])
