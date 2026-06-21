package com.easyfetch.app.data.repository

import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import com.easyfetch.app.data.model.DownloadedVideo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.net.SocketTimeoutException

private const val RELATIVE_FOLDER = "EasyFetch"

class MediaStorageRepositoryImpl(
    private val appContext: Context,
    private val httpClient: OkHttpClient
) : MediaStorageRepository {

    override suspend fun saveVideoToGallery(sourceUrl: String, fileName: String): SaveResult =
        withContext(Dispatchers.IO) {
            try {
                val request = Request.Builder().url(sourceUrl).build()
                httpClient.newCall(request).execute().use { response ->
                    if (!response.isSuccessful) {
                        return@withContext SaveResult.Error("Download failed (${response.code}).")
                    }
                    val body = response.body
                        ?: return@withContext SaveResult.Error("Empty response from server.")

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        saveViaMediaStore(fileName, body.byteStream())
                    } else {
                        saveToLegacyPublicStorage(fileName, body.byteStream())
                    }
                }
            } catch (e: SocketTimeoutException) {
                SaveResult.Error("Download timed out. Please try again.")
            } catch (e: IOException) {
                SaveResult.Error("Failed to save the video file.")
            } catch (e: Exception) {
                SaveResult.Error("Unexpected error while downloading.")
            }
        }

    private fun saveViaMediaStore(fileName: String, input: InputStream): SaveResult {
        val resolver = appContext.contentResolver
        val values = ContentValues().apply {
            put(MediaStore.Video.Media.DISPLAY_NAME, fileName)
            put(MediaStore.Video.Media.MIME_TYPE, "video/mp4")
            put(MediaStore.Video.Media.RELATIVE_PATH, "${Environment.DIRECTORY_MOVIES}/$RELATIVE_FOLDER")
            put(MediaStore.Video.Media.IS_PENDING, 1)
        }
        val collection = MediaStore.Video.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
        val itemUri = resolver.insert(collection, values)
            ?: return SaveResult.Error("Could not create a file entry on this device.")

        val outputStream = resolver.openOutputStream(itemUri)
            ?: return SaveResult.Error("Could not open output stream for the new file.")
        outputStream.use { output -> input.copyTo(output) }

        values.clear()
        values.put(MediaStore.Video.Media.IS_PENDING, 0)
        resolver.update(itemUri, values, null, null)

        val id = ContentUris.parseId(itemUri)
        val sizeBytes = queryFileSize(itemUri)

        return SaveResult.Success(
            DownloadedVideo(
                id = id,
                uri = itemUri,
                displayName = fileName,
                dateAddedSeconds = System.currentTimeMillis() / 1000,
                sizeBytes = sizeBytes
            )
        )
    }

    private fun saveToLegacyPublicStorage(fileName: String, input: InputStream): SaveResult {
        val moviesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES)
        val targetDir = File(moviesDir, RELATIVE_FOLDER)
        if (!targetDir.exists()) targetDir.mkdirs()
        val outputFile = File(targetDir, fileName)

        FileOutputStream(outputFile).use { output -> input.copyTo(output) }

        MediaScannerConnection.scanFile(
            appContext,
            arrayOf(outputFile.absolutePath),
            arrayOf("video/mp4"),
            null
        )

        return SaveResult.Success(
            DownloadedVideo(
                id = outputFile.lastModified(),
                uri = Uri.fromFile(outputFile),
                displayName = fileName,
                dateAddedSeconds = outputFile.lastModified() / 1000,
                sizeBytes = outputFile.length()
            )
        )
    }

    private fun queryFileSize(uri: Uri): Long {
        val projection = arrayOf(MediaStore.Video.Media.SIZE)
        appContext.contentResolver.query(uri, projection, null, null, null)?.use { cursor ->
            if (cursor.moveToFirst()) {
                return cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.SIZE))
            }
        }
        return 0L
    }

    override suspend fun listDownloadedVideos(): List<DownloadedVideo> = withContext(Dispatchers.IO) {
        val results = mutableListOf<DownloadedVideo>()
        val isModernStorage = Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q

        val collection = if (isModernStorage) {
            MediaStore.Video.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
        } else {
            MediaStore.Video.Media.EXTERNAL_CONTENT_URI
        }

        val projection = arrayOf(
            MediaStore.Video.Media._ID,
            MediaStore.Video.Media.DISPLAY_NAME,
            MediaStore.Video.Media.DATE_ADDED,
            MediaStore.Video.Media.SIZE
        )

        val selection: String
        val selectionArgs: Array<String>
        if (isModernStorage) {
            selection = "${MediaStore.Video.Media.RELATIVE_PATH} LIKE ?"
            selectionArgs = arrayOf("%$RELATIVE_FOLDER%")
        } else {
            selection = "${MediaStore.Video.Media.DATA} LIKE ?"
            selectionArgs = arrayOf("%/$RELATIVE_FOLDER/%")
        }

        val sortOrder = "${MediaStore.Video.Media.DATE_ADDED} DESC"

        appContext.contentResolver.query(collection, projection, selection, selectionArgs, sortOrder)
            ?.use { cursor ->
                val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID)
                val nameColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME)
                val dateColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATE_ADDED)
                val sizeColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.SIZE)
                while (cursor.moveToNext()) {
                    val id = cursor.getLong(idColumn)
                    val contentUri = ContentUris.withAppendedId(collection, id)
                    results.add(
                        DownloadedVideo(
                            id = id,
                            uri = contentUri,
                            displayName = cursor.getString(nameColumn) ?: "video.mp4",
                            dateAddedSeconds = cursor.getLong(dateColumn),
                            sizeBytes = cursor.getLong(sizeColumn)
                        )
                    )
                }
            }
        results
    }

    override suspend fun deleteVideo(video: DownloadedVideo): Boolean = withContext(Dispatchers.IO) {
        try {
            appContext.contentResolver.delete(video.uri, null, null) > 0
        } catch (e: Exception) {
            false
        }
    }
}
