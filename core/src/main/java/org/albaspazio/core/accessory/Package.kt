package org.albaspazio.core.accessory

import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.util.Log


fun getVersionName(c: Context): String? {
    val info = getPackageInfo(c) ?: return "?.?.?"
    return info.versionName
}

fun getPackageInfo(c: Context): PackageInfo? {
    val manager: PackageManager = c.packageManager
    try {
        return manager.getPackageInfo(c.packageName, 0)
    } catch (e: PackageManager.NameNotFoundException) {
        Log.e("Utils", "Couldn't find package information in PackageManager: $e")
    }
    return null
}

//fun String.isInt(){
//    if(this.isBlank())
//}