package com.status.statussaver.data.repository

import android.net.Uri
import com.status.statussaver.data.model.StatusDataModel
import kotlinx.coroutines.flow.Flow

interface WhatsAppBusinessStatusRepository {
    fun getStatusList(): Flow<List<StatusDataModel>>
    suspend fun hasStoragePermission(): Boolean
    suspend fun requestStorageAccess(): Uri?
    suspend fun saveTreeUri(uri: Uri)
    fun getTreeUri(): String?
    suspend fun isWhatsAppBusinessInstalled(): Boolean
}