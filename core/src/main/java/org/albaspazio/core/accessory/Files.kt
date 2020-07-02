package org.albaspazio.core.accessory

import android.app.DownloadManager
import android.content.Context
import android.os.Environment
import java.io.*

// by default I do not notify DM, I notify DM when explicitly requested or in case file do not exist)
fun saveText(ctx: Context, filename: String, text: String, dir:String= Environment.DIRECTORY_DOWNLOADS, overwrite:Boolean=true, notifyDm:Boolean=false){

    if (!isExternalStorageWritable()){
        showToast("Cannot write on External Storage", ctx)
        return
    }
    try {
        val path    = Environment.getExternalStoragePublicDirectory(dir)
        val file    = File(path, filename)

        val exist   = file.exists()

        if(exist && overwrite) deleteFile(filename, dir)

        val bytes   = text.toByteArray(charset("UTF-8"))
        val stream  = FileOutputStream(file, true)
        stream.write(bytes)
        stream.close()

        if (notifyDm) {
            val down = ctx.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
            down.addCompletedDownload(
                file.name,
                "User file",
                false,
                "text/plain",
                file.path,
                file.length(),
                true
            )
        }
    }
    catch (exc: Exception)
    {
        showToast(
            "Could not save data to file!",
            ctx
        )
    }
}

fun readText(filename: String, dir:String= Environment.DIRECTORY_DOWNLOADS):String{

    val path                = Environment.getExternalStoragePublicDirectory(dir)
    val file                = File(path,filename)
    val fileInputStream     = FileInputStream(file)
    val inputStreamReader   = InputStreamReader(fileInputStream)
    val bufferedReader      = BufferedReader(inputStreamReader)
    val stringBuilder       = StringBuilder()
    var text:String?        = null

    while ({ text = bufferedReader.readLine(); text }() != null) {
        stringBuilder.append(text)
    }
    fileInputStream.close()
    return stringBuilder.toString()
}

fun getFileList(dir:String= Environment.DIRECTORY_DOWNLOADS, allowedext:List<String>):List<File>{

    val path: String    = Environment.getExternalStoragePublicDirectory(dir).absolutePath
    val fullpath        = File(path)
    val listAllFiles    = fullpath.listFiles()

    val fileList:MutableList<File> = mutableListOf()

    if (listAllFiles != null && listAllFiles.isNotEmpty()) {
        listAllFiles.map{
            for(ext in allowedext) {
                if (it.name.endsWith(ext)) {
//                    Log.e("downloadFilePath", it.absolutePath) // File absolute path
//                    Log.e("downloadFileName", it.name)         // File Name
                    fileList.add(it)
                }
            }
        }
    }
    return fileList
}

fun existFile(filename:String, dir:String = Environment.DIRECTORY_DOWNLOADS):Pair<Boolean, File?>{

    val path    = Environment.getExternalStoragePublicDirectory(dir)
    val file    = File(path, filename)

    return when(file.exists()) {
        true    -> Pair(true, file)
        false   -> Pair(false, null)
    }
}

fun existFileStartingWith(startfilename:String, dir:String = Environment.DIRECTORY_DOWNLOADS, allowedext:List<String>):Boolean{

    val existing = getFileList(dir, allowedext)
    val startlen = startfilename.length
    existing.map{

        val filenoext = it.nameWithoutExtension
        if(startlen < filenoext.length)
            if(filenoext.substring(0, startlen) == startfilename) return true
    }
    return false
}

fun getAbsoluteFilePath(filename:String, dir:String = Environment.DIRECTORY_DOWNLOADS):Pair<Boolean, String>{

    val path:File   = Environment.getExternalStoragePublicDirectory(dir)
    val file        = File(path, filename)

    return when(file.exists()) {
        true    -> Pair(true, "${path.absolutePath}/$filename")
        false   -> Pair(false, "")
    }
}

fun deleteFile(filename:String, writedir:String= Environment.DIRECTORY_DOWNLOADS){
    val res     = existFile(filename, writedir)
    if (res.first)
        res.second!!.delete()
}

fun isExternalStorageWritable(): Boolean {
    val state = Environment.getExternalStorageState()
    return (Environment.MEDIA_MOUNTED == state)
}

/* Checks if external storage is available to at least read */
fun isExternalStorageReadable(): Boolean {
    val state = Environment.getExternalStorageState()
    return (Environment.MEDIA_MOUNTED == state || Environment.MEDIA_MOUNTED_READ_ONLY == state)
}
