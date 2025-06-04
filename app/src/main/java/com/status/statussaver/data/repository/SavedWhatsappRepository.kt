package com.status.statussaver.data.repository

import android.net.Uri
import com.status.statussaver.data.model.StatusDataModel
import kotlinx.coroutines.flow.Flow

interface SavedWhatsappRepository {
    fun getSavedStatusList(): Flow<ArrayList<String >>
    fun getWBSavedStatusList(): Flow<ArrayList<String>>
    suspend fun deleteStatus(uri: Uri)
    suspend fun hasStoragePermission(): Boolean
    suspend fun requestStoragePermission()
}