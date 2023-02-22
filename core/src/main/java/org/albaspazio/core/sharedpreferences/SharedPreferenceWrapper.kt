package org.albaspazio.core.sharedpreferences

import android.content.SharedPreferences

abstract class SharedPreferenceWrapper {

    protected lateinit var prefs: SharedPreferences

    fun isInitialized() = ::prefs.isInitialized

    open fun read(key: String, defvalue: Any): Any? {
        return when(defvalue){
            is String   -> prefs.getString(key, defvalue)
            is Int      -> prefs.getInt(key, defvalue)
            is Boolean  -> prefs.getBoolean(key, defvalue)
            is Long     -> prefs.getLong(key, defvalue)
            is Float    -> prefs.getFloat(key, defvalue)
            is Set<*>   -> {
                        if(defvalue.isNotEmpty() && defvalue.elementAt(0) is String)
                                prefs.getStringSet(key, defvalue as Set<String>)
                        else    return null
            }
            else        -> null
        }
    }

    open fun write(key: String, value: Any):Any? {
        val prefsEditor: SharedPreferences.Editor = prefs.edit()
        with(prefsEditor) {
            when(value){
                is String   -> putString(key, value)
                is Int      -> putInt(key, value)
                is Boolean  -> putBoolean(key, value)
                is Long     -> putLong(key, value)
                is Float    -> putFloat(key, value)
                is Set<*>   -> {
                                if(value.isNotEmpty() && value.elementAt(0) is String)    putStringSet(key, value as Set<String>)
                                else                                                            return null
                }
                else        -> return null
            }
            commit()
        }
        return value
    }
}