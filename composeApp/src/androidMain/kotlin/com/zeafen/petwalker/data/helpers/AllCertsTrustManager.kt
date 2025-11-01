package com.zeafen.petwalker.data.helpers

import java.security.cert.X509Certificate
import javax.net.ssl.X509TrustManager

class AllCertsTrustManager: X509TrustManager {
    override fun checkClientTrusted(
        p0: Array<out X509Certificate?>?,
        p1: String?
    ) {
        
    }

    override fun checkServerTrusted(
        p0: Array<out X509Certificate?>?,
        p1: String?
    ) {
        
    }

    override fun getAcceptedIssuers(): Array<out X509Certificate?>? = arrayOf()
}