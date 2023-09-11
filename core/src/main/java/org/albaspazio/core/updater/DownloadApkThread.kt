/* =================================================================================================
Part of android-core module

https://github.com/albaspazio/android-core

Author: Alberto Inuggi
Copyright (©) 2019-2023
==================================================================================================*/

package org.albaspazio.core.updater

import android.content.Context
import android.os.Environment
import android.os.Handler
import org.albaspazio.core.ui.CustomProgressAlertDialog
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL


class DownloadApkThread(
    mContext: Context?,
    private val mHandler: Handler,
    private val mDownloadDialog: CustomProgressAlertDialog,
    var update: Update
) : Runnable {

    private val TAG = "DownloadApkThread"
    private val mSavePath: String   = "${Environment.getExternalStorageDirectory()}/download"
    private var progress            = 0
    private var cancelUpdate        = false
    private val downloadHandler: DownloadHandler = DownloadHandler(mContext!!, mDownloadDialog, mSavePath, update)

    override fun run() {
        downloadAndInstall()
    }

    fun cancelBuildUpdate() {
        cancelUpdate = true
    }

    private fun downloadAndInstall() {
        try {
            if (Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED) {
                val url = URL(update.apkurl)
                val conn: HttpURLConnection
                val length: Int
                try {
                    conn = url.openConnection() as HttpURLConnection
                    if (update.authenticationOptions.hasCredentials())    conn.setRequestProperty( "Authorization", update.authenticationOptions.encodedAuthorization)
                    conn.connect()
                    length = conn.contentLength
                } catch (e: Exception) {
                    mHandler.sendEmptyMessage(Constants.NETWORK_ERROR)
                    e.printStackTrace()
                    return
                }
                val `is`: InputStream
                `is` = try {
                    conn.inputStream
                } catch (e: IOException) {
                    mHandler.sendEmptyMessage(Constants.REMOTE_FILE_NOT_FOUND)
                    e.printStackTrace()
                    return
                }
                val file = File(mSavePath)
                if (!file.exists()) file.mkdir()

                val apkFile = File(mSavePath, update.name + ".apk")
                val fos     = FileOutputStream(apkFile)
                var count   = 0
                val buf     = ByteArray(1024)

                do {
                    val numread = `is`.read(buf)
                    count += numread
                    progress = (count.toFloat() / length * 100).toInt()
                    downloadHandler.updateProgress(progress)
                    downloadHandler.sendEmptyMessage(Constants.DOWNLOAD)
                    if (numread <= 0) {
                        downloadHandler.sendEmptyMessage(Constants.DOWNLOAD_FINISH)
                        mHandler.sendEmptyMessage(Constants.DOWNLOAD_FINISH)
                        break
                    }
                    fos.write(buf, 0, numread)
                } while (!cancelUpdate)
                fos.close()
                `is`.close()
            }
        } catch (e: MalformedURLException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}