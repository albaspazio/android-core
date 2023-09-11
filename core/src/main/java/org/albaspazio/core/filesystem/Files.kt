/* =================================================================================================
Part of android-core module

https://github.com/albaspazio/android-core

Author: Alberto Inuggi
Copyright (©) 2019-2023
==================================================================================================*/

package org.albaspazio.core.filesystem

import android.app.DownloadManager
import android.content.Context
import android.os.Environment
import android.util.Log
import java.io.File


fun existFile(filename:String, dir: String = Environment.DIRECTORY_DOWNLOADS):Pair<Boolean, File?>{

    val path    = Environment.getExternalStoragePublicDirectory(dir)
    val file    = File(path, filename)

    return when(file.exists()){
        true    -> Pair(true, file)
        false   -> Pair(false, null)
    }
}

fun deleteFile(filename:String, writedir:String = Environment.DIRECTORY_DOWNLOADS):Boolean{

    val res = existFile(filename, writedir)
    return  if(res.first)   res.second!!.delete()
            else            false
}

fun deleteFileQ(filename:String, writedir:String = Environment.DIRECTORY_DOWNLOADS):Boolean{

    val res = existFile(filename, writedir)
    return  if(res.first)   res.second!!.delete()
            else            false
}

fun renameFile(origfilename:String, finalfilename:String, writedir:String= Environment.DIRECTORY_DOWNLOADS):Boolean{

    return  try {
        val res = existFile(origfilename, writedir)

        if (res.first)  res.second!!.renameTo(File(Environment.getExternalStoragePublicDirectory(writedir), finalfilename))
        else            false
    }
    catch(e:java.lang.Exception){
        Log.e("Files", "error in renameFile: $e")
        false
    }
}

fun notifyFile(file:String, ctx:Context, dir:String = Environment.DIRECTORY_DOWNLOADS){
    val path    = Environment.getExternalStoragePublicDirectory(dir)
    val f       = File(path, file)
    val down    = ctx.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
    down.addCompletedDownload(f.name, "User file", false, "text/plain", f.path, f.length(), true)
}

// return absolute file path or empty string
fun getAbsoluteFilePath(filename:String, dir:String = Environment.DIRECTORY_DOWNLOADS):Pair<Boolean, String>{

    val path:File   = Environment.getExternalStoragePublicDirectory(dir)
    val file        = File(path, filename)

    return when(file.exists()) {
        true    -> Pair(true, "${path.absolutePath}/$filename")
        false   -> Pair(false, "")
    }
}

// ================================================================================
// manage File List
// ================================================================================
fun getFileList(dir:String=Environment.DIRECTORY_DOWNLOADS, allowedext:List<String> = listOf(), contains:String=""):List<File>{

    val fileList:MutableList<File> = mutableListOf()

    Environment.getExternalStoragePublicDirectory(dir)?.listFiles()?.map{
        if(allowedext.isEmpty()) {
            if (contains.isEmpty() || it.name.contains(contains)) fileList.add(it)
        }
        else
            for(ext in allowedext)
                if(it.name.endsWith(ext))
                    if(contains.isEmpty() || it.name.contains(contains)) fileList.add(it)
    }
    return fileList
}

fun getFileNamesList(dir:String=Environment.DIRECTORY_DOWNLOADS, allowedext:List<String> = listOf(), contains:String=""):List<String>{
    return getFileList(dir, allowedext, contains).map{ it.name }.sorted()
}

fun existFileStartingWith(startfilename:String, dir:String = Environment.DIRECTORY_DOWNLOADS, allowedext:List<String> = listOf()):Boolean{

    val startlen = startfilename.length

    getFileList(dir, allowedext).map{
        if(startlen < it.nameWithoutExtension.length)
            if(it.nameWithoutExtension.substring(0, startlen) == startfilename) return true
    }
    return false
}

fun deleteFilesStartingWith(startfilename:String, writedir:String= Environment.DIRECTORY_DOWNLOADS, allowedext:List<String> = listOf()):Int{

    val startlen     = startfilename.length
    var deletedFiles = 0

    getFileList(writedir, allowedext).map{
        if(startlen < it.nameWithoutExtension.length)
            if(it.nameWithoutExtension.substring(0, startlen) == startfilename)
                if(it.delete())
                    deletedFiles++
    }
    return deletedFiles
}