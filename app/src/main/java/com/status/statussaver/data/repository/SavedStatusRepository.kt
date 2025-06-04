package com.status.statussaver.data.repository

import com.status.statussaver.data.model.StatusDataModel
import com.status.statussaver.utils.StatusType

interface SavedStatusRepository {
    suspend fun getSavedStatuses(type: StatusType): List<String>
    suspend fun deleteStatus(status: StatusDataModel): Boolean
}