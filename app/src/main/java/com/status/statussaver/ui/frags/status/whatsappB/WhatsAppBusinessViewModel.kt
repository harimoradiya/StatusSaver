package com.status.statussaver.ui.frags.status.whatsappB

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.status.statussaver.data.model.StatusDataModel
import com.status.statussaver.data.repository.WhatsAppBusinessStatusRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class WhatsAppBusinessViewModel(
    private val repository: WhatsAppBusinessStatusRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<WhatsAppBusinessUiState>(WhatsAppBusinessUiState.Initial)
    val uiState: StateFlow<WhatsAppBusinessUiState> = _uiState

    init {
        checkInitialState()
    }

    private fun checkInitialState() {
        viewModelScope.launch {
            if (repository.getTreeUri().isNullOrEmpty()) {
                _uiState.value = WhatsAppBusinessUiState.NeedsPermission
            } else {
                loadStatuses()
            }
        }
    }

    fun saveTreeUri(uri: Uri) {
        viewModelScope.launch {
            repository.saveTreeUri(uri)
            loadStatuses()
        }
    }



    fun loadStatuses() {
        _uiState.value = WhatsAppBusinessUiState.Loading

        repository.getStatusList()
            .onEach { statuses ->
                _uiState.value = if (statuses.isEmpty()) {
                    WhatsAppBusinessUiState.Empty
                } else {
                    WhatsAppBusinessUiState.Success(statuses)
                }
            }
            .catch { error ->
                _uiState.value = WhatsAppBusinessUiState.Error(error.message ?: "Unknown error occurred")
            }
            .launchIn(viewModelScope)
    }

    fun checkAndRequestPermission() {
        viewModelScope.launch {
            if (!repository.isWhatsAppBusinessInstalled()) {
                _uiState.value = WhatsAppBusinessUiState.WhatsAppBusinessNotInstalled
                return@launch
            }

            if (!repository.hasStoragePermission()) {
                _uiState.value = WhatsAppBusinessUiState.StoragePermissionRequired
                return@launch
            }

            val uri = repository.requestStorageAccess()
            if (uri != null) {
                _uiState.value = WhatsAppBusinessUiState.StorageAccessRequired(uri)
            } else {
                _uiState.value = WhatsAppBusinessUiState.Error("Failed to request storage access")
            }
        }
    }

    fun handleStorageAccessResult(uri: Uri) {
        viewModelScope.launch {
            repository.saveTreeUri(uri)
            loadStatuses()
        }
    }
}

sealed class WhatsAppBusinessUiState {
    object Initial : WhatsAppBusinessUiState()
    object Loading : WhatsAppBusinessUiState()
    object Empty : WhatsAppBusinessUiState()
    object NeedsPermission : WhatsAppBusinessUiState()
    object StoragePermissionRequired : WhatsAppBusinessUiState()
    object WhatsAppBusinessNotInstalled : WhatsAppBusinessUiState()
    data class StorageAccessRequired(val uri: Uri) : WhatsAppBusinessUiState()
    data class Success(val statuses: List<StatusDataModel>) : WhatsAppBusinessUiState()
    data class Error(val message: String) : WhatsAppBusinessUiState()
}