package com.jkuhldev.openhand.network

import android.content.Context
import com.jkuhldev.openhand.R
import java.security.KeyStore
import java.security.SecureRandom
import java.security.cert.CertificateFactory
import java.security.cert.X509Certificate
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLSocketFactory
import javax.net.ssl.TrustManagerFactory

/**
 * Creates an SSLSocketFactory that trusts the Bambu CA certificate
 * @param context Android context used to access the Bambu CA certificate file
 * @return SSLSocketFactory configured to trust the Bambu CA certificate
 */
fun createSocketFactory(context: Context): SSLSocketFactory {
    context.resources.openRawResource(R.raw.bambu_ca_cert).use { caInput ->
        val bambuCaCert = CertificateFactory
            .getInstance("X.509")
            .generateCertificate(caInput) as X509Certificate

        // Create a KeyStore and add the Bambu CA certificate to it
        val keyStore = KeyStore.getInstance(KeyStore.getDefaultType())
        keyStore.load(null, null)
        keyStore.setCertificateEntry("bambuCA", bambuCaCert)

        // Create a TrustManager that trusts the KeyStore
        val trustManagerFactory =
            TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm())
        trustManagerFactory.init(keyStore)

        // Create an SSLContext that uses the TrustManager
        val sslContext = SSLContext.getInstance("TLS")
        sslContext.init(null, trustManagerFactory.trustManagers, SecureRandom())
        return sslContext.socketFactory
    }
}
