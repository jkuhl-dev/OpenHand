package com.jkuhldev.openhand.data

import androidx.compose.ui.graphics.Color
import org.json.JSONObject

/**
 * Model representing filament status data returned by the printer
 * @property id ID used to determine which slot the filament is loaded in
 * @property color Color of the filament or null if no filament is loaded
 * @property type String containing the filament type or null if no filament is loaded
 */
data class FilamentStatus(val id: String, val color: Color?, val type: String?)

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
 * @property loadedFilament List of FilamentStatus objects, one for each filament in the printer
 * @property nozzleTemperature Double that describes the current nozzle temperature
 * @property nozzleTargetTemperature Double that describes the target nozzle temperature
 * @property printName String containing the name of the active print
 * @property printProgress Integer from 0 to 100 that describes the progress of the active print
 * @property printTimeRemainingMinutes Integer that describes the number of minutes remaining on the active print
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
    val loadedFilament: List<FilamentStatus> = emptyList(),
    val nozzleTemperature: Double? = null,
    val nozzleTargetTemperature: Double? = null,
    val printName: String? = null,
    val printProgress: Int? = null,
    val printTimeRemainingMinutes: Int? = null,
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
                loadedFilament = loadFilamentStatus(
                    amsData = p.getJSONObject("ams"),
                    externalData = p.getJSONObject("vt_tray")
                ),
                nozzleTemperature = p.optDouble("nozzle_temper")
                    .takeIf { !it.isNaN() },
                nozzleTargetTemperature = p.optDouble("nozzle_target_temper")
                    .takeIf { !it.isNaN() },
                printName = p.optString("subtask_name")
                    .takeIf { it.isNotBlank() },
                printProgress = p.optInt("mc_percent", -1)
                    .takeIf { it >= 0 },
                printTimeRemainingMinutes = p.optInt("mc_remaining_time", -1)
                    .takeIf { it >= 0 },
                printerSerial = p.optJSONObject("upgrade_state")?.optString("sn")
                    ?.takeIf { it.isNotBlank() }
            )
        }

        /**
         * Creates a new list of FilamentStatus objects using parsed JSON input
         * @param amsData JSON object of data describing the current state of all connected AMS
         * @param externalData JSON object describing the current state of external filament tray
         * @return List of FilamentStatus objects, 1 for each filament tray
         */
        private fun loadFilamentStatus(
            amsData: JSONObject,
            externalData: JSONObject
        ): List<FilamentStatus> {
            val inputList = mutableListOf(externalData)
            val outputList = mutableListOf<FilamentStatus>()

            // Load all AMS tray data into inputList
            val connectedAms = amsData.optJSONArray("ams")
            if (connectedAms != null) {
                for (i in 0 until connectedAms.length()) {
                    val ams = connectedAms.optJSONObject(i) ?: continue
                    val trayData = ams.optJSONArray("tray") ?: continue
                    for (i in 0 until trayData.length()) {
                        val tray = trayData.optJSONObject(i) ?: continue
                        inputList.add(tray)
                    }
                }
            }

            // Attempt to parse input data to FilamentStatus objects
            inputList.forEach { tray ->
                val id = tray.optString("id") ?: return@forEach
                outputList.add(
                    FilamentStatus(
                        id = id,
                        color = parseColor(tray.optString("tray_color")),
                        type = tray.optString("tray_type").takeIf { it.isNotBlank() }
                    )
                )
            }

            return outputList.sortedWith(
                compareBy(
                    { it.id.toIntOrNull() ?: Int.MAX_VALUE },
                    { it.id })
            )
        }

        /**
         * Parses a Color from a color code string returned by the printer
         * @param colorCode String containing an 8 character HTML color code
         * @return Parsed Color or null if unable
         */
        private fun parseColor(colorCode: String?): Color? {
            if (colorCode == null) return null
            return try {
                Color(
                    red = colorCode.substring(0, 2).toInt(16),
                    green = colorCode.substring(2, 4).toInt(16),
                    blue = colorCode.substring(4, 6).toInt(16),
                    alpha = colorCode.substring(6, 8).toInt(16)
                )
            } catch (_: Exception) {
                null
            }
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
    loadedFilament = listOf(
        FilamentStatus("0", Color.Red, "PLA"),
        FilamentStatus("1", null, null),
        FilamentStatus("2", Color.Green, "ABS"),
        FilamentStatus("3", Color.Black, "PETG"),
        FilamentStatus("254", Color.White, "PLA")
    ),
    nozzleTemperature = 220.0,
    nozzleTargetTemperature = 220.0,
    printName = "Preview",
    printProgress = 20,
    printTimeRemainingMinutes = 117,
    printerSerial = "000000000000000"
)
