package org.albaspazio.core.filesystem

import android.content.Context
import android.os.Environment
import java.io.File

fun isExternalStorageWritable() = (Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED)

/* Checks if external storage is available to at least read */
fun isExternalStorageReadable():Boolean         = (Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED || Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED_READ_ONLY)

fun isExternalStorageLegacy(path:File):Boolean  =   if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q)
                                                    Environment.isExternalStorageLegacy(path)
                                                    else    true

fun isExternalStorageEmulated(path:File):Boolean = Environment.isExternalStorageEmulated(path)

fun createFolder(ctx:Context, dirname: String /*, parentdir: String = Environment.DIRECTORY_DOWNLOADS*/):Boolean{
    val file = ctx.getExternalFilesDir(dirname) // automatically create the folder if does not exist
    return file?.exists() ?: false
}

