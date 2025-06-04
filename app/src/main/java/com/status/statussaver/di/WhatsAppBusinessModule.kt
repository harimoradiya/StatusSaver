package com.status.statussaver.di

import android.content.Context
import com.status.statussaver.data.repository.WhatsAppBusinessStatusRepository
import com.status.statussaver.data.repository.WhatsAppBusinessStatusRepositoryImpl
import com.status.statussaver.ui.frags.status.whatsappB.WhatsAppBusinessViewModel

object WhatsAppBusinessModule {
    private var repository: WhatsAppBusinessStatusRepository? = null
    private var viewModel: WhatsAppBusinessViewModel? = null

    @Synchronized
    fun provideRepository(context: Context): WhatsAppBusinessStatusRepository {
        return repository ?: WhatsAppBusinessStatusRepositoryImpl(context.applicationContext).also {
            repository = it
        }
    }

    @Synchronized
    fun provideViewModel(context: Context): WhatsAppBusinessViewModel {
        return viewModel ?: WhatsAppBusinessViewModel(provideRepository(context)).also {
            viewModel = it
        }
    }

    fun clearViewModel() {
        viewModel = null
    }
}