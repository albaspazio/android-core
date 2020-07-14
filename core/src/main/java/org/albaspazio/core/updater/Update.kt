package org.albaspazio.core.updater


import org.w3c.dom.Element
import org.w3c.dom.Text
import org.xml.sax.InputSource
import java.net.HttpURLConnection
import java.net.URL
import javax.xml.parsers.DocumentBuilderFactory

data class Update(val xmlurl: String, val localversion:Int, val authenticationOptions: AuthenticationOptions){

    var version:Int = 0
    var sver:String = ""
    var apkurl:String = ""
    var name:String = ""

    fun readXml(timeOutMs: Int){

        val url = URL(xmlurl)
        val isrc = if(authenticationOptions.hasCredentials()) {
                val conn    = url.openConnection() as HttpURLConnection //利用HttpURLConnection对象,我们可以从网络中获取网页数据.
                conn.setRequestProperty("Authorization", authenticationOptions.encodedAuthorization)

                conn.connectTimeout = timeOutMs
                conn.doInput        = true
                conn.connect()
                conn.inputStream as InputSource
        }
        else    InputSource(url.openStream())

        parseXml(isrc)
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
                        "url" -> apkurl = (item.childNodes.item(0) as Text).wholeText
                        "sver" -> sver = (item.childNodes.item(0) as Text).wholeText
                        "name" -> name = (item.childNodes.item(0) as Text).wholeText
                        "version" -> version = (item.childNodes.item(0) as Text).wholeText.toInt()
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