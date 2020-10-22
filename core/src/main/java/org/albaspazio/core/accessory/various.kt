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