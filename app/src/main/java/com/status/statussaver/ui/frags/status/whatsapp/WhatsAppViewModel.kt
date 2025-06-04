package com.status.statussaver.ui.frags.status.whatsapp

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.status.statussaver.data.model.StatusDataModel
import com.status.statussaver.data.repository.WhatsAppStatusRepository
import com.status.statussaver.ui.frags.status.whatsappB.WhatsAppBusinessUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class WhatsAppViewModel(private val repository: WhatsAppStatusRepository) : ViewModel() {

    private val _uiState = MutableStateFlow<WhatsAppUiState>(WhatsAppUiState.Initial)
    val uiState: StateFlow<WhatsAppUiState> = _uiState

    init {
        if (repository.getTreeUri().isNotEmpty()) {
            loadStatuses()
        }
    }

    fun loadStatuses() {
        _uiState.value = WhatsAppUiState.Loading
        viewModelScope.launch {
            repository.getStatusList()
                .catch { 
                    _uiState.value = WhatsAppUiState.Error(it.message ?: "Unknown error occurred")
                }
                .collect { statusList ->
                    Log.d("WhatsAppViewModel", "loadStatuses: ${statusList.toString()}")
                    _uiState.value = if (statusList.isEmpty()) {
                        WhatsAppUiState.Empty
                    } else {
                        WhatsAppUiState.Success(statusList)
                    }
                }
        }
    }

    fun saveTreeUri(uri: Uri) {
        viewModelScope.launch {
            repository.saveTreeUri(uri)
            loadStatuses()
        }
    }

    fun checkStoragePermission() {
        viewModelScope.launch {
            if (!repository.hasStoragePermission()) {
                _uiState.value = WhatsAppUiState.RequirePermission
            }
        }
    }

}

sealed class WhatsAppUiState {
    object Initial : WhatsAppUiState()
    object Loading : WhatsAppUiState()
    object Empty : WhatsAppUiState()
    object RequirePermission : WhatsAppUiState()
    data class Success(val statusList: List<StatusDataModel>) : WhatsAppUiState()
    data class Error(val message: String) : WhatsAppUiState()
}