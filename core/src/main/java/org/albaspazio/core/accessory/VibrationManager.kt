/* =================================================================================================
Part of android-core module

https://github.com/albaspazio/android-core

Author: Alberto Inuggi
Copyright (©) 2019-2023
==================================================================================================*/

package org.albaspazio.core.accessory

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity


class VibrationManager(private val ctx: Context) {

    private var vibrator: Vibrator? = null

    companion object{

        fun getVibrator(ctx:Context):Vibrator?{
            val vib =   if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                            val vibratorManager = ctx.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager?
                            vibratorManager?.defaultVibrator
                        } else {
                            @Suppress("DEPRECATION")
                            ctx.getSystemService(AppCompatActivity.VIBRATOR_SERVICE) as Vibrator?
                        }
            return when(vib?.hasVibrator()){
                true -> vib
                else -> null
            }
        }

        fun sysHasVibrator(ctx:Context):Boolean{
            return getVibrator(ctx) != null
        }
    }

    fun init(): VibrationManager? {

        vibrator = getVibrator(ctx)

        return  if(vibrator != null)    this
                else                    null
    }

    fun vibrateSingle(duration: Long, ampl: Int = -1) {   // ampl -1 corresponds to VibrationEffect.DEFAULT_AMPLITUDE

        val dur =   if(duration < 1)   {
                        Log.w("VibrationManager", "vibrateSingle: duration($duration) was below 1, set to 100")
                        100
                    } else duration

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            vibrateSingle26(dur, ampl)
        else
            @Suppress("DEPRECATION")
            vibrator!!.vibrate(dur)
    }

    fun vibratePattern(timings: LongArray, amplitudes: IntArray, rep: Int = -1) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            vibratePattern26(timings, amplitudes, rep)
        else
            @Suppress("DEPRECATION")
            vibrator!!.vibrate(timings, rep)
    }

    fun vibratePattern(pattern: VibrationEffect) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            vibratePattern26(pattern)
        else
            return
    }

    fun vibratePattern(timings_amplitudes: LongArray, rep: Int = -1) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            vibratePattern26(timings_amplitudes, rep)
        else
            return
    }

    fun cancel() {
        vibrator?.cancel()
    }

    // ===========================================================================================================
    @RequiresApi(api = Build.VERSION_CODES.O)
    private fun vibrateSingle26(duration:Long, ampl:Int = VibrationEffect.DEFAULT_AMPLITUDE) {
        val effect = VibrationEffect.createOneShot(duration, ampl)
        vibrator!!.vibrate(effect)
    }
    @RequiresApi(api = Build.VERSION_CODES.Q)
    private fun vibrateSingle29(duration:Long, ampl:Int = VibrationEffect.DEFAULT_AMPLITUDE) {
        // val effect = VibrationEffect.createOneShot(duration, ampl)
        val effect = VibrationEffect.createPredefined(VibrationEffect.EFFECT_HEAVY_CLICK) // VibrationEffect.createOneShot(duration, ampl)
        vibrator!!.vibrate(effect)
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private fun vibratePattern26(timings: LongArray, amplitudes: IntArray, rep: Int = -1) {
        val effect = VibrationEffect.createWaveform(timings, amplitudes, rep)
        vibrator!!.vibrate(effect)
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    private fun vibratePattern26(pattern: VibrationEffect) {
        vibrator!!.vibrate(pattern)
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private fun vibratePattern26(timings_amplitudes: LongArray, rep: Int = -1) {
        val effect = VibrationEffect.createWaveform(timings_amplitudes, rep)
        vibrator!!.vibrate(effect)
    }
    // ===========================================================================================================
}