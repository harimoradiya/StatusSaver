package com.status.statussaver.utils

import android.content.Context
import android.content.res.Configuration
import android.preference.PreferenceManager
import java.util.Locale

class LocaleHelper {
    companion object {
        private const val SELECTED_LANGUAGE = "app_language"

        fun setLocale(context: Context, languageCode: String): Context {
            persist(context, languageCode)
            return updateResources(context, languageCode)
        }

        fun getPersistedLanguage(context: Context): String {
            val preferences = PreferenceManager.getDefaultSharedPreferences(context)
            return preferences.getString(SELECTED_LANGUAGE, Locale.getDefault().language) ?: Locale.getDefault().language
        }

        private fun persist(context: Context, languageCode: String) {
            val preferences = PreferenceManager.getDefaultSharedPreferences(context)
            preferences.edit().putString(SELECTED_LANGUAGE, languageCode).apply()
        }

        private fun updateResources(context: Context, languageCode: String): Context {
            val locale = createLocale(languageCode)
            Locale.setDefault(locale)

            val configuration = Configuration(context.resources.configuration)
            configuration.setLocale(locale)
            configuration.setLayoutDirection(locale)

            return context.createConfigurationContext(configuration)
        }

        private fun createLocale(languageCode: String): Locale {
            return when (languageCode) {
                "zh-Hant" -> Locale("zh", "TW")
                "zh" -> Locale("zh", "CN")
                else -> Locale(languageCode)
            }
        }

        fun getCurrentLanguageName(context: Context): String {
            val languageCode = getPersistedLanguage(context)
            return getLanguageMap()[languageCode] ?: "English"
        }

        fun getLanguageMap(): Map<String, String> {
            return mapOf(
                "en" to "English",
                "hi" to "हिन्दी",
                "gu" to "ગુજરાતી",
                "bn" to "বাংলা",
                "mr" to "मराठी",
                "ta" to "தமிழ்",
                "te" to "తెలుగు",
                "kn" to "ಕನ್ನಡ",
                "ml" to "മലയാളം",
                "ur" to "اُردُو‎",
                "pa" to "ਪੰਜਾਬੀ",
                "ar" to "العربية",
                "es" to "Español",
                "fr" to "Français",
                "de" to "Deutsch",
                "it" to "Italiano",
                "ru" to "Русский",
                "zh" to "简体中文",
                "zh-Hant" to "繁體中文",
                "ja" to "日本語",
                "ko" to "한국어",
                "pt" to "Português",
                "tr" to "Türkçe",
                "vi" to "Tiếng Việt",
                "id" to "Bahasa Indonesia",
                "th" to "ไทย",
                "nl" to "Nederlands",
                "el" to "Ελληνικά",
                "he" to "עברית",
                "tl" to "Filipino"
            )
        }
    }
}