package org.albaspazio.core.accessory

import android.app.DownloadManager
import android.content.Context
import android.os.Environment
import android.util.Log
import org.albaspazio.core.ui.showToast
import java.io.*

// by default I do not notify DM, I notify DM when explicitly requested or in case file do not exist)
fun saveText(ctx: Context, filename: String, text: String, dir:String= Environment.DIRECTORY_DOWNLOADS, overwrite:Boolean=true, notifyDm:Boolean=false):Boolean{

    if (!isExternalStorageWritable()){
        showToast("Cannot write on External Storage", ctx)
        return false
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
            down.addCompletedDownload(file.name, "User file", false, "text/plain", file.path, file.length(), true)
        }
        return true
    }
    catch (e:Exception)
    {
        showToast("Could not save data to file!\nerror: $e", ctx)
        return false
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

fun getFileList(dir:String=Environment.DIRECTORY_DOWNLOADS, allowedext:List<String> = listOf(), contains:String=""):List<File>{

    val path: String    = Environment.getExternalStoragePublicDirectory(dir).absolutePath
    val fullpath        = File(path)
    val listAllFiles    = fullpath.listFiles()

    val fileList:MutableList<File> = mutableListOf()

    if (listAllFiles != null && listAllFiles.isNotEmpty()) {
        listAllFiles.map{
            if(allowedext.isEmpty()) {
                if (contains.isEmpty() || it.name.contains(contains)) fileList.add(it)
            }
            else
                for(ext in allowedext)
                    if(it.name.endsWith(ext))
                        if(contains.isEmpty() || it.name.contains(contains)) fileList.add(it)
        }
    }
    return fileList
}

fun existFile(filename:String, dir: String = Environment.DIRECTORY_DOWNLOADS):Pair<Boolean, File?>{

    val path    = Environment.getExternalStoragePublicDirectory(dir)
    val file    = File(path, filename)

    return when(file.exists()) {
        true    -> Pair(true, file)
        false   -> Pair(false, null)
    }
}

fun existFileStartingWith(startfilename:String, dir:String = Environment.DIRECTORY_DOWNLOADS, allowedext:List<String> = listOf()):Boolean{

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

fun deleteFile(filename:String, writedir:String= Environment.DIRECTORY_DOWNLOADS):Boolean{
    val res = existFile(filename, writedir)
    return  if(res.first)
                res.second!!.delete()
            else false
}

fun deleteFilesStartingWith(startfilename:String, writedir:String= Environment.DIRECTORY_DOWNLOADS, allowedext:List<String> = listOf()):Int{
    val existing = getFileList(writedir, allowedext)
    val startlen = startfilename.length

    var deletedFiles = 0
    existing.map{
        val filenoext = it.nameWithoutExtension
        if(startlen < filenoext.length)
            if(filenoext.substring(0, startlen) == startfilename)
                if(it.delete())
                    deletedFiles++
    }
    return deletedFiles
}

fun renameFile(origfilename:String, finalfilename:String, writedir:String= Environment.DIRECTORY_DOWNLOADS):Boolean{

    try {
        var dir=Environment.getExternalStoragePublicDirectory(writedir)
        val res = existFile(origfilename, writedir)
        return  if (res.first)  res.second!!.renameTo(File(dir, finalfilename))
                else            false
    }
    catch(e:java.lang.Exception){
        Log.e("Files", "error in renameFile: $e")
        return false
    }
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

fun notifyFile(file:String, ctx:Context, dir:String = Environment.DIRECTORY_DOWNLOADS){
    val path    = Environment.getExternalStoragePublicDirectory(dir)
    val f       = File(path, file)
    val down    = ctx.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
    down.addCompletedDownload(f.name, "User file", false, "text/plain", f.path, f.length(), true)
}