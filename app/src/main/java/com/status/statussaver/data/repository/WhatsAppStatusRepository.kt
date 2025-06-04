package com.status.statussaver.data.repository

import android.net.Uri
import com.status.statussaver.data.model.StatusDataModel
import kotlinx.coroutines.flow.Flow

interface WhatsAppStatusRepository {
    suspend fun getStatusList(): Flow<List<StatusDataModel>>
    suspend fun saveTreeUri(uri: Uri)
    fun getTreeUri(): String
    suspend fun hasStoragePermission(): Boolean
    suspend fun requestStoragePermission()
}