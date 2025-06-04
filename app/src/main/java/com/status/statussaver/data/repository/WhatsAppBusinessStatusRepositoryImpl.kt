package com.status.statussaver.data.repository

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Environment
import androidx.core.content.ContextCompat
import androidx.documentfile.provider.DocumentFile
import com.status.statussaver.data.model.StatusDataModel
import com.status.statussaver.utils.SharedPrefs
import com.status.statussaver.utils.Utils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import java.io.File

class WhatsAppBusinessStatusRepositoryImpl(
    private val context: Context
) : WhatsAppBusinessStatusRepository {

    override fun getStatusList(): Flow<List<StatusDataModel>> = flow {
        val treeUri = getTreeUri()
        if (treeUri.isNullOrEmpty()) {
            emit(emptyList())
            return@flow
        }

        val fromTreeUri = DocumentFile.fromTreeUri(context.applicationContext, Uri.parse(treeUri))
        if (fromTreeUri == null || !fromTreeUri.exists() || !fromTreeUri.isDirectory || !fromTreeUri.canRead()) {
            emit(emptyList())
            return@flow
        }

        val statusList = fromTreeUri.listFiles()
            .filterNotNull()
            .filter { !it.uri.toString().contains(".nomedia") }
            .map { StatusDataModel(it.name!!, it.uri.toString()) }
            .reversed()

        emit(statusList)
    }.flowOn(Dispatchers.IO)

    override suspend fun hasStoragePermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) == 0 && ContextCompat.checkSelfPermission(
            context,
            android.Manifest.permission.READ_EXTERNAL_STORAGE
        ) == 0
    }

    override suspend fun requestStorageAccess(): Uri? {
        val statusDir = getWhatsAppBusinessFolder()
        return Uri.parse("content://com.android.externalstorage.documents/document/primary%3A$statusDir")
    }

    override suspend fun saveTreeUri(uri: Uri) {
        try {
            context.contentResolver.takePersistableUriPermission(
                uri,
                Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
            )
            SharedPrefs.setWBTree(context, uri.toString())
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun getTreeUri(): String? = SharedPrefs.getWBTree(context)

    override suspend fun isWhatsAppBusinessInstalled(): Boolean =
        Utils.appInstalledOrNot(context, "com.whatsapp.w4b")

    private fun getWhatsAppBusinessFolder(): String {
        return if (File(
                Environment.getExternalStorageDirectory()
                    .toString() + File.separator + "Android/media/com.whatsapp.w4b/WhatsApp Business" + File.separator + "Media" + File.separator + ".Statuses"
            ).isDirectory
        ) {
            "Android%2Fmedia%2Fcom.whatsapp.w4b%2FWhatsApp Business%2FMedia%2F.Statuses"
        } else {
            "WhatsApp Business%2FMedia%2F.Statuses"
        }
    }
}