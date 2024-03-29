/* =================================================================================================
Part of android-core module

https://github.com/albaspazio/android-core

Author: Alberto Inuggi
Copyright (©) 2019-2023
==================================================================================================*/

package org.albaspazio.core.accessory

import android.content.Context
import android.graphics.Rect
import android.util.Log
import android.util.TypedValue
import android.view.MotionEvent
import android.view.View
import org.json.JSONException
import org.json.JSONObject

fun makeJSON(code: Int, msg: Any?): JSONObject? {
    val json = JSONObject()
    try {
        json.put("code", code)
        json.put("message", msg)
    } catch (e: JSONException) {
        e.printStackTrace()
    }
    return json
}


fun Exception.logLastTwo(LOG_TAG:String){
    Log.e(LOG_TAG, "${this.stackTrace[0].fileName} at line: ${this.stackTrace[0].lineNumber}")
    Log.e(LOG_TAG, "${this.stackTrace[1].fileName} at line: ${this.stackTrace[1].lineNumber}")
}

// Extension method to convert pixels to dp
fun Int.toDp(context: Context):Int = TypedValue.applyDimension(
    TypedValue.COMPLEX_UNIT_DIP,this.toFloat(),context.resources.displayMetrics
).toInt()


// found in https://medium.com/@BladeCoder/kotlin-singletons-with-argument-194ef06edd9e
open class SingletonHolder<out T: Any, in A>(creator: (A) -> T) {
    private var creator: ((A) -> T)? = creator
    @Volatile private var instance: T? = null

    fun getInstance(arg: A): T {


        return instance ?: synchronized(this) {

            val i2 = instance
            if (i2 != null) {
                i2
            } else {
                val created = creator!!(arg)
                instance = created
                creator = null
                created
            }
        }

//        val i = instance
//        if (i != null) {
//            return i
//        }
//
//        return synchronized(this) {
//            val i2 = instance
//            if (i2 != null) {
//                i2
//            } else {
//                val created = creator!!(arg)
//                instance = created
//                creator = null
//                created
//            }
//        }
    }
}

val MotionEvent.up get() = action == MotionEvent.ACTION_UP

fun MotionEvent.isIn(view: View): Boolean {
    val rect = Rect(view.left, view.top, view.right, view.bottom)
    return rect.contains((view.left + x).toInt(), (view.top + y).toInt())
}