package org.albaspazio.core.accessory

import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.util.Log
import java.util.*


fun getTimeDifference(startdate:Date):Int{
    val now:Long = Date().time
    return (now - startdate.time).toInt()
}

fun getVersionName(c: Context): String? {
    val info = getPackageInfo(c) ?: return "?.?.?"
    return info.versionName
}

fun getPackageInfo(c: Context): PackageInfo? {
    val manager: PackageManager = c.getPackageManager()
    try {
        return manager.getPackageInfo(c.getPackageName(), 0)
    } catch (e: PackageManager.NameNotFoundException) {
        Log.e("Utils", "Couldn't find package information in PackageManager: $e")
    }
    return null
}

//fun String.isInt(){
//    if(this.isBlank())
//}