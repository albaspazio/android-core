/* =================================================================================================
Part of android-core module

https://github.com/albaspazio/android-core

Author: Alberto Inuggi
Copyright (©) 2019-2023
==================================================================================================*/

package org.albaspazio.core.updater

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Message
import org.json.JSONObject
import java.io.FileNotFoundException
import java.io.IOException
import java.net.ConnectException
import java.net.SocketTimeoutException

class CheckUpdateThread(
    private val mContext: Context,
    private val mHandler: Handler,
    private val updateXmlUrl: String,
    private val timeOutMs: Int,
    options: JSONObject?
) : Runnable {

    lateinit var update:Update
    private val TAG     = "CheckUpdateThread"
    private val authentication: AuthenticationOptions = AuthenticationOptions(options)

    override fun run() {

        val localver = UpdateManager.getVersionCodeLocal(mContext)
        update = Update(updateXmlUrl, localver.first, authentication)

        try {
            update.readXml(timeOutMs)
//            update.readXmlWithOkHttp(timeOutMs)

            val msg = Message()
            msg.what = Constants.VERSION_COMPARE_END

            val b = Bundle()
            b.putInt("localcode", localver.first)
            b.putString("localver", localver.second)
            b.putString("description", update.description)

            b.putInt("remotecode", update.version)
            b.putString("remotever", update.sver)
            msg.data = b

            mHandler.sendMessage(msg)

        } catch (e: SocketTimeoutException) {
            android.util.Log.e(TAG, "Timeout error: ${e.message}", e)
            mHandler.sendEmptyMessage(Constants.TIMEOUT_ERROR)
        } catch (e: FileNotFoundException) {
            android.util.Log.e(TAG, "File not found: ${e.message}", e)
//            mHandler.sendEmptyMessage(Constants.REMOTE_FILE_NOT_FOUND)
            mHandler.sendEmptyMessage(Constants.VERSION_UP_TO_UPDATE)
        } catch (e: ConnectException) {
            android.util.Log.e(TAG, "Connection error: ${e.message}", e)
            mHandler.sendEmptyMessage(Constants.CONNECTION_ERROR)
        } catch (e: IOException) {
            android.util.Log.e(TAG, "Network/IO error: ${e.message}", e)
            // Check if it's a 407 Proxy Authentication error
            if (e.message?.contains("407") == true) {
                android.util.Log.e(TAG, "HTTP 407 Proxy Authentication Required - check network proxy settings")
            }
            mHandler.sendEmptyMessage(Constants.NETWORK_ERROR)
        } catch (e: ParseException) {
            android.util.Log.e(TAG, "Parse error: ${e.message}", e)
            mHandler.sendEmptyMessage(Constants.VERSION_PARSE_FAIL)
        } catch (e: Exception) {
            android.util.Log.e(TAG, "Unexpected error: ${e.message}", e)
            mHandler.sendEmptyMessage(Constants.NETWORK_ERROR)
        }
    }
}