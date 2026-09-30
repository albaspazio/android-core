/* =================================================================================================
Part of android-core module

https://github.com/albaspazio/android-core

Author: Alberto Inuggi
Copyright (©) 2019-2023
==================================================================================================*/

package org.albaspazio.core.accessory

import android.app.ActivityManager
import android.app.ActivityManager.MemoryInfo
import android.content.Context
import android.os.Build
import android.os.Build.VERSION
import android.os.Parcelable
import android.os.Process
import android.os.SystemClock
import android.util.Log
import kotlinx.parcelize.Parcelize
import java.io.File

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
) : Parcelable {

    companion object {
        private var lastAppCpuTime: Long = 0L
        private var lastAppRealtime: Long = 0L

        /**
         * Gets the CPU usage of the current application process as a percentage (0-100%).
         * Uses Process.getElapsedCpuTime and SystemClock to calculate process CPU usage.
         * Returns 0.0 if calculation fails or data is unavailable.
         */
        @JvmStatic
        fun getAppCpuPercent(): Float {
            return try {
                val currentCpuTime = Process.getElapsedCpuTime()
                val currentRealtime = SystemClock.elapsedRealtime()

                val cpuDelta = currentCpuTime - lastAppCpuTime
                val timeDelta = currentRealtime - lastAppRealtime

                val previousRealtime = lastAppRealtime
                lastAppCpuTime = currentCpuTime
                lastAppRealtime = currentRealtime

                if (timeDelta > 0 && previousRealtime > 0) {
                    val numCores = Runtime.getRuntime().availableProcessors()
                    val cpuUsage = (cpuDelta.toFloat() / (timeDelta.toFloat() * numCores)) * 100.0f
                    cpuUsage.coerceIn(0.0f, 100.0f)
                } else {
                    0.0f
                }
            } catch (_: Exception) {
                0.0f
            }
        }

        /**
         * Gets the memory used by the current application process in MB.
         * Uses Runtime API to calculate app memory usage.
         * Returns 0.0 if calculation fails.
         */
        @JvmStatic
        fun getAppRamMb(): Float {
            return try {
                val runtime = Runtime.getRuntime()
                val totalMemory = runtime.totalMemory()
                val freeMemory = runtime.freeMemory()
                val usedMemory = totalMemory - freeMemory
                
                (usedMemory / (1024.0f * 1024.0f)).coerceAtLeast(0.0f)
            } catch (e: Exception) {
                Log.e("Device.getAppRamMb", "Error reading app RAM", e)
                0.0f
            }
        }

        /**
         * Gets the total CPU usage across all processes on the device as a percentage (0-100%).
         * Reads /proc/stat if accessible (on older Android versions or rooted devices).
         * Returns 0.0 if calculation fails or data is restricted by OS permissions.
         */
        @JvmStatic
        fun getDeviceCpuPercent(): Float {
            return try {
                val statFile = File("/proc/stat")
                if (!statFile.exists() || !statFile.canRead()) {
                    return 0.0f
                }
                
                val firstLine = statFile.readLines().firstOrNull() ?: return 0.0f
                val fields = firstLine.split("\\s+".toRegex()).drop(1)
                
                if (fields.size < 8) {
                    return 0.0f
                }
                
                val user = fields[0].toLongOrNull() ?: return 0.0f
                val nice = fields[1].toLongOrNull() ?: return 0.0f
                val system = fields[2].toLongOrNull() ?: return 0.0f
                val idle = fields[3].toLongOrNull() ?: return 0.0f
                val iowait = fields.getOrNull(4)?.toLongOrNull() ?: 0L
                val irq = fields.getOrNull(5)?.toLongOrNull() ?: 0L
                val softirq = fields.getOrNull(6)?.toLongOrNull() ?: 0L
                
                val activeTime = user + nice + system + iowait + irq + softirq
                val totalTime = activeTime + idle
                
                if (totalTime > 0) {
                    ((activeTime.toFloat() / totalTime.toFloat()) * 100.0f).coerceIn(0.0f, 100.0f)
                } else {
                    0.0f
                }
            } catch (_: Exception) {
                0.0f
            }
        }

        /**
         * Gets the total system RAM in MB using Android API.
         * Uses ActivityManager.MemoryInfo to retrieve total device memory.
         * Returns 0.0 if retrieval fails or context is null.
         */
        @JvmStatic
        fun getSystemRam(context: Context?): Float {
            if (context == null) return 0.0f
            return try {
                val actManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
                val memInfo = MemoryInfo()
                actManager.getMemoryInfo(memInfo)
                
                (memInfo.totalMem / (1024.0f * 1024.0f)).coerceAtLeast(0.0f)
            } catch (e: Exception) {
                Log.e("Device.getSystemRam", "Error reading system RAM", e)
                0.0f
            }
        }
    }
}

fun Device.getMemory(context: Context): Device {
    val actManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
    val memInfo = MemoryInfo()
    actManager.getMemoryInfo(memInfo)
    this.totMemory = ((memInfo.totalMem) / 1024000).toInt()
    this.freeMemory = ((memInfo.availMem) / 1024000).toInt()
    return this
}
