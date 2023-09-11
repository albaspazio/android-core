/* =================================================================================================
Part of android-core module

https://github.com/albaspazio/android-core

Author: Alberto Inuggi
Copyright (©) 2019-2023
==================================================================================================*/

package org.albaspazio.core.mail

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File

/*
in the manifest of the App using this library it must be defined this

        <provider
            android:name="androidx.core.content.FileProvider"
            android:authorities="iit.uvip.psysuite.provider"
            android:exported="false"
            android:grantUriPermissions="true">
            <meta-data
                android:name="android.support.FILE_PROVIDER_PATHS"
                android:resource="@xml/provider_paths"/>
        </provider>

in res/xml/provider_paths.xml

 <?xml version="1.0" encoding="utf-8"?>
<paths xmlns:android="http://schemas.android.com/apk/res/android">
    <external-path name="external_files" path="."/>
</paths>
 */


class MailIntent {

    companion object {

        fun composeEmail(
            activity: Activity,
            applicationID:String, // e.g. "iit.uvip.psysuite.provider" defined in the manifest of the App using this library
            addresses: Array<String> = arrayOf(),
            subject: String = "",
            body: String = "",
            attachments: List<String> = listOf()    // list of fullpaths
        ) {

            //convert from paths to Android friendly Parcelable Uri's
            val uris = ArrayList<Uri>()
            for (file in attachments) {
                val fileIn  = File(file)
                val u       = FileProvider.getUriForFile(activity, applicationID, fileIn)
                uris.add(u)
            }

            val intent = Intent(Intent.ACTION_SEND_MULTIPLE).apply {
                data = Uri.parse("mailto:") // only email apps should handle this
                type = "text/plain"
                putExtra(Intent.EXTRA_EMAIL, addresses)
                putExtra(Intent.EXTRA_SUBJECT, subject)
                putExtra(Intent.EXTRA_TEXT, body)
                putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris)
            }
            if (intent.resolveActivity(activity.packageManager) != null) {
                activity.startActivity(intent)
            }
        }
    }
}