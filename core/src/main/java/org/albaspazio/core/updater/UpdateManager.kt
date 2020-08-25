package org.albaspazio.core.updater


import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.util.Log
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.albaspazio.core.R
import org.albaspazio.core.accessory.isOnline
import org.albaspazio.core.ui.CustomProgressAlertDialog
import org.albaspazio.core.ui.show2ChoisesDialog
import org.json.JSONObject

class UpdateManager(private var activity: Activity,
                    private var updateXmlUrl: String,
                    private val onSuccess:(Int)-> Unit,
                    private val onError:(String)-> Unit,
                    private var timeOutMs:Int = 10000,
                    private var options:JSONObject? = null) {
    /*
     *   <update>
     *       <version>2222</version>
     *       <sver>0.2.25</version>
     *       <description>%s</description>
     *       <name>name</name>
     *       <url>http://192.168.3.102/android.apk</url>
     *   </update>
     */

    private val TAG                                     = "UpdateManager"

    private var packageName: String                     = activity.packageName

    private var isDownloading                           = false
    private var checkUpdateThread: CheckUpdateThread?   = null
    private var downloadApkThread: DownloadApkThread?   = null

    private lateinit var alertDialog:AlertDialog
    private val progressDialog = CustomProgressAlertDialog(activity)

    private val errors:HashMap<Int, String>                 = hashMapOf(
        Constants.NETWORK_ERROR         to activity.resources.getString(R.string.network_error),
        Constants.CONNECTION_ERROR      to activity.resources.getString(R.string.connection_error),
        Constants.VERSION_PARSE_FAIL    to activity.resources.getString(R.string.parse_error),
        Constants.REMOTE_FILE_NOT_FOUND to activity.resources.getString(R.string.remotefile_notfound_error),
        Constants.TIMEOUT_ERROR         to activity.resources.getString(R.string.timeout_error),
        Constants.UNKNOWN_ERROR         to activity.resources.getString(R.string.unknown_error),
        Constants.NETWORK_ABSENT         to activity.resources.getString(R.string.network_absent))



    private val mHandler: Handler = object : Handler() {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            when (msg.what) {
                Constants.VERSION_COMPARE_END   ->  onCompareEnd(msg.data)

                Constants.DOWNLOAD_CLICK_START  ->  downloadApk()
                Constants.DOWNLOAD_FINISH       ->  isDownloading = false

                Constants.VERSION_UP_TO_UPDATE,
                Constants.UPDATE_CANCELLED,
                Constants.NETWORK_ABSENT        ->  onSuccess(msg.what)

                Constants.NETWORK_ERROR         ->  onError(errors.get(Constants.NETWORK_ERROR)!!)
                Constants.CONNECTION_ERROR      ->  onError(errors.get(Constants.CONNECTION_ERROR)!!)
                Constants.VERSION_PARSE_FAIL    ->  onError(errors.get(Constants.VERSION_PARSE_FAIL)!!)
                Constants.REMOTE_FILE_NOT_FOUND ->  onError(errors.get(Constants.REMOTE_FILE_NOT_FOUND)!!)
                Constants.TIMEOUT_ERROR         ->  onError(errors.get(Constants.TIMEOUT_ERROR)!!)
                else                            ->  onError(errors.get(Constants.UNKNOWN_ERROR)!!)
            }
        }
    }

    // called by activity => VERSION_COMPARE_END => onCompareEnd :  upgrade or skip => downloadApk()
    fun checkUpdate() {

        if(!isOnline(activity)){
            mHandler.sendEmptyMessage(Constants.NETWORK_ABSENT)
            return
        }

        Log.d(TAG, "checkUpdate..")
        checkUpdateThread = CheckUpdateThread(activity, mHandler, packageName, updateXmlUrl, timeOutMs, options)

        GlobalScope.launch {
            (checkUpdateThread as CheckUpdateThread).run()
        }
    }

    // =====================================================================================================================================
    // =====================================================================================================================================
    // from handleMessage
    private fun onCompareEnd(ver:Bundle) {

        if (ver.getInt("localcode") < ver.getInt("remotecode")) {
            Log.d(TAG, activity.resources.getString(R.string.update_title))

            alertDialog = show2ChoisesDialog(activity,
                activity.resources.getString(R.string.update_title),
                activity.resources.getString(R.string.update_message, ver.getString("localver"),ver.getString("remotever"), ver.getString("description")),
                activity.resources.getString(R.string.update_update_btn),
                activity.resources.getString(R.string.no),
                {   // ok
                    alertDialog.dismiss()
                    mHandler.sendEmptyMessage(Constants.DOWNLOAD_CLICK_START)
                },                
                {   // cancel
                    alertDialog.dismiss()
                    mHandler.sendEmptyMessage(Constants.UPDATE_CANCELLED)
                })
        }
        else mHandler.sendEmptyMessage(Constants.VERSION_UP_TO_UPDATE)
    }

    // onCompareEnd -> USER CLICK
    private fun downloadApk() {
        Log.d(TAG, "downloadApk")

        progressDialog.show(activity.resources.getString(R.string.downloading),
                            activity.resources.getString(R.string.download_complete_neu_btn),
                            activity.resources.getString(R.string.update_cancel),
                            null,
                            downloadCancelOnClick)

        downloadApkThread = DownloadApkThread(activity, mHandler, progressDialog, checkUpdateThread!!.update)
        GlobalScope.launch {
            (downloadApkThread as DownloadApkThread).run()
        }
    }

    // USER CLICK => handleMessage => cancel new version's download
    private val downloadCancelOnClick = DialogInterface.OnClickListener { _: DialogInterface, _: Int ->
        downloadApkThread?.cancelBuildUpdate()
        progressDialog.dismiss()
        mHandler.sendEmptyMessage(Constants.UPDATE_CANCELLED)
    }
}