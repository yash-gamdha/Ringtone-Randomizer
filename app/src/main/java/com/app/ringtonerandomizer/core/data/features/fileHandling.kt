package com.app.ringtonerandomizer.core.data.features

import android.app.RecoverableSecurityException
import android.content.ContentResolver
import android.content.Context
import android.database.Cursor
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import com.app.ringtonerandomizer.core.data.GlobalVariables
import java.io.File
import java.io.FileOutputStream

suspend fun addRingtones(listOfUri: List<Uri>, context: Context): Boolean {
    var isSuccessful = true
    listOfUri.forEach { uri ->
        var outputStream: FileOutputStream? = null

        try {
            val fileName = getRingtoneName(uri, context.contentResolver)
            val file = File("${GlobalVariables.PATH}$fileName")

            if (!file.exists()) {
                outputStream = FileOutputStream(file)

                context.contentResolver.openInputStream(uri)?.use { input ->
                    outputStream.use { output ->
                        input.copyTo(output)
                    }
                }
            }

            // triggering manual media scan so that copied ringtone is also detected by system ringtones
            MediaScannerConnection.scanFile(
                context,
                arrayOf(file.absolutePath),
                null,
                null
            )
        } catch (e: Exception) {
            isSuccessful = false
            Log.d("selected copy error", e.message.toString())
        } finally {
            outputStream?.close()
        }
    }

    return isSuccessful
}

// uri to name
fun getRingtoneName(uri: Uri, contentResolver: ContentResolver): String {
    var name = ""
    var cursor: Cursor? = null

    try {
        cursor = contentResolver.query(
            uri, arrayOf(MediaStore.MediaColumns.DISPLAY_NAME), null, null, null
        )
        cursor?.use {
            if (it.moveToFirst()) {
                name = it.getString(it.getColumnIndexOrThrow(MediaStore.MediaColumns.DISPLAY_NAME))
            }
        }
    } catch (e: Exception) {
        Log.d("error", e.message.toString())
    } finally {
        cursor?.close()
    }

    return name
}

// name to uri
fun getFileUri(file: String, context: Context): Uri {
    var uri: Uri? = null
    val projection = arrayOf(
        MediaStore.Audio.Media._ID, MediaStore.Audio.Media.DATA
    )
    val selection = "${MediaStore.Audio.Media.DATA} = ?"
    val selectionArgs = arrayOf(file)

    val cursor = context.contentResolver.query(
        MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
        projection,
        selection,
        selectionArgs,
        null
    )

    cursor?.use {
        if (it.moveToFirst()) {
            val id = it.getLong(it.getColumnIndexOrThrow(MediaStore.MediaColumns._ID))
            uri = Uri.withAppendedPath(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, id.toString()
            )
        }
    }

    cursor?.close()
    return uri!!
}

suspend fun deleteRingtone(
    context: Context,
    ringtone: String,
    intentLauncher: ActivityResultLauncher<IntentSenderRequest>
): Boolean {
    val fileUri = getFileUri("${GlobalVariables.PATH}$ringtone", context)
    try {
        context.contentResolver.delete(fileUri, null, null)
        return true
    } catch (e: SecurityException) {
        val intentSender = when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.R -> {
                MediaStore.createDeleteRequest(
                    context.contentResolver,
                    listOf(fileUri)
                ).intentSender
            }

            Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q -> {
                val recoverableSecurityException = e as? RecoverableSecurityException
                recoverableSecurityException?.userAction?.actionIntent?.intentSender
            }
            else -> null
        }
        intentSender?.let {
            intentLauncher.launch(IntentSenderRequest.Builder(it).build())
        }
        return false
    }
}