package com.status.statussaver.ui.frags.saved

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.status.statussaver.data.model.StatusDataModel
import com.status.statussaver.data.repository.SavedWhatsappRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class SavedWhatsappViewModel(private val repository: SavedWhatsappRepository) : ViewModel() {
    val TAG = SavedWhatsappViewModel::class.simpleName
    private val _uiState = MutableStateFlow<SavedWhatsappUiState>(SavedWhatsappUiState.Initial)
    val uiState: StateFlow<SavedWhatsappUiState> = _uiState

    init {
        loadSavedStatuses()
        loadWBSavedStatuses()
    }

    fun loadSavedStatuses() {
        _uiState.value = SavedWhatsappUiState.Loading
        viewModelScope.launch {
            repository.getSavedStatusList()
                .catch { 
                    _uiState.value = SavedWhatsappUiState.Error(it.message ?: "Unknown error occurred")
                }
                .collect { statusList ->
                    Log.d(TAG, "loadSavedStatuses: $statusList")
                    _uiState.value = if (statusList.isEmpty()) {
                        SavedWhatsappUiState.Empty
                    } else {
                        SavedWhatsappUiState.Success(statusList)
                    }
                }
        }
    }

    fun loadWBSavedStatuses() {
        _uiState.value = SavedWhatsappUiState.Loading
        viewModelScope.launch {
            repository.getWBSavedStatusList()
                .catch {
                    _uiState.value = SavedWhatsappUiState.Error(it.message ?: "Unknown error occurred")
                }
                .collect { statusList ->
                    Log.d(TAG, "loadSavedStatuses: $statusList")
                    _uiState.value = if (statusList.isEmpty()) {
                        SavedWhatsappUiState.Empty
                    } else {
                        SavedWhatsappUiState.Success(statusList)
                    }
                }
        }
    }

    fun deleteStatus(uri: Uri) {
        viewModelScope.launch {
            try {
                repository.deleteStatus(uri)
                loadSavedStatuses()
            } catch (e: Exception) {
                _uiState.value = SavedWhatsappUiState.Error(e.message ?: "Failed to delete status")
            }
        }
    }
}

sealed class SavedWhatsappUiState {
    object Initial : SavedWhatsappUiState()
    object Loading : SavedWhatsappUiState()
    object Empty : SavedWhatsappUiState()
    data class Success(val statusList: ArrayList<String>) : SavedWhatsappUiState()
    data class Error(val message: String) : SavedWhatsappUiState()
}