/* =================================================================================================
Part of android-core module

https://github.com/albaspazio/android-core

Author: Alberto Inuggi
Copyright (©) 2019-2023
==================================================================================================*/

package org.albaspazio.core.mail

import java.io.File
import java.util.*
import javax.activation.CommandMap
import javax.activation.DataHandler
import javax.activation.FileDataSource
import javax.activation.MailcapCommandMap
import javax.mail.*
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeBodyPart
import javax.mail.internet.MimeMessage
import javax.mail.internet.MimeMultipart


// The code should run from AsynchTask or dedicated Thread

class Mail() : javax.mail.Authenticator() {

    // EMailAccount
    private var port:String     = "465"
    private var sport:String    = "465"
    private var host:String     = "smtp.googlemail.com"
    private var user:String     = ""
    private var password:String = ""
    private var from:String     = ""

    private val _auth = true
    private val _debuggable = false

    private var multipart: Multipart = MimeMultipart()


    constructor(_user: String, _pass: String, _from:String, _port:String = "465", _sport:String = "465", _host:String = "smtp.googlemail.com"):this(){
        setAccount(EMailAccount(_user, _pass, _from, _port, _sport, _host))
    }

    constructor(account:EMailAccount):this(){
        setAccount(account)
    }

    init {

        // There is something wrong with MailCap, javamail can not find a
        // handler for the multipart/mixed part, so this bit needs to be added.
        val mc: MailcapCommandMap = CommandMap.getDefaultCommandMap() as MailcapCommandMap
        mc.addMailcap("text/html;; x-java-content-handler=com.sun.mail.handlers.text_html")
        mc.addMailcap("text/xml;; x-java-content-handler=com.sun.mail.handlers.text_xml")
        mc.addMailcap("text/plain;; x-java-content-handler=com.sun.mail.handlers.text_plain")
        mc.addMailcap("multipart/*;; x-java-content-handler=com.sun.mail.handlers.multipart_mixed")
        mc.addMailcap("message/rfc822;; x-java-content-handler=com.sun.mail.handlers.message_rfc822")
        CommandMap.setDefaultCommandMap(mc)
    }

    fun setAccount(account:EMailAccount){

        user        = account.user
        password    = account.pass
        port        = account.port
        sport       = account.sport
        host        = account.host
        from        = account.from
    }

    private fun isValidEMail(to:Array<String>, subject:String, body:String):Boolean{
        return (user != "" && password != "" && to.isNotEmpty() && from != "" && subject != "" && body != "")
    }

    @Throws(Exception::class)
    fun send(to:Array<String>, subject:String, body:String, attachments:List<String> = listOf()): Boolean {
        val props = _setProperties()

        return if(isValidEMail(to, subject, body)){
            val session: Session    = Session.getInstance(props, this)
            val msg                 = MimeMessage(session)

            msg.setFrom(InternetAddress(from))

            val addressTo: Array<InternetAddress?> = arrayOfNulls<InternetAddress>(to.size)
            to.mapIndexed { index, s -> addressTo[index] = InternetAddress(s) }
            msg.setRecipients(MimeMessage.RecipientType.TO, addressTo)

            msg.subject = subject
            msg.sentDate = Date()

            // setup message body
            val messageBodyPart: BodyPart = MimeBodyPart()
            messageBodyPart.setText(body)
            multipart.addBodyPart(messageBodyPart)

            attachments.map{
                addAttachment(it)
            }

            // Put parts in message
            msg.setContent(multipart)

            // send email
            Transport.send(msg)
            true
        } else {
            false
        }
    }

    @Throws(Exception::class)
    fun addAttachment(filename: String) {
        val messageBodyPart: BodyPart           = MimeBodyPart()
        val source: javax.activation.DataSource = FileDataSource(filename)
        messageBodyPart.dataHandler             = DataHandler(source)
        messageBodyPart.fileName                = File(filename).name
        multipart.addBodyPart(messageBodyPart)
    }

    override fun getPasswordAuthentication(): PasswordAuthentication? {
        return PasswordAuthentication(user, password)
    }

    private fun _setProperties(): Properties {
        val props = Properties()
        props["mail.smtp.host"]                     = host
        props["mail.smtp.port"]                     = port
        props["mail.smtp.socketFactory.port"]       = sport
        props["mail.smtp.socketFactory.class"]      = "javax.net.ssl.SSLSocketFactory"
        props["mail.smtp.socketFactory.fallback"]   = "false"
        if (_debuggable)    props["mail.debug"]     = "true"
        if (_auth)          props["mail.smtp.auth"] = "true"

        return props
    }

}

data class EMailAccount(var user: String,
                        var pass: String,
                        var from:String,
                        var port:String     = "465",
                        var sport:String    = "465",
                        var host:String     = "smtp.googlemail.com"){

    fun isValid():Boolean{
        return (user.isNotBlank() && pass.isNotBlank() && from.isNotBlank() &&
                port.isNotBlank() && sport.isNotBlank() && host.isNotBlank())
    }
}