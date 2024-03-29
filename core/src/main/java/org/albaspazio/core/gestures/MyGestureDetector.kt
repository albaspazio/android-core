/* =================================================================================================
Part of android-core module

https://github.com/albaspazio/android-core

Author: Alberto Inuggi
Copyright (©) 2019-2023
==================================================================================================*/

package org.albaspazio.core.gestures


//import android.util.Log
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import java.util.*

class MyGestureDetector constructor(
    private var onGesture: (m: String) -> Unit,
    private var usageMonitor: UsageMonitor?
) : GestureDetector.SimpleOnGestureListener() {

    private val SWIPE_MIN_DISTANCE = 150
    private val SWIPE_MAX_OFF_PATH = 300
    private val SWIPE_THRESHOLD_VELOCITY = 100

    private var dblTapTime: Long = 0
    private val dblTapInterval:Long = 2000   // mechanism to prevent a LP event short after a DT. don't know why it happens
                                            // I thus set a silent period of dblTapInterval ms before enabling a LP after a DT

    override fun onDown(e: MotionEvent): Boolean {
        return true
    }

    override fun onFling(e1: MotionEvent, e2: MotionEvent, velocityX: Float, velocityY: Float): Boolean {
        if (e1 == null || e2 == null) return false
        val dX = e2.x - e1.x
        val dY = e1.y - e2.y

        var text = "none"

        if (Math.abs(dY) < SWIPE_MAX_OFF_PATH && Math.abs(velocityX) >= SWIPE_THRESHOLD_VELOCITY && Math.abs(dX) >= SWIPE_MIN_DISTANCE) {
            text = if (dX > 0) "SR" else "SL"
        } else {
            if (Math.abs(dX) < SWIPE_MAX_OFF_PATH && Math.abs(velocityY) >= SWIPE_THRESHOLD_VELOCITY && Math.abs(dY) >= SWIPE_MIN_DISTANCE) {
                text = if (dY > 0) "SU" else "SD"
            }
        }
        // Log.d("MyGestureDetector", text)
        usageMonitor?.addGesture(text)
        onGesture(text)
        return super.onFling(e1, e2, velocityX, velocityY)
    }

    override fun onSingleTapConfirmed(e: MotionEvent): Boolean {
        val text = "ST"
        Log.d("MyGestureDetector", "onSingleTapConfirmed: $text")
        usageMonitor?.addGesture(text)
        onGesture(text)
        return super.onSingleTapConfirmed(e)
    }

    override fun onDoubleTap(e: MotionEvent): Boolean {

        val text = "DT"
        dblTapTime = Date().time
        Log.d("MyGestureDetector", "onDoubleTap $text")
        usageMonitor?.addGesture(text)
        onGesture(text)
        return super.onDoubleTap(e)
    }

    override fun onSingleTapUp(event: MotionEvent): Boolean {
        return true
    }

    // ISSUE: after a double tap, a LongPress is triggered. can't prevent it.
    // thus I block it if it happens within 1000 ms.
    override fun onLongPress(e: MotionEvent) {
        val text = "LP"
        val elapsed = Date().time - dblTapTime

        if(elapsed > dblTapInterval){
            usageMonitor?.addGesture(text)
            onGesture(text)
            Log.d("MyGestureDetector", "onLongPress triggered, elapsed $elapsed")
        }
        else Log.d("MyGestureDetector", "onLongPress NOT triggered. $text elapsed $elapsed")

        super.onLongPress(e)
    }
}


/*

    fun setListener(onG: (m: String) -> Unit){
        onGesture = onG
    }

    fun setMonitor(umon: UsageMonitor){
        usageMonitor = umon
    }
 */