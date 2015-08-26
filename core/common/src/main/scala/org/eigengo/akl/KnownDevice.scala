package org.eigengo.akl

/**
 * Holds the data on a known device belonging to the given ``clientId``, identified by ``deviceId``,
 * with the specified ``configuration``.
 * 
 * @param clientId the client that "owns" the device
 * @param deviceId the device identifier
 * @param configuration the configuration
 */
case class KnownDevice(clientId: ClientId, deviceId: DeviceId, configuration: DeviceConfiguration)
