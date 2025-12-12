package com.example.app_clinica_atl.util

import android.content.Context
import android.net.Uri
import java.io.File
import java.io.IOException

/** Copia el contenido del [Uri] a un archivo temporal dentro de [Context.cacheDir]. */
@Throws(IOException::class)
fun copyUriToTempFile(uri: Uri, context: Context): File {
    val cacheFolder = File(context.cacheDir, "profile_photos").apply { mkdirs() }
    val outputFile = File.createTempFile("profile_photo_", ".jpg", cacheFolder)
    context.contentResolver.openInputStream(uri)?.use { input ->
        outputFile.outputStream().use { output ->
            input.copyTo(output)
        }
    } ?: throw IOException("No se pudo leer la imagen seleccionada.")
    return outputFile
}
