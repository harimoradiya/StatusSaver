package com.status.statussaver.ui.activities

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.preference.PreferenceManager
import android.util.Log
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.status.saver.video.R
import com.status.saver.video.databinding.ActivitySplashBinding
import com.status.statussaver.utils.Constant
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@SuppressLint("SplashActivity")
class SplashActivity : AppCompatActivity() {
    companion object {
        const val TAG = "SplashActivity"
    }


    var mContext: Context? = null
    private lateinit var admob_app_open_ad_unit_id: String
    private lateinit var ads_status: String
    private lateinit var myPreferences: SharedPreferences
    private lateinit var AppOpen: String
    private lateinit var AdStatus: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
            NextScreen()


    }


    private fun NextScreen(){

        CoroutineScope(Dispatchers.Main).launch {
            delay(1300L)
            val mIntent = Intent(this@SplashActivity, HelperActivity::class.java)
            startActivity(
                mIntent
            )
            finish()
        }

    }
}