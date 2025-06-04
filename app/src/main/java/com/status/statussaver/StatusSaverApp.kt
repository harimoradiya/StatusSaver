package com.status.statussaver

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import android.os.Build
import android.preference.PreferenceManager
import android.util.Log
import com.status.statussaver.utils.LocaleHelper
import java.util.*

class StatusSaverApp : Application() {

    override fun onCreate() {
        super.onCreate()
        // Don't call setupLanguage here, it's handled in attachBaseContext
    }

    override fun attachBaseContext(base: Context) {
        val languageCode = LocaleHelper.getPersistedLanguage(base)
        val context = LocaleHelper.setLocale(base, languageCode)
        super.attachBaseContext(context)
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        val languageCode = LocaleHelper.getPersistedLanguage(this)
        LocaleHelper.setLocale(this, languageCode)
    }
}