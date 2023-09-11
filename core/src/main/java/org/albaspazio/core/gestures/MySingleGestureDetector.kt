/* =================================================================================================
Part of android-core module

https://github.com/albaspazio/android-core

Author: Alberto Inuggi
Copyright (©) 2019-2023
==================================================================================================*/

package org.albaspazio.core.gestures


import android.content.Context
import android.view.View
import androidx.core.view.GestureDetectorCompat

object MySingleGestureDetector {

    private lateinit var mDetector: GestureDetectorCompat
    private lateinit var myDetector: MyGestureDetector

    fun init(view: View, ctx: Context, onGesture: (m: String) -> Unit = {}, um: UsageMonitor? = null) {

        myDetector  = MyGestureDetector(onGesture, um)
        mDetector   = GestureDetectorCompat(ctx, myDetector)
        view.setOnTouchListener { _, motionEvent -> mDetector.onTouchEvent(motionEvent) }
    }
}


    // doesn't prevent callback
//    fun stopListening(view:View){
//        view.setOnTouchListener(null)
//    }
//    
//    fun setListener(onG: KFunction1<String, Unit>){
//        myDetector.setListener(onG)
//    }
//
//    fun setMonitor(umon:UsageMonitor ){
//        myDetector.setMonitor(umon)
//    }
//
//    fun onTouchEvent(event: MotionEvent){
//        mDetector.onTouchEvent(event)
//    }

