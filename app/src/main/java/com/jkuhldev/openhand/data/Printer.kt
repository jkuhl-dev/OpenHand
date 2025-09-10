package com.jkuhldev.openhand.data

import kotlinx.serialization.Serializable

/**
 * Model representing data for a single known printer
 * @property name Name of the printer
 * @property ipAddress IPv4 address of the printer
 * @property accessCode 8-character alphanumeric access code
 */
@Serializable
data class Printer(val name: String, val ipAddress: String, val accessCode: String)

val PREVIEW_PRINTER = Printer("Preview", "192.168.1.100", "00000000")
