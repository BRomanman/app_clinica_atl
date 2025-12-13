package com.example.app_clinica_atl.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import kotlin.math.max
import kotlin.math.roundToInt

/** Copia el contenido del [Uri] a un archivo temporal dentro de [Context.cacheDir]. */
@Throws(IOException::class)
fun copyUriToTempFile(uri: Uri, context: Context): File {
    val cacheFolder = File(context.cacheDir, "profile_photos").apply { mkdirs() }
    val outputFile = File.createTempFile("profile_photo_", ".jpg", cacheFolder)

    val resolver = context.contentResolver

    // 1) Leer dimensiones sin cargar en memoria
    val originalBounds = resolver.openInputStream(uri)?.use { input ->
        BitmapFactory.Options().apply {
            inJustDecodeBounds = true
            BitmapFactory.decodeStream(input, null, this)
        }
    } ?: throw IOException("No se pudo leer la imagen seleccionada.")

    // 2) Decodificar con sampleo para bajar resolución
    val sampleSize = calculateInSampleSize(
        width = originalBounds.outWidth,
        height = originalBounds.outHeight,
        maxWidth = 900,
        maxHeight = 900
    )
    val decodedBitmap = resolver.openInputStream(uri)?.use { input ->
        BitmapFactory.decodeStream(
            input,
            null,
            BitmapFactory.Options().apply { inSampleSize = sampleSize }
        )
    } ?: throw IOException("No se pudo procesar la imagen seleccionada.")

    // 3) Reescalar si sigue siendo muy grande y comprimir < 1 MB
    val resized = resizeBitmapIfNeeded(decodedBitmap, 900)
    val compressedBytes = compressWithStrictLimit(resized, 500 * 1024) // mantenernos por debajo de 1MB con holgura

    outputFile.outputStream().use { output ->
        output.write(compressedBytes)
    }

    if (resized != decodedBitmap) decodedBitmap.recycle()
    resized.recycle()
    return outputFile
}

private fun calculateInSampleSize(
    width: Int,
    height: Int,
    maxWidth: Int,
    maxHeight: Int
): Int {
    var inSampleSize = 1
    if (height > maxHeight || width > maxWidth) {
        var halfHeight = height / 2
        var halfWidth = width / 2
        while (halfHeight / inSampleSize >= maxHeight && halfWidth / inSampleSize >= maxWidth) {
            inSampleSize *= 2
        }
    }
    return max(1, inSampleSize)
}

private fun resizeBitmapIfNeeded(bitmap: Bitmap, maxSize: Int): Bitmap {
    val largestSide = max(bitmap.width, bitmap.height)
    if (largestSide <= maxSize) return bitmap

    val scale = maxSize.toFloat() / largestSide.toFloat()
    val targetWidth = (bitmap.width * scale).roundToInt()
    val targetHeight = (bitmap.height * scale).roundToInt()
    return Bitmap.createScaledBitmap(bitmap, targetWidth, targetHeight, true)
}

private fun compressWithStrictLimit(bitmap: Bitmap, byteLimit: Int): ByteArray {
    var current = bitmap
    var targetMaxSide = max(bitmap.width, bitmap.height)
    var best: ByteArray? = null

    repeat(5) {
        val compressed = compressJpeg(current, byteLimit)
        best = compressed
        if (compressed.size <= byteLimit) {
            if (current !== bitmap) current.recycle()
            return compressed
        }
        // Si sigue grande, reducimos resolución y reintentamos.
        targetMaxSide = (targetMaxSide * 0.8f).roundToInt().coerceAtLeast(600)
        val resized = resizeBitmapIfNeeded(current, targetMaxSide)
        if (resized !== current && current !== bitmap) current.recycle()
        current = resized
    }

    if (current !== bitmap) current.recycle()
    // Devolver la mejor compresión lograda; aunque supere el límite, es lo más pequeño logrado.
    return best ?: ByteArray(0)
}

private fun compressJpeg(
    bitmap: Bitmap,
    byteLimit: Int,
    startQuality: Int = 85,
    minQuality: Int = 35
): ByteArray {
    val stream = ByteArrayOutputStream()
    var quality = startQuality
    var best: ByteArray? = null
    do {
        stream.reset()
        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, stream)
        val data = stream.toByteArray()
        best = data
        if (data.size <= byteLimit) break
        quality -= 5
    } while (quality >= minQuality)
    return best ?: ByteArray(0)
}
