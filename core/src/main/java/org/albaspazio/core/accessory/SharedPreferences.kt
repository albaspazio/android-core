package org.albaspazio.core.accessory

import android.annotation.SuppressLint
import android.content.Context


/*
"com.app.options".save(applicationContext, mapOf("volume" to 0.8f, "fullscreen" to true))

val volume = "options".load(applicationContext)["volume"] as? Float // 0.8
 */

@SuppressLint("ApplySharedPref")
fun String.save(applicationContext: Context, value: Map<String, Any>, clear: Boolean = false, now: Boolean = false) {
    val sp = applicationContext.getSharedPreferences(this, Context.MODE_PRIVATE).edit()
    if (clear)
        sp.clear()
    value.keys.forEach { key ->
        val v = value[key]
        if (v != null) {
            when (v) {
                is String -> sp.putString(key, v)
                is Float -> sp.putFloat(key, v)
                is Long -> sp.putLong(key, v)
                is Int -> sp.putInt(key, v)
                is Boolean -> sp.putBoolean(key, v)
            }
        }
    }
    if (now)
        sp.commit()
    else
        sp.apply()
}

fun String.load(applicationContext: Context): Map<String, Any> {
    val sp = applicationContext.getSharedPreferences(this, Context.MODE_PRIVATE)
    val keys = sp.all.keys
    val result = hashMapOf<String, Any>()
    keys.map { key ->
        val v = sp.all[key]
        if (v != null)
            result[key] = v
    }
    return result
}