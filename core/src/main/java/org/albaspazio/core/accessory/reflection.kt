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
    return Pair(co?.functions?.first { it.name.equals(meth) }, coi)
}