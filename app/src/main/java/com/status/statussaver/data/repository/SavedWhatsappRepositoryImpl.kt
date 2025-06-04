package com.status.statussaver.data.repository

import android.app.Activity
import android.content.Context
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import android.os.Environment
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import com.status.saver.video.R
import com.status.statussaver.data.model.StatusDataModel
import com.status.statussaver.utils.Utils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import java.io.File

import kotlinx.coroutines.delay


class SavedWhatsappRepositoryImpl(
    private val context: Context
) : SavedWhatsappRepository {

    override fun getWBSavedStatusList(): Flow<ArrayList<String>> = flow {
        val path = File(
        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            .toString() + File.separator + "StatusSaver" + File.separator + "WABusiness"
    )

        if (!path.exists() || !path.isDirectory) {
            emit(ArrayList())
            return@flow
        }

        val statusList = path.listFiles()
            ?.reversed()
            ?.map { file ->
                file.path
            } ?: emptyList()

        emit(ArrayList(statusList))
    }.flowOn(Dispatchers.IO)


    override fun getSavedStatusList(): Flow<ArrayList<String>> = flow {
        val path = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                .toString() + File.separator + "StatusSaver" + File.separator + "Whatsapp"
        )

        if (!path.exists() || !path.isDirectory) {
            emit(ArrayList())
            return@flow
        }

        val statusList = path.listFiles()
            ?.reversed()
            ?.map { file ->
                file.path
            } ?: emptyList()

        emit(ArrayList(statusList))
    }.flowOn(Dispatchers.IO)

    @RequiresApi(Build.VERSION_CODES.R)
    override suspend fun deleteStatus(uri: Uri) {
        Utils.deleteAPI30(context as Activity, uri)
    }

    override suspend fun hasStoragePermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) == 0 && ContextCompat.checkSelfPermission(
            context,
            android.Manifest.permission.READ_EXTERNAL_STORAGE
        ) == 0
    }

    override suspend fun requestStoragePermission() {
        // Implementation will be handled in the ViewModel/Fragment
    }
}
