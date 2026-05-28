/* =================================================================================================
Part of android-core module

https://github.com/albaspazio/android-core

Author: Alberto Inuggi
Copyright (©) 2019-2023
==================================================================================================*/

package org.albaspazio.core.filesystem

import android.app.DownloadManager
import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.annotation.RequiresApi
import java.io.BufferedReader
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStreamReader

// manage both before and after Q.
// returns String (BQ), Uri (Q+) or null (error)
@RequiresApi(Build.VERSION_CODES.FROYO)
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
// filename can be a String or an Uri.
// append/overwrite existing according to overwrite parameter
fun saveTextQ(ctx: Context,
              filename: Any,
              text: String,
              dir: String = Environment.DIRECTORY_DOWNLOADS,
              overwrite: Boolean = true,
              notifyDm: Boolean = false
): Uri {

    val path = Environment.getExternalStoragePublicDirectory(dir)

    if (!path.exists())
        createFolder(ctx, dir)

    val fileUri = when (filename) {
        is String -> {
            val mime = if (filename.endsWith("json")) "application/json" else "text/plain"

            // Check if file already exists in MediaStore
            val projection = arrayOf(MediaStore.MediaColumns._ID)
            val selection = "${MediaStore.MediaColumns.DISPLAY_NAME} = ? AND ${MediaStore.MediaColumns.RELATIVE_PATH} = ?"
            val selectionArgs = arrayOf(filename, "$dir/")

            val existingUri = ctx.contentResolver.query(
                MediaStore.Files.getContentUri("external"),
                projection,
                selection,
                selectionArgs,
                null
            )?.use { cursor ->
                if (cursor.moveToFirst()) {
                    val id =
                        cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns._ID))
                    ContentUris.withAppendedId(MediaStore.Files.getContentUri("external"), id)
                } else null
            }

            // Use existing Uri or create new one
            existingUri ?: run {
                val values = ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
                    put(MediaStore.MediaColumns.MIME_TYPE, mime)
                    put(MediaStore.MediaColumns.RELATIVE_PATH, dir)
                }
                ctx.contentResolver.insert(MediaStore.Files.getContentUri("external"), values)
                    ?: throw Exception("error in saveTextQ: insert failed")
            }
        }

        is Uri -> filename
        else -> throw IOException("IO.saveTextQ was called with a wrong param type")
    }

    // DECISION LOGIC FOR OVERWRITE
    // "w"  -> Write (Overwrites existing content)
    // "wa" -> Write Append (Adds to the end of the file)
    val mode = if (overwrite) "w" else "wa"

    val outputStream = ctx.contentResolver.openOutputStream(fileUri, mode)
        ?: throw Exception("error in saveTextQ: could not open output stream")

    outputStream.use { stream ->
        stream.write(text.toByteArray(Charsets.UTF_8))
    }

    return fileUri
}

@RequiresApi(Build.VERSION_CODES.Q)
// filename can be a String for a new file, or an Uri to append it
fun saveTextQ2(
    ctx: Context,
    filename: Any,
    text: String,
    dir: String = Environment.DIRECTORY_DOWNLOADS, // e.g. "Documents/MyApp_Data/logs/"
    overwrite: Boolean = true,
    notifyDm: Boolean = false
): Uri {

    val path = Environment.getExternalStoragePublicDirectory(dir)
    Log.d("saveTextQ", "Target path: $path")

    if (!path.exists()) {
        Log.d("saveTextQ", "Path does not exist, creating...")
        createFolder(ctx, dir)
    }

    try {
        val fileUri: Uri = when (filename) {
            is String -> {
                val mime = if (filename.endsWith("json")) "application/json" else "text/plain"
                val collection = MediaStore.Files.getContentUri("external")
                
                // First check if file exists in MediaStore
                fun findExistingFile(): Uri? {
                    val selection = "${MediaStore.MediaColumns.RELATIVE_PATH} = ? AND ${MediaStore.MediaColumns.DISPLAY_NAME} = ?"
                    val selectionArgs = arrayOf("$dir/", filename)
                    
                    return ctx.contentResolver.query(
                        collection,
                        arrayOf(MediaStore.MediaColumns._ID),
                        selection,
                        selectionArgs,
                        null
                    )?.use { cursor ->
                        if (cursor.moveToFirst()) {
                            val id = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns._ID))
                            Log.d("saveTextQ", "File exists in MediaStore, ID: $id")
                            ContentUris.withAppendedId(collection, id)
                        } else {
                            null
                        }
                    }
                }
                
                // Return existing file if found
                findExistingFile()?.let { return@let it }
                
                // Create new file entry
                val values = ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
                    put(MediaStore.MediaColumns.MIME_TYPE, mime)
                    put(MediaStore.MediaColumns.RELATIVE_PATH, dir)
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        put(MediaStore.MediaColumns.IS_PENDING, 1)
                    }
                }

                Log.d("saveTextQ", "Inserting into MediaStore with values=$values")

                val uri = ctx.contentResolver.insert(collection, values)
                    ?: throw IOException("MediaStore insert returned null for $filename in $dir. " +
                            "Check if the app has storage permissions and the target directory is accessible.")
                
                Log.d("saveTextQ", "Successfully created MediaStore entry: $uri")
                uri
            }

            is Uri -> {
                Log.d("saveTextQ", "Filename is already a Uri: $filename")
                filename
            }

            else -> throw IOException("IO.saveTextQ was called with a wrong param type: ${filename::class}")
        }

        Log.d("saveTextQ", "Got fileUri=$fileUri, opening outputStream...")

        val outputStream = try {
            ctx.contentResolver.openOutputStream(fileUri, "wa")
        } catch (e: Exception) {
            Log.e("saveTextQ", "openOutputStream failed: ${e.message}", e)
            throw e
        } ?: throw IOException("contentResolver.openOutputStream returned null")

        Log.d("saveTextQ", "Writing ${text.toByteArray().size} bytes...")

        outputStream.use {
            it.write(text.toByteArray(Charsets.UTF_8))
        }

        Log.d("saveTextQ", "File successfully saved at $fileUri")
        return fileUri
    } catch (e: IOException) {
        Log.e("saveTextQ", "IOException while saving file: ${e.message}", e)
        throw e
    } catch (e: Exception) {
        Log.e("saveTextQ", "Unexpected error: ${e.message}", e)
        throw e
    }
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
                val path = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA))
                val f = path.substring(path.lastIndexOf('/') + 1)

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
