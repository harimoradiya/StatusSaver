package com.status.statussaver.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.IntentSender.SendIntentException
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.BaseColumns
import android.provider.MediaStore
import androidx.annotation.RequiresApi
import androidx.core.content.FileProvider
import androidx.documentfile.provider.DocumentFile
import com.status.saver.video.BuildConfig
import java.io.File
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.net.URLConnection
import java.util.*
import java.util.regex.Pattern
import androidx.core.net.toUri


object Utils {



    var TEXT_MESSAGE = "Text Message"
    var WHATSAPP = "WhatsApp"
    var MY_PREFS_NAME = "ads"

    var downloadWABusiDir = File(
        Environment.getExternalStorageDirectory().toString() + "/Download/StatusSaver/WABusiness"
    )

    val downloadWhatsAppDir =
        File(Environment.getExternalStorageDirectory().toString() + "/Download/StatusSaver/Whatsapp")

    fun isSomePackageInstalled(context: Context, packageName: String): Boolean {
        val packageManager = context.packageManager
        return runCatching { packageManager.getPackageInfo(packageName, 0) }.isSuccess
    }

    fun getBack(paramString1: String?, paramString2: String?): String? {
        val localMatcher = Pattern.compile(paramString2).matcher(paramString1)
        return if (localMatcher.find()) {
            localMatcher.group(1)
        } else ""
    }

    fun copyFileInSavedDir(context: Context, sourceFile: String?, isWApp: Boolean): Boolean {
        val finalPath: String =
          getDir(isWApp)!!.absolutePath
        val pathWithName = finalPath + File.separator + sourceFile?.let { File(it).name }
        val destUri = Uri.fromFile(File(pathWithName))
        var `is`: InputStream? = null
        var os: OutputStream? = null
        return try {
            val uri = sourceFile?.toUri()
            `is` = uri?.let { context.contentResolver.openInputStream(it) }
            os = context.contentResolver.openOutputStream(destUri, "w")
            val buffer = ByteArray(1024)
            var length: Int
            while (`is`!!.read(buffer).also { length = it } > 0) os!!.write(buffer, 0, length)
            `is`.close()
            os!!.flush()
            os.close()
            val intent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
            intent.data = destUri
            context.sendBroadcast(intent)
            true
        } catch (e: IOException) {
            e.printStackTrace()
            false
        }
    }
    fun getDir(isWApp: Boolean): File? {
        var rootFile: File = downloadWhatsAppDir
        if (!isWApp) {
            rootFile = downloadWABusiDir
        }
        rootFile.mkdirs()
        return rootFile
    }

    fun shareFile(context: Context, isVideo: Boolean, path: String) {
        val share = Intent()
        share.action = Intent.ACTION_SEND
        if (isVideo) share.type = "Video/*" else share.type = "image/*"

        val uri: Uri
        uri = if (path.startsWith("content")) {
            Uri.parse(path)
        } else {
            FileProvider.getUriForFile(
                context,
                context.applicationContext.packageName + ".provider", File(path)
            )
        }

        share.putExtra(Intent.EXTRA_STREAM, uri)
        context.startActivity(share)
    }


    fun isVideoFile(context: Context?, path: String): Boolean {
        if (path.startsWith("content")) {
            val mimeType = DocumentFile.fromSingleUri(context!!, Uri.parse(path))!!
                .type
            return if (mimeType == null || !mimeType.startsWith("video")) {
                false
            } else true
        }
        val mimeType2 = URLConnection.guessContentTypeFromName(path)
        return if (mimeType2 == null || !mimeType2.startsWith("video")) {
            false
        } else true
    }



    fun repostWhatsApp(context: Context, isVideo: Boolean, path: String,iswapp:Boolean) {
        val uri: Uri
        val share = Intent()
        share.action = "android.intent.action.SEND"
        if (isVideo) {
            share.type = "Video/*"
        } else {
            share.type = "image/*"
        }
        uri = if (path.startsWith("content")) {
            Uri.parse(path)
        } else {
            FileProvider.getUriForFile(
                context,
                context.applicationContext.packageName + ".provider",
                File(path)
            )
        }
        share.putExtra("android.intent.extra.STREAM", uri)
        if (iswapp){
            share.setPackage("com.whatsapp")
        }
        else{
            share.setPackage("com.whatsapp.w4b")
        }

        context.startActivity(share)
    }

    fun isImageFile(path: String?): Boolean {
        val mimeType = URLConnection.guessContentTypeFromName(path)
        return mimeType != null && mimeType.startsWith("image")
    }

    fun isVideoFile(path: String?): Boolean {
        val mimeType = URLConnection.guessContentTypeFromName(path)
        return mimeType != null && mimeType.startsWith("video")
    }

    fun mShare(filepath: String?, activity: Activity) {
        val fileToShare = File(filepath)
        if (isImageFile(filepath)) {
            val share = Intent(Intent.ACTION_SEND)
            share.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            share.type = "image/*"
            val photoURI = FileProvider.getUriForFile(
                activity.applicationContext, activity.applicationContext
                    .packageName + ".provider", fileToShare
            )
            share.putExtra(
                Intent.EXTRA_STREAM,
                photoURI
            )
            activity.startActivity(Intent.createChooser(share, "Share via"))
        } else if (isVideoFile(filepath)) {
            val videoURI = FileProvider.getUriForFile(
                activity.applicationContext, activity.applicationContext
                    .packageName + ".provider", fileToShare
            )
            val videoshare = Intent(Intent.ACTION_SEND)
            videoshare.type = "*/*"
            videoshare.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            videoshare.putExtra(Intent.EXTRA_STREAM, videoURI)
            activity.startActivity(videoshare)
        }
    }

    fun getImageUriId(activity: Activity, uri: String, type: String, id: Long): Uri {
        var id = id
        val projection = arrayOf(MediaStore.MediaColumns._ID)
        val cursor: Cursor?
        cursor = if (type == "video") {
            activity.contentResolver.query(
                MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                projection,
                MediaStore.MediaColumns.DATA + "=?",
                arrayOf(uri),
                null
            )
        } else {
            activity.contentResolver.query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                projection,
                MediaStore.MediaColumns.DATA + "=?",
                arrayOf(uri),
                null
            )
        }
        if (cursor != null) {
            if (cursor.count > 0) {
                cursor.moveToNext()
                id = cursor.getLong(cursor.getColumnIndexOrThrow(BaseColumns._ID))
            }
        }
        cursor!!.close()
        return if (type == "video") {
            Uri.withAppendedPath(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, id.toInt().toString())
        } else {
            Uri.withAppendedPath(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                id.toInt().toString()
            )
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.R)
    @Throws(SendIntentException::class)
    fun deleteAPI30(activity: Activity, imageUri: Uri) {
        val contentResolver = activity.contentResolver
        // API 30
        val uriList: ArrayList<Uri> = ArrayList()
        Collections.addAll(uriList, imageUri)
        val pendingIntent = MediaStore.createDeleteRequest(contentResolver, uriList)
        activity.startIntentSenderForResult(
            pendingIntent.intentSender,
            100, null, 0,
            0, 0, null
        )
    }


    fun share(context: Context, title: String?, path: String?) {
        val shareIntent = Intent(Intent.ACTION_SEND)
        if (Utils.isVideoFile(path)) {
            shareIntent.type = "video/*"
        } else {
            shareIntent.type = "image/*"
        }
        shareIntent.putExtra(Intent.EXTRA_TITLE, title)
        shareIntent.putExtra(
            Intent.EXTRA_STREAM,
            FileProvider.getUriForFile(
                context,
                BuildConfig.APPLICATION_ID + ".provider",
                File(path)
            )
        )
        shareIntent.putExtra(
            Intent.EXTRA_TEXT,
            "Download App From here : https://play.google.com/store/apps/details?id=" + context.packageName
        )
        shareIntent
            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET)
        context.startActivity(Intent.createChooser(shareIntent, "Share"))
    }


    fun appInstalledOrNot(context: Context, uri: String?): Boolean {
        val pm = context.packageManager
        return try {
            pm.getPackageInfo(uri!!, PackageManager.GET_ACTIVITIES)
            true
        } catch (e: PackageManager.NameNotFoundException) {
            false
        }
    }




}