package com.status.statussaver.utils

import android.content.Context
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.util.Log
import androidx.documentfile.provider.DocumentFile
import androidx.exifinterface.media.ExifInterface
import com.drew.imaging.ImageMetadataReader
import com.drew.metadata.exif.ExifIFD0Directory
import java.text.SimpleDateFormat
import java.util.*
import com.status.statussaver.data.model.StatusDataModel

class MetadataExtractor(private val context: Context) {

    companion object {
        private const val WHATSAPP_DATE_PATTERN = "yyyyMMdd"
        private const val STATUS_EXPIRY_HOURS = 24L
    }

    fun extractMetadata(documentFile: DocumentFile): StatusDataModel {
        val uri = documentFile.uri

        Log.d("MetadataExtractor", "extractMetadata: ${documentFile.uri.path}")
        val inputStream = context.contentResolver.openInputStream(uri)

        return try {
            // Basic file information
            val filename = documentFile.name ?: ""
            val filepath = uri.toString()
            val fileSize = documentFile.length()
            val fileFormat = documentFile.type ?: ""
            val lastModifiedDate = documentFile.lastModified()

            // Calculate expiry time based on lastModifiedDate
            val expiryTime = calculateExpiryTime(lastModifiedDate)

            StatusDataModel(
                filename = filename,
                filepath = filepath,
                fileSize = fileSize,
                fileFormat = fileFormat,
                lastModifiedDate = lastModifiedDate,
                expiryTime = expiryTime,
            )
        } catch (e: Exception) {
            e.printStackTrace()
            // Return basic model if metadata extraction fails
            StatusDataModel(documentFile.name ?: "", uri.toString())
        } finally {
            inputStream?.close()
        }
    }

    private fun calculateExpiryTime(lastModifiedDate: Long): Long? {
        return if (lastModifiedDate > 0) {
            // WhatsApp statuses expire 24 hours after creation
            // Using lastModifiedDate as the reference point
            lastModifiedDate + (STATUS_EXPIRY_HOURS * 60 * 60 * 1000) // 24 hours in milliseconds
        } else {
            null
        }
    }

    // Helper function to check if status has expired
    fun isStatusExpired(expiryTime: Long?): Boolean {
        return expiryTime?.let {
            System.currentTimeMillis() > it
        } ?: false
    }

    // Helper function to get remaining time in readable format
    fun getRemainingTime(expiryTime: Long?): String {
        return expiryTime?.let { expiry ->
            val currentTime = System.currentTimeMillis()
            val remainingMillis = expiry - currentTime

            if (remainingMillis <= 0) {
                "Expired"
            } else {
                val hours = remainingMillis / (1000 * 60 * 60)
                val minutes = (remainingMillis % (1000 * 60 * 60)) / (1000 * 60)

                when {
                    hours > 0 -> "${hours}h ${minutes}m remaining"
                    minutes > 0 -> "${minutes}m remaining"
                    else -> "Less than 1m remaining"
                }
            }
        } ?: "Unknown"
    }

    private fun extractImageDimensions(exif: ExifInterface?): Pair<Int, Int> {
        return try {
            val width = exif?.getAttributeInt(ExifInterface.TAG_IMAGE_WIDTH, 0) ?: 0
            val height = exif?.getAttributeInt(ExifInterface.TAG_IMAGE_LENGTH, 0) ?: 0
            Pair(width, height)
        } catch (e: Exception) {
            Pair(0, 0)
        }
    }

    private fun determineStatusType(mimeType: String): String {
        return when {
            mimeType.startsWith("image/") -> "image"
            mimeType.startsWith("video/") -> "video"
            else -> "unknown"
        }
    }
}