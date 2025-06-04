package com.status.statussaver.di

import android.content.Context
import com.status.statussaver.data.repository.WhatsAppStatusRepository
import com.status.statussaver.data.repository.WhatsAppStatusRepositoryImpl
import com.status.statussaver.ui.frags.status.whatsapp.WhatsAppViewModel
import com.status.statussaver.utils.MetadataExtractor

object WhatsAppModule {
    private var repository: WhatsAppStatusRepository? = null
    private var viewModel: WhatsAppViewModel? = null

    fun provideRepository(context: Context): WhatsAppStatusRepository {
        return repository ?: WhatsAppStatusRepositoryImpl(
            context.applicationContext,
            MetadataExtractor(context.applicationContext)
        ).also { repository = it }
    }

    fun provideViewModel(context: Context): WhatsAppViewModel {
        return viewModel ?: WhatsAppViewModel(
            provideRepository(context)
        ).also { viewModel = it }
    }

    fun clearViewModel() {
        viewModel = null
    }
}