/* =================================================================================================
Part of android-core module

https://github.com/albaspazio/android-core

Author: Alberto Inuggi
Copyright (©) 2019-2023
==================================================================================================*/

package org.albaspazio.core.filesystem

import android.app.DownloadManager
import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.annotation.RequiresApi
import java.io.*

// manage both before and after Q.
// returns String (BQ), Uri (Q+) or null (error)
fun saveText(ctx: Context,
             filename: Any, /* BQ: is a String, Q+: is a Uri */
             text: String,
             dir: String = Environment.DIRECTORY_DOWNLOADS,
             overwrite: Boolean = true,
             notifyDm: Boolean = false,
             forceOld:Boolean = false
):Any{

    if (!isExternalStorageWritable())   throw IOException("Cannot write on External Storage")

    // patch while new version have issues (e.g. does not re-write a file deleted with the old method)
    return  if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
//                if(filename is Uri)
                    saveTextQ(ctx, filename, text, dir, overwrite, notifyDm)
//                else                    throw IOException("IO.saveText was called with wrong param type: $filename")
            } else {
                if(filename is String)  saveTextBQ(ctx, filename as String, text, dir, overwrite, notifyDm)
                else                    throw IOException("IO.saveText was called with wrong param type: $filename")
            }
}

// by default I do not notify DM, I notify DM when explicitly requested or in case file do not exist)
fun saveTextBQ(ctx: Context,
    filename: String,
    text: String,
    dir: String = Environment.DIRECTORY_DOWNLOADS,
    overwrite: Boolean = true,
    notifyDm: Boolean = false
):Any{

    val path = Environment.getExternalStoragePublicDirectory(dir)

    if(!path.exists())
        createFolder(ctx, dir)

    val file = File(path, filename)
    val exist = file.exists()

    if (exist && overwrite) deleteFile(filename, dir)

    val bytes = text.toByteArray(charset("UTF-8"))
    val stream = FileOutputStream(file, true)
    stream.write(bytes)
    stream.close()

    if (notifyDm) {
        val down = ctx.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        down.addCompletedDownload(file.name, "User file", false, "text/plain", file.path, file.length(), true)
    }
    return filename
}

@RequiresApi(Build.VERSION_CODES.Q)
// filename can be a String for a new file, or an Uri to append it
fun saveTextQ(ctx: Context,
              filename: Any,
              text: String,
              dir: String = Environment.DIRECTORY_DOWNLOADS,    // "Documents/MyApp_Data/logs/"
              overwrite: Boolean = true,
              notifyDm: Boolean = false
):Uri {

    val path = Environment.getExternalStoragePublicDirectory(dir)

    if(!path.exists())
        createFolder(ctx, dir)

    val fileUri = when (filename) {
        is String -> {
            val mime =  if(filename.endsWith("json"))   "application/json"
                        else                                  "text/plain"

            val values = ContentValues()
            values.put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
            values.put(MediaStore.MediaColumns.MIME_TYPE, mime)     //file extension, will automatically add to file
            values.put(MediaStore.MediaColumns.RELATIVE_PATH, dir)

            ctx.contentResolver.insert(MediaStore.Files.getContentUri("external"), values) ?: throw Exception("error in saveTextQ")
        }
        is Uri  -> filename
        else    -> throw IOException("IO.saveTextQ was called with a wrong param type")
    }

    val outputStream    = ctx.contentResolver.openOutputStream(fileUri, "wa") ?: throw Exception("error in saveTextQ")

    outputStream.write(text.toByteArray(charset("UTF-8")))
    outputStream.close()

    return fileUri
}

@RequiresApi(Build.VERSION_CODES.Q)
fun existUriQ(ctx: Context, uri:Uri):Boolean{
    return try {
        ctx.contentResolver.openInputStream(uri)?.use {}
        true
    }
    catch (e: IOException) {
      false
    }
}

@RequiresApi(Build.VERSION_CODES.Q)
fun existQ(ctx: Context, filename: Any, dir: String = Environment.DIRECTORY_DOWNLOADS):Boolean{
    return try {
        val cursor = ctx.contentResolver.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
        null, null, null, null)

        if (cursor != null) {
            while (cursor.moveToNext()) {
                // Use an ID column from the projection to get
                // a URI representing the media item itself.
                val path = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA));
                val f = path.substring(path.lastIndexOf('/') + 1);

                if(f == filename)
                    return true
            }
            false
        }
        else  false
    }
    catch(e:Exception){
        false
    }
}


fun readText(filename: String, dir: String = Environment.DIRECTORY_DOWNLOADS):String{

    val path                = Environment.getExternalStoragePublicDirectory(dir)
    val file                = File(path, filename)
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
