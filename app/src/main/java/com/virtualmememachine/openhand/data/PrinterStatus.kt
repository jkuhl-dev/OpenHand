package com.virtualmememachine.openhand.data

import org.json.JSONObject

/**
 * Model representing status data returned by the printer
 * @property connectionState ConnectionState value that describes the current connection to the printer
 * @property error String that describes an error, if one occurred.
 * @property lastUpdatedMillis Timestamp in milliseconds of the last status update.
 * @property bedTemperature Double that describes the current bed temperature
 * @property bedTargetTemperature Double that describes the target bed temperature
 * @property chamberTemperature Double that describes the current chamber temperature
 * @property chamberTargetTemperature Double that describes the target chamber temperature
 * @property layerCurrent Integer that describees the current layer number of the active print
 * @property layerTotal Integer that describes the total number of layers in the active print
 * @property nozzleTemperature Double that describes the current nozzle temperature
 * @property nozzleTargetTemperature Double that describes the target nozzle temperature
 * @property printName String containing the name of the active print
 * @property printProgress Integer from 0 to 100 that describes the progress of the active print
 * @property printTimeRemaining Integer that describes the number of minutes remaining on the active print
 * @property printerSerial String containing the serial number of the printer
 */
data class PrinterStatus(
    val connectionState: ConnectionState = ConnectionState.CONNECTING,
    val error: String? = null,
    val lastUpdatedMillis: Long? = null,
    val bedTemperature: Double? = null,
    val bedTargetTemperature: Double? = null,
    val chamberTemperature: Double? = null,
    val chamberTargetTemperature: Double? = null,
    val layerCurrent: Int? = null,
    val layerTotal: Int? = null,
    val nozzleTemperature: Double? = null,
    val nozzleTargetTemperature: Double? = null,
    val printName: String? = null,
    val printProgress: Int? = null,
    val printTimeRemaining: Int? = null,
    val printerSerial: String? = null,
) {
    companion object {

        /**
         * Creates a new PrinterStatus object using parsed JSON input
         * @param jsonInput The JSON string containing printer status data
         * @return PrinterStatus object constructed from the JSON input
         */
        fun fromJson(jsonInput: String?): PrinterStatus? {
            val jsonObject = JSONObject(jsonInput ?: "{}")

            // Reject JSON input that does not contain a 'print' key in the top level dictionary
            if (!jsonObject.has("print")) return null
            val p = jsonObject.optJSONObject("print") ?: return null

            return PrinterStatus(
                connectionState = ConnectionState.SUCCESS,
                error = null,
                lastUpdatedMillis = System.currentTimeMillis(),
                bedTemperature = p.optDouble("bed_temper")
                    .takeIf { !it.isNaN() },
                bedTargetTemperature = p.optDouble("bed_target_temper")
                    .takeIf { !it.isNaN() },
                chamberTemperature = p.optDouble("chamber_temper")
                    .takeIf { !it.isNaN() },
                chamberTargetTemperature = p.optDouble("chamber_target_temper")
                    .takeIf { !it.isNaN() },
                layerCurrent = p.optInt("layer_num", -1)
                    .takeIf { it >= 0 },
                layerTotal = p.optInt("total_layer_num", -1)
                    .takeIf { it >= 0 },
                nozzleTemperature = p.optDouble("nozzle_temper")
                    .takeIf { !it.isNaN() },
                nozzleTargetTemperature = p.optDouble("nozzle_target_temper")
                    .takeIf { !it.isNaN() },
                printName = p.optString("subtask_name")
                    .takeIf { it.isNotBlank() },
                printProgress = p.optInt("mc_percent", -1)
                    .takeIf { it >= 0 },
                printTimeRemaining = p.optInt("mc_remaining_time", -1)
                    .takeIf { it >= 0 },
                printerSerial = p.optJSONObject("upgrade_state")?.optString("sn")
                    ?.takeIf { it.isNotBlank() }
            )
        }
    }
}

val PREVIEW_PRINTER_STATUS = PrinterStatus(
    connectionState = ConnectionState.SUCCESS,
    error = null,
    lastUpdatedMillis = System.currentTimeMillis(),
    bedTemperature = 55.0,
    bedTargetTemperature = 55.0,
    chamberTemperature = 36.0,
    chamberTargetTemperature = null,
    layerCurrent = 14,
    layerTotal = 67,
    nozzleTemperature = 220.0,
    nozzleTargetTemperature = 220.0,
    printName = "Preview",
    printProgress = 20,
    printTimeRemaining = 117,
    printerSerial = "000000000000000"
)
