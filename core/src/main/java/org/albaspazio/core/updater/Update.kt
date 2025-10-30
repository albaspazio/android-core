/* =================================================================================================
Part of android-core module

https://github.com/albaspazio/android-core

Author: Alberto Inuggi
Copyright (©) 2019-2023
==================================================================================================*/

package org.albaspazio.core.updater


import android.util.Log
import okhttp3.OkHttpClient
import okhttp3.Request
import org.w3c.dom.Element
import org.w3c.dom.Text
import org.xml.sax.InputSource
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL
import java.util.concurrent.TimeUnit
import javax.xml.parsers.DocumentBuilderFactory
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager
import okhttp3.ConnectionSpec
import okhttp3.TlsVersion
import java.security.cert.X509Certificate

data class Update(val xmlurl: String, val localversion:Int, val authenticationOptions: AuthenticationOptions){

    var version:Int         = 0
    var sver:String         = ""
    var apkurl:String       = ""
    var name:String         = ""
    var description:String  = ""

    fun readXmlWithOkHttp(timeOutMs: Int) {
        Log.d("UPDATE_DEBUG", "OkHttp connecting to URL: $xmlurl")

        try {
            val client = OkHttpClient.Builder()
                .connectTimeout(timeOutMs.toLong(), TimeUnit.MILLISECONDS)
                .readTimeout(timeOutMs.toLong(), TimeUnit.MILLISECONDS)
                .followRedirects(true)
                .followSslRedirects(true)
                .retryOnConnectionFailure(true)
                .build()

            val request = Request.Builder()
                .url(xmlurl)
                .addHeader("User-Agent", "curl/7.81.0")  // Use curl's user agent
                .addHeader("Accept", "*/*")
                .addHeader("Connection", "close")
                .build()

            Log.d("UPDATE_DEBUG", "Making request...")
            client.newCall(request).execute().use { response ->
                Log.d("UPDATE_DEBUG", "OkHttp response code: ${response.code}")
                Log.d("UPDATE_DEBUG", "Response headers: ${response.headers}")

                if (!response.isSuccessful) {
                    throw IOException("HTTP Error: ${response.code} - ${response.message}")
                }

                val responseBody = response.body?.string()
                Log.d("UPDATE_DEBUG", "Response body: $responseBody")

                val isrc = InputSource(responseBody?.byteInputStream())
                parseXml(isrc)
            }
        } catch (e: Exception) {
            Log.e("UPDATE_DEBUG", "OkHttp Error: ${e.javaClass.simpleName}: ${e.message}")
            Log.e("UPDATE_DEBUG", "Stack trace: ${e.stackTrace.joinToString("\n")}")
            throw e
        }
    }
    fun readXml(timeOutMs: Int){

        Log.d("UPDATE_DEBUG", "Connecting to URL: $xmlurl")

        try {
            val url = URL(xmlurl)
            val conn = url.openConnection() as HttpURLConnection

            // Set timeouts
            conn.connectTimeout = timeOutMs
            conn.readTimeout = timeOutMs
            conn.doInput = true

            // Set HTTP method explicitly
            conn.requestMethod = "GET"

            // Set headers that match working curl request
            conn.setRequestProperty("User-Agent", "PsySuite-Android-Updater/1.0")
            conn.setRequestProperty("Accept", "application/xml, text/xml, */*")
            conn.setRequestProperty("Connection", "close")

            // Set authentication if provided
            if(authenticationOptions.hasCredentials()) {
                conn.setRequestProperty("Authorization", authenticationOptions.encodedAuthorization)
            }

            // Don't follow redirects automatically - handle them manually
            conn.instanceFollowRedirects = false

            Log.d("UPDATE_DEBUG", "About to connect...")

            // Get response
            val responseCode = conn.responseCode
            Log.d("UPDATE_DEBUG", "Response code: $responseCode")

            when (responseCode) {
                HttpURLConnection.HTTP_OK -> {
                    Log.d("UPDATE_DEBUG", "Success! Reading XML...")
                    val isrc = InputSource(conn.inputStream)
                    parseXml(isrc)
                }
                HttpURLConnection.HTTP_MOVED_PERM,
                HttpURLConnection.HTTP_MOVED_TEMP,
                HttpURLConnection.HTTP_SEE_OTHER -> {
                    val newUrl = conn.getHeaderField("Location")
                    Log.d("UPDATE_DEBUG", "Redirect to: $newUrl")
                    conn.disconnect()
                    // Handle redirect manually if needed
                    throw IOException("Redirect not handled: $newUrl")
                }
                else -> {
                    throw IOException("HTTP Error: $responseCode - ${conn.responseMessage}")
                }
            }

            conn.disconnect()

        } catch (e: Exception) {
            Log.e("UPDATE_DEBUG", "Error: ${e.javaClass.simpleName}: ${e.message}")
            e.printStackTrace()
            throw e
        }
    }

    private fun parseXml(isrc:InputSource){
        try {
            val builderFactory = DocumentBuilderFactory.newInstance()
            val docBuilder = builderFactory.newDocumentBuilder()
            val doc = docBuilder.parse(isrc)
            val nList = doc.getElementsByTagName("update")
            val children = nList.item(0).childNodes

            for (i in 0 until children.length) {
                val item = children.item(i)
                if (item is Element) {
                    when (item.tagName) {
                        "url"           -> apkurl       = (item.childNodes.item(0) as Text).wholeText
                        "sver"          -> sver         = (item.childNodes.item(0) as Text).wholeText
                        "name"          -> name         = (item.childNodes.item(0) as Text).wholeText
                        "version"       -> version      = (item.childNodes.item(0) as Text).wholeText.toInt()
                        "description"   -> description  = (item.childNodes.item(0) as Text).wholeText
                    }
                }
            }
        }
        catch (e:Exception){
            throw e as ParseException
        }
    }
}

class ParseException(): Exception()