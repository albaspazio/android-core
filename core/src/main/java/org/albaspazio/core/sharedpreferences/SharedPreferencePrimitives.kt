/* =================================================================================================
Part of android-core module

https://github.com/albaspazio/android-core

Author: Alberto Inuggi
Copyright (©) 2019-2023
==================================================================================================*/

package org.albaspazio.core.sharedpreferences

import android.content.SharedPreferences

abstract class SharedPreferencePrimitives() {

    // both are initialized in subclasses
    protected lateinit var editor: SharedPreferences.Editor
    protected lateinit var pref: SharedPreferences

    fun isInitialized() = ::pref.isInitialized && ::editor.isInitialized

    protected fun String.put(long: Long) {
        editor.putLong(this, long)
        editor.commit()
    }

    protected fun String.put(int: Int) {
        editor.putInt(this, int)
        editor.commit()
    }

    protected fun String.put(string: String) {
        editor.putString(this, string)
        editor.commit()
    }

    protected fun String.put(boolean: Boolean) {
        editor.putBoolean(this, boolean)
        editor.commit()
    }

    protected fun String.getLong() = pref.getLong(this, 0)
    protected fun String.getInt() = pref.getInt(this, 0)
    protected fun String.getString() = pref.getString(this, "")!!
    protected fun String.getBoolean() = pref.getBoolean(this, false)

    fun clearData() {
        if (!isInitialized()) return

        editor.clear()
        editor.commit()
    }

    fun removeKey(key: String) {
        if (!isInitialized()) return
        editor.remove(key)
        editor.commit()
    }
}