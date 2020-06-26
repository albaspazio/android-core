package org.albaspazio.core.accessory

import java.util.*

fun getTimeDifference(startdate:Date):Int{
    val now:Long = Date().time
    return (now - startdate.time).toInt()
}

fun getDateString(format:String = "it"):String{

    val c = Calendar.getInstance()

    val m = if(c.get(Calendar.MONTH) > 9)           "0" + c.get(Calendar.MONTH).toString()
            else                                    c.get(Calendar.MONTH).toString()

    val d = if(c.get(Calendar.DAY_OF_MONTH) > 9)    "0" + c.get(Calendar.DAY_OF_MONTH).toString()
            else                                    c.get(Calendar.DAY_OF_MONTH).toString()

    return  if(format == "it")   d + m + c.get(Calendar.YEAR)
            else                 m + d + c.get(Calendar.YEAR)

}
fun getFullDateString(format:String = "it"):String{

    val c = Calendar.getInstance()

    val h = if(c.get(Calendar.HOUR) > 9)            "0" + c.get(Calendar.HOUR).toString()
            else                                    c.get(Calendar.HOUR).toString()

    val mi = if(c.get(Calendar.MINUTE) > 9)         "0" + c.get(Calendar.MINUTE).toString()
            else                                    c.get(Calendar.MINUTE).toString()

    val s = if(c.get(Calendar.SECOND) > 9)          "0" + c.get(Calendar.SECOND).toString()
            else                                    c.get(Calendar.SECOND).toString()

    return   getDateString(format) + h + mi + s
}