package com.example.qrustaad.utils

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import java.io.OutputStream

fun saveBitmapToGallery(context: Context, bitmap: Bitmap, filename: String = "QRUstaad_${System.currentTimeMillis()}.png") {
    val imageCollection = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
    } else {
        MediaStore.Images.Media.EXTERNAL_CONTENT_URI
    }

    val contentValues = ContentValues().apply {
        put(MediaStore.Images.Media.DISPLAY_NAME, filename)
        put(MediaStore.Images.Media.MIME_TYPE, "image/png")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/QRUstaad")
        }
    }

    try {
        val contentResolver = context.contentResolver
        val uri: Uri? = contentResolver.insert(imageCollection, contentValues)

        uri?.let {
            val outputStream: OutputStream? = contentResolver.openOutputStream(it)
            outputStream?.use { stream ->
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
            }
            Toast.makeText(context, "QR Code saved to Gallery!", Toast.LENGTH_SHORT).show()
        } ?: throw Exception("Failed to create new MediaStore record.")

    } catch (e: Exception) {
        e.printStackTrace()
        Toast.makeText(context, "Failed to save QR Code: ${e.message}", Toast.LENGTH_LONG).show()
    }
}