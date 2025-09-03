package com.virtualmememachine.openhand.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json

private val Context.printerDataStore by preferencesDataStore(name = "printers")
private val KEY_KNOWN_PRINTERS = stringPreferencesKey("known_printers")
private const val DEFAULT_KNOWN_PRINTERS = "{}"

/**
 * Data store for known printers
 * @param context Context where the data store is located
 */
class PrinterDataStore(private val context: Context) {

    /**
     * Flow for interacting with and monitoring the printer data store
     */
    val printersFlow: Flow<Map<String, Printer>> = context.printerDataStore.data.map { prefs ->
        Json.decodeFromString<LinkedHashMap<String, Printer>>(
            prefs[KEY_KNOWN_PRINTERS] ?: DEFAULT_KNOWN_PRINTERS
        )
    }

    /**
     * Adds or updates a known printer
     * @param printer Printer that should be added or updated
     */
    suspend fun addOrUpdatePrinter(printer: Printer) {
        context.printerDataStore.edit { prefs ->
            val printerMap = Json.decodeFromString<LinkedHashMap<String, Printer>>(
                prefs[KEY_KNOWN_PRINTERS] ?: DEFAULT_KNOWN_PRINTERS
            )
            printerMap[printer.ipAddress] = printer
            prefs[KEY_KNOWN_PRINTERS] = Json.encodeToString(printerMap)
        }
    }

    /**
     * Removes a known printer
     * @param printer Printer that should be removed
     */
    suspend fun removePrinter(printer: Printer?) {
        context.printerDataStore.edit { prefs ->
            val printerMap = Json.decodeFromString<LinkedHashMap<String, Printer>>(
                prefs[KEY_KNOWN_PRINTERS] ?: DEFAULT_KNOWN_PRINTERS
            )
            if (printerMap.remove(printer?.ipAddress) != null) {
                prefs[KEY_KNOWN_PRINTERS] = Json.encodeToString(printerMap)
            }
        }
    }
}
