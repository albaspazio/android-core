/* =================================================================================================
Part of android-core module

https://github.com/albaspazio/android-core

Author: Alberto Inuggi
Copyright (©) 2019-2023
==================================================================================================*/

package org.albaspazio.core.filesystem

import android.content.ContentValues
import android.content.Context
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.annotation.RequiresApi
import java.io.File

fun isExternalStorageWritable() = (Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED)

/* Checks if external storage is available to at least read */
fun isExternalStorageReadable():Boolean         = (Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED || Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED_READ_ONLY)

fun isExternalStorageLegacy(path:File):Boolean  =   if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
                                                            Environment.isExternalStorageLegacy(path)
                                                    else    true

fun isExternalStorageEmulated(path:File):Boolean = Environment.isExternalStorageEmulated(path)

fun createFolder(ctx:Context, dirname: String /*, parentdir: String = Environment.DIRECTORY_DOWNLOADS*/):Boolean{

    return  if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
        createFolderBQ(ctx, dirname)
    else
        createFolderBQ(ctx, dirname)
}

fun createFolderBQ(ctx:Context, dirname: String /*, parentdir: String = Environment.DIRECTORY_DOWNLOADS*/):Boolean{

    val file = File("${Environment.getExternalStorageDirectory()}/$dirname")
   return  if(!file.exists())  file.mkdirs()
            else                true
}

@RequiresApi(Build.VERSION_CODES.Q)
fun createFolderQ(ctx:Context, name:String = Environment.DIRECTORY_DOWNLOADS):Boolean{

    val values = ContentValues()
    values.put(MediaStore.MediaColumns.RELATIVE_PATH, name)       //folder name
    ctx.contentResolver.insert(MediaStore.Files.getContentUri("external"), values)
    return true
}
