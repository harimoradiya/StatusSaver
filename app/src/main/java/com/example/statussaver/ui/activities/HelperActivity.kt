package com.example.statussaver.ui.activities

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen


class HelperActivity : AppCompatActivity() {

    private lateinit var preferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        preferences = getSharedPreferences("startup", MODE_PRIVATE)

    }

    override fun onResume() {
        super.onResume()
        if (preferences.getBoolean("value", true)) {
            preferences.edit().putBoolean("value", false).apply()
            startActivity(Intent(this, PrivacyPolicyActivity::class.java))
        } else {
            startActivity(Intent(this, MainActivity::class.java))
        }
    }
}