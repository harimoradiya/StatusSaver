package com.status.statussaver.ui.activities

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.status.statussaver.utils.LocaleHelper

abstract class BaseActivity : AppCompatActivity() {

    override fun attachBaseContext(newBase: Context) {
        val languageCode = LocaleHelper.getPersistedLanguage(newBase)
        val context = LocaleHelper.setLocale(newBase, languageCode)
        super.attachBaseContext(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Apply language on each activity creation
        val languageCode = LocaleHelper.getPersistedLanguage(this)
        LocaleHelper.setLocale(this, languageCode)
    }

    protected fun changeLanguage(languageCode: String) {
        if (languageCode != LocaleHelper.getPersistedLanguage(this)) {
            LocaleHelper.setLocale(this, languageCode)
            // Signal that language was changed
            setResult(RESULT_OK)
            recreateActivity()
        }
    }

    private fun recreateActivity() {
        // Use intent to restart the activity smoothly
        val intent = intent
        finish()
        startActivity(intent)
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }
}