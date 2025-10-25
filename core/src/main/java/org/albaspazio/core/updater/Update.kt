/* =================================================================================================
Part of android-core module

https://github.com/albaspazio/android-core

Author: Alberto Inuggi
Copyright (©) 2019-2023
==================================================================================================*/

package org.albaspazio.core.updater


import org.w3c.dom.Element
import org.w3c.dom.Text
import org.xml.sax.InputSource
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL
import javax.xml.parsers.DocumentBuilderFactory

data class Update(val xmlurl: String, val localversion:Int, val authenticationOptions: AuthenticationOptions){

    var version:Int         = 0
    var sver:String         = ""
    var apkurl:String       = ""
    var name:String         = ""
    var description:String  = ""

    fun readXml(timeOutMs: Int){

        val url = URL(xmlurl)
        val conn = url.openConnection() as HttpURLConnection
        
        // Set timeouts
        conn.connectTimeout = timeOutMs
        conn.readTimeout = timeOutMs
        conn.doInput = true
        
        // Set authentication if provided
        if(authenticationOptions.hasCredentials()) {
            conn.setRequestProperty("Authorization", authenticationOptions.encodedAuthorization)
        }
        
        // Set user agent to avoid some server blocks
        conn.setRequestProperty("User-Agent", "PsySuite-Android-Updater/1.0")
        
        try {
            conn.connect()
            
            // Check response code
            val responseCode = conn.responseCode
            if (responseCode != HttpURLConnection.HTTP_OK) {
                throw IOException("HTTP Error: $responseCode - ${conn.responseMessage}")
            }
            
            val isrc = InputSource(conn.inputStream)
            parseXml(isrc)
            
        } finally {
            conn.disconnect()
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