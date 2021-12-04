package org.albaspazio.core.updater

import android.content.Context
import android.content.pm.PackageManager
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
            e.printStackTrace()
            mHandler.sendEmptyMessage(Constants.TIMEOUT_ERROR)
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
            mHandler.sendEmptyMessage(Constants.REMOTE_FILE_NOT_FOUND)
        } catch (e: ConnectException) {
            e.printStackTrace()
            mHandler.sendEmptyMessage(Constants.CONNECTION_ERROR)
        } catch (e: IOException) {
            e.printStackTrace()
            mHandler.sendEmptyMessage(Constants.NETWORK_ERROR)
        } catch (e: ParseException) {
            e.printStackTrace()
            mHandler.sendEmptyMessage(Constants.NETWORK_ERROR)
        }
    }
}