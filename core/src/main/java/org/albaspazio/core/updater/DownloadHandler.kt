/* =================================================================================================
Part of android-core module

https://github.com/albaspazio/android-core

Author: Alberto Inuggi
Copyright (©) 2019-2023
==================================================================================================*/

package org.albaspazio.core.updater

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.os.Message
import android.util.Log
import android.view.View
import androidx.core.content.FileProvider
import org.albaspazio.core.ui.CustomProgressAlertDialog
import java.io.File

class DownloadHandler(
    private val mContext: Context,
    private val mDownloadDialog: CustomProgressAlertDialog,
    private val mSavePath: String,
    private val update: Update
) : Handler() {

    private val TAG         = "DownloadHandler"
    private var progress    = 0

    override fun handleMessage(msg: Message) {
        when (msg.what) {
            Constants.DOWNLOAD ->           mDownloadDialog.updateProgress(progress)
            Constants.DOWNLOAD_FINISH -> {
                                            mDownloadDialog.onDownloadFinished(downloadCompleteOnClick)
                                            installApk()
            }
        }
    }

    fun updateProgress(progress: Int) {
        this.progress = progress
    }

    private val downloadCompleteOnClick = View.OnClickListener { installApk() }

    private fun installApk() {
        Log.d(TAG, "Installing APK")
        val apkFile = File(mSavePath, update.name + ".apk")
        if (!apkFile.exists()) {
            Log.e(TAG, "Could not find APK: " + update.name)
            return
        }
        Log.d(TAG, "APK Filename: $apkFile")

        // Intent APK
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Log.d(TAG, "Build SDK Greater than or equal to Nougat")
            val apkUri  = FileProvider.getUriForFile(mContext,"${mContext.applicationContext.packageName}.provider", apkFile)
            val i       = Intent(Intent.ACTION_INSTALL_PACKAGE)
            i.data      = apkUri
            i.flags     = Intent.FLAG_GRANT_READ_URI_PERMISSION
            mContext.startActivity(i)
        } else {
            Log.d(TAG, "Build SDK less than Nougat")
            val i = Intent(Intent.ACTION_VIEW)
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            i.setDataAndType(Uri.parse("file://$apkFile"), "application/vnd.android.package-archive")
            mContext.startActivity(i)
        }
    }
}

/*
the following code must be present in app manifest
    <provider
        android:name="androidx.core.content.FileProvider"
        android:authorities="${applicationId}.provider"
        android:exported="false"
        android:grantUriPermissions="true">
        <meta-data
            android:name="android.support.FILE_PROVIDER_PATHS"
            android:resource="@xml/provider_paths"/>
    </provider>
 */