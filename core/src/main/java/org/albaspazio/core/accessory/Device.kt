package org.albaspazio.core.accessory

import android.app.ActivityManager
import android.app.ActivityManager.MemoryInfo
import android.content.Context
import android.os.Build
import android.os.Build.VERSION
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

// to be called with  val device:Device = Device().setRam(requireContext())

@Parcelize
data class Device(
    val os:String           = VERSION.RELEASE,
    val device:String       = Build.DEVICE,
    val manufacturer:String = Build.MANUFACTURER,
    val model:String        = Build.MODEL,
    val id:String           = Build.ID,
    var totMemory:Int       = 0,    // [MByte]
    var freeMemory:Int      = 0     // [MByte]
) : Parcelable


fun Device.setRam(context: Context):Device{

    val actManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
    val memInfo     = MemoryInfo()
    actManager.getMemoryInfo(memInfo)
    this.totMemory  = ((memInfo.totalMem)/1024000).toInt()
    this.freeMemory = ((memInfo.availMem)/1024000).toInt()
    return this
}