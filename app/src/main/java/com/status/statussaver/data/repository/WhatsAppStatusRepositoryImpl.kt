package com.status.statussaver.data.repository

import android.content.Context
import android.net.Uri
import androidx.documentfile.provider.DocumentFile
import com.status.statussaver.data.model.StatusDataModel
import com.status.statussaver.utils.MetadataExtractor
import com.status.statussaver.utils.SharedPrefs
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class WhatsAppStatusRepositoryImpl(
    private val context: Context,
    private val metadataExtractor: MetadataExtractor
) : WhatsAppStatusRepository {

    override suspend fun getStatusList(): Flow<List<StatusDataModel>> = flow {
        val treeUri = getTreeUri()
        if (treeUri.isNotEmpty()) {
            val fromTreeUri = DocumentFile.fromTreeUri(context, Uri.parse(treeUri))
            if (fromTreeUri != null && fromTreeUri.exists() && fromTreeUri.isDirectory
                && fromTreeUri.canRead() && fromTreeUri.canWrite()
            ) {
                val statusList = fromTreeUri.listFiles()
                    .filter { !it.uri.toString().contains(".nomedia") }
                    .map { metadataExtractor.extractMetadata(it) }
                emit(statusList)
            } else {
                emit(emptyList())
            }
        } else {
            emit(emptyList())
        }
    }.flowOn(Dispatchers.IO)

    override suspend fun saveTreeUri(uri: Uri) {
        SharedPrefs.setWATree(context, uri.toString())
    }

    override fun getTreeUri(): String {
        return SharedPrefs.getWATree(context).toString()
    }

    override suspend fun hasStoragePermission(): Boolean {
        // Implement permission check logic
        return true
    }

    override suspend fun requestStoragePermission() {
        // Implement permission request logic
    }
}