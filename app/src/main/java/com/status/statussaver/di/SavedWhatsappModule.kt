package com.status.statussaver.di

import android.content.Context
import com.status.statussaver.data.repository.SavedWhatsappRepository
import com.status.statussaver.data.repository.SavedWhatsappRepositoryImpl
import com.status.statussaver.ui.frags.saved.SavedWhatsappViewModel

object SavedWhatsappModule {
    private var repository: SavedWhatsappRepository? = null
    private var viewModel: SavedWhatsappViewModel? = null

    @Synchronized
    fun provideRepository(context: Context): SavedWhatsappRepository {
        return repository ?: SavedWhatsappRepositoryImpl(context.applicationContext).also {
            repository = it
        }
    }

    @Synchronized
    fun provideViewModel(context: Context): SavedWhatsappViewModel {
        return viewModel ?: SavedWhatsappViewModel(provideRepository(context)).also {
            viewModel = it
        }
    }

    fun clearViewModel() {
        viewModel = null
    }
}