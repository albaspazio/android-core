/* =================================================================================================
Part of android-core module

https://github.com/albaspazio/android-core

Author: Alberto Inuggi
Copyright (©) 2019-2023
==================================================================================================*/

package org.albaspazio.core.accessory

import java.text.SimpleDateFormat
import java.util.*

fun getTimeDifference(startdate:Date):Int{
    val now:Long = Date().time
    return (now - startdate.time).toInt()
}

fun getDateString(format:String = "it"):String{

    val c = Calendar.getInstance()

    return  if(format == "it")   SimpleDateFormat("ddMMyyyy", Locale.ITALIAN).format(c.getTime())
            else                 SimpleDateFormat("MMyddyyy", Locale.US).format(c.getTime())
}

fun getFullDateString(format:String = "it"):String{

    val c = Calendar.getInstance()

    val h = if(c.get(Calendar.HOUR) > 9)            c.get(Calendar.HOUR).toString()
            else                                    "0" + c.get(Calendar.HOUR).toString()

    val mi = if(c.get(Calendar.MINUTE) > 9)         c.get(Calendar.MINUTE).toString()
            else                                    "0" + c.get(Calendar.MINUTE).toString()

    val s = if(c.get(Calendar.SECOND) > 9)          c.get(Calendar.SECOND).toString()
            else                                    "0" + c.get(Calendar.SECOND).toString()

    return   getDateString(format) + "_" + h + mi + s
}

fun getOnsetDate():String{

    val c = Calendar.getInstance()

    val min = if(c.get(Calendar.MINUTE) > 9)         c.get(Calendar.MINUTE).toString()
            else                                    "0" + c.get(Calendar.MINUTE).toString()

    val s = if(c.get(Calendar.SECOND) > 9)          c.get(Calendar.SECOND).toString()
            else                                    "0" + c.get(Calendar.SECOND).toString()

    val ms = if(c.get(Calendar.MILLISECOND) > 9)    c.get(Calendar.MILLISECOND).toString()
            else                                    "0" + c.get(Calendar.MILLISECOND).toString()

    return "$min:$s:$ms"
}