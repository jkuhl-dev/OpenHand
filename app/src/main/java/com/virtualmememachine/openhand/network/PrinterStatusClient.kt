package com.virtualmememachine.openhand.network

import android.content.Context
import com.virtualmememachine.openhand.data.ConnectionState
import com.virtualmememachine.openhand.data.Printer
import com.virtualmememachine.openhand.data.PrinterStatus
import info.mqtt.android.service.MqttAndroidClient
import info.mqtt.android.service.QoS
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.eclipse.paho.client.mqttv3.IMqttActionListener
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken
import org.eclipse.paho.client.mqttv3.IMqttToken
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended
import org.eclipse.paho.client.mqttv3.MqttConnectOptions
import org.eclipse.paho.client.mqttv3.MqttMessage
import java.security.SecureRandom
import java.security.cert.X509Certificate
import java.util.UUID
import javax.net.ssl.SSLContext
import javax.net.ssl.X509TrustManager

/**
 * MQTT client for retrieving printer status data
 * @param context Android context used for initializing the MQTT client
 * @param scope Scope used for launching background operations
 * @param printer Printer we want the client to connect to
 */
class PrinterStatusClient(
    private val context: Context,
    private val scope: CoroutineScope,
    private val printer: Printer
) {
    private var client: MqttAndroidClient? = null
    private val _status = MutableStateFlow(PrinterStatus())
    val status: StateFlow<PrinterStatus> = _status.asStateFlow()

    /**
     * Starts the client
     */
    fun start() {
        if (client != null) return

        // Initialize new MQTT client
        client = MqttAndroidClient(
            context = context,
            serverURI = "ssl://${printer.ipAddress}:8883",
            clientId = "openhand-${UUID.randomUUID()}"
        )

        // Configure MQTT client callbacks
        client?.setCallback(object : MqttCallbackExtended {
            /**
             * Callback triggered when MQTT connection was successful
             * @param reconnect Boolean that denotes if the connection was a reconnect or not
             * @param serverURI URI of the MQTT server
             */
            override fun connectComplete(reconnect: Boolean, serverURI: String?) {
                client?.subscribe("device/+/report", QoS.AtMostOnce.value)
            }

            /**
             * Callback triggered when MQTT connection is lost
             * @param cause Throwable containing error details
             */
            override fun connectionLost(cause: Throwable?) {
                _status.value = PrinterStatus(
                    connectionState = ConnectionState.ERROR,
                    error = cause?.toString()
                )
            }

            /**
             * Callback triggered when an MQTT message is received
             * @param topic MQTT topic the message was received on
             * @param message MQTT message data
             */
            override fun messageArrived(topic: String?, message: MqttMessage?) {
                val parsed = PrinterStatus.fromJson(message?.payload?.toString(Charsets.UTF_8))
                if (parsed != null) _status.value = parsed
            }

            /**
             * Callback triggered when an MQTT message is sent
             * @param token Token sent back from the MQTT server containing message details
             */
            override fun deliveryComplete(token: IMqttDeliveryToken?) {}
        })

        // Configure SSL to allow connections with unknown certificates
        val sslContext = SSLContext.getInstance("TLS")
        sslContext.init(
            null,
            arrayOf(object : X509TrustManager {
                override fun getAcceptedIssuers(): Array<X509Certificate> = arrayOf()
                override fun checkClientTrusted(chain: Array<X509Certificate>, authType: String) {}
                override fun checkServerTrusted(chain: Array<X509Certificate>, authType: String) {}
            }),
            SecureRandom()
        )

        // Attempt to connect to MQTT server
        client?.connect(
            options = MqttConnectOptions().apply {
                isAutomaticReconnect = true
                isCleanSession = true
                userName = "bblp"
                password = printer.accessCode.toCharArray()
                connectionTimeout = 5
                socketFactory = sslContext.socketFactory
            },
            userContext = null,
            callback = object : IMqttActionListener {
                /**
                 * Callback triggered when MQTT client successfully connected to server
                 * @param asyncActionToken Connection tracking token
                 */
                override fun onSuccess(asyncActionToken: IMqttToken?) {}

                /**
                 * Callback triggered when MQTT client fails to connect to server
                 * @param asyncActionToken Connection tracking token
                 * @param exception Exception containing failure details
                 */
                override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                    _status.value = PrinterStatus(
                        connectionState = ConnectionState.ERROR,
                        error = exception?.toString()
                    )
                }
            }
        )
    }

    /**
     * Stops the client
     */
    fun stop() {
        val c = client
        client = null
        if (c != null) {
            scope.launch(Dispatchers.IO) {
                runCatching { c.unregisterResources() }
                runCatching { c.close() }
                runCatching { c.disconnect() }
            }
        }
    }
}
