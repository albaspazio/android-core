package org.albaspazio.core.accessory

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
