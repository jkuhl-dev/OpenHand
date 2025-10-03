package com.jkuhldev.openhand.network

import android.util.Log
import com.jkuhldev.openhand.data.Printer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.apache.commons.net.ProtocolCommandEvent
import org.apache.commons.net.ProtocolCommandListener
import org.apache.commons.net.ftp.FTPFile
import org.apache.commons.net.ftp.FTPSClient
import org.apache.commons.net.util.TrustManagerUtils
import org.bouncycastle.jsse.BCExtendedSSLSession
import org.bouncycastle.jsse.BCSSLSocket
import org.bouncycastle.jsse.provider.BouncyCastleJsseProvider
import java.net.Socket
import java.security.SecureRandom
import java.util.Locale
import javax.net.ssl.SSLContext

/**
 * FTPS client for managing files on a connected printer
 */
class PrinterFilesClient() {
    private var client: FTPSClientWithSessionReuse? = null

    /**
     * Changes the current directory
     * @param targetDirectory Path to the directory we want to change to
     */
    suspend fun changeDirectory(targetDirectory: String) = withContext(Dispatchers.IO) {
        client?.changeWorkingDirectory(targetDirectory)
    }

    /**
     * Gets the current directory and returns it
     * @return String containing current directory
     */
    suspend fun getCurrentDirectory(): String = withContext(Dispatchers.IO) {
        client?.printWorkingDirectory() ?: ""
    }

    /**
     * Lists the contents of the current directory and returns it
     * @return List of FTPFile objects or null if unable
     */
    suspend fun listCurrentDirectory(): List<FTPFile> = withContext(Dispatchers.IO) {
        val c = client ?: return@withContext emptyList()

        c.enterLocalPassiveMode()
        val files = c.listFiles()
        c.enterLocalActiveMode()

        files.toList().sortedWith(
            compareByDescending<FTPFile> { it.isDirectory }
                .thenBy { it.name.lowercase(Locale.getDefault()) }
        )
    }

    /**
     * Starts the client
     * @param printer Printer we want the client to connect to
     */
    suspend fun start(printer: Printer) = withContext(Dispatchers.IO) {
        if (client != null) return@withContext

        // Initialize FTPS client
        val ftps = FTPSClientWithSessionReuse()

        // Configure listener to log messages to and from printer
        ftps.addProtocolCommandListener(object : ProtocolCommandListener {
            override fun protocolCommandSent(event: ProtocolCommandEvent) {
                Log.d(TAG, "--> ${event.message.trimEnd()}")
            }

            override fun protocolReplyReceived(event: ProtocolCommandEvent) {
                Log.d(TAG, "<-- [${event.replyCode}] ${event.message.trimEnd()}")
            }
        })

        // Connect to printer and log in
        ftps.connect(printer.ipAddress, 990)
        ftps.login("bblp", printer.accessCode)

        // Configure printer connection
        // https://en.wikipedia.org/wiki/List_of_FTP_commands
        ftps.opts("UTF8", "ON")
        ftps.execPBSZ(0)
        ftps.execPROT("P")

        // Store client reference
        client = ftps
    }

    /**
     * Stops the client
     */
    suspend fun stop() = withContext(Dispatchers.IO) {
        val c = client
        client = null
        if (c != null) {
            runCatching { c.logout() }
            runCatching { c.disconnect() }
        }
    }

    companion object {
        private const val TAG = "PrinterFilesClient"
    }
}

/**
 * Custom FTPSClient that reuses SSL sessions per modern FTPS standards
 * The stock Apache Commons FTPSClient does not provide this functionality out of the box
 * See: https://issues.apache.org/jira/browse/NET-408
 *      https://github.com/bcgit/bc-java/issues/458
 */
class FTPSClientWithSessionReuse() : FTPSClient(true, createSSLContext()) {
    private var sessionToResume: BCExtendedSSLSession? = null

    /**
     * Store the current session immediately after connecting
     */
    override fun _connectAction_() {
        super._connectAction_()
        sessionToResume = (_socket_ as? BCSSLSocket)?.bcSession ?: sessionToResume
    }

    /**
     * Intercept new sockets as they are created and set them to use the existing SSL session
     */
    override fun _prepareDataSocket_(socket: Socket?) {
        if (sessionToResume != null) {
            (socket as? BCSSLSocket)?.setBCSessionToResume(sessionToResume)
        }
    }

    companion object {
        /**
         * Creates an SSLContext that uses BouncyCastle for SSL
         */
        private fun createSSLContext(): SSLContext {
            System.setProperty("jdk.tls.allowLegacyResumption", "true")

            val sslContext = SSLContext.getInstance("TLS", BouncyCastleJsseProvider())
            sslContext.init(
                null,
                arrayOf(TrustManagerUtils.getValidateServerCertificateTrustManager()),
                SecureRandom()
            )
            return sslContext
        }
    }
}
