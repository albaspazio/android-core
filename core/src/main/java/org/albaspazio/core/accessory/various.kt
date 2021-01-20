package org.albaspazio.core.accessory

import android.util.Log
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

// found in https://medium.com/@BladeCoder/kotlin-singletons-with-argument-194ef06edd9e
open class SingletonHolder<out T: Any, in A>(creator: (A) -> T) {
    private var creator: ((A) -> T)? = creator
    @Volatile private var instance: T? = null

    fun getInstance(arg: A): T {
        val i = instance
        if (i != null) {
            return i
        }

        return synchronized(this) {
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
    }
}