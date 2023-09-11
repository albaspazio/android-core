/* =================================================================================================
Part of android-core module

https://github.com/albaspazio/android-core

Author: Alberto Inuggi
Copyright (©) 2019-2023
==================================================================================================*/

package org.albaspazio.core.accessory

import kotlin.reflect.KFunction
import kotlin.reflect.full.companionObject
import kotlin.reflect.full.companionObjectInstance
import kotlin.reflect.full.functions

fun getCompanionObjectMethod(clsname:String, meth:String): Pair<KFunction<*>?, Any?>  {

    val cls     = Class.forName(clsname)
    val kClass  = cls.kotlin

    val co = kClass.companionObject
    val coi = kClass.companionObjectInstance

    var kf:KFunction<*>? = null
    co?.functions?.map{
        if(it.name == meth) kf = it
    }
    return Pair(kf, coi)
}