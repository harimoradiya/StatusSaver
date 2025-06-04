package com.status.statussaver.ui.activities

import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.preference.PreferenceManager
import android.text.Html
import android.view.MenuItem
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.ui.AppBarConfiguration
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.status.saver.video.BuildConfig
import com.status.saver.video.R
import com.status.saver.video.databinding.ActivitySettingsBinding
import com.status.statussaver.utils.Constant
import com.status.statussaver.utils.LocaleHelper


class SettingActivity : BaseActivity() {

    private lateinit var binding: ActivitySettingsBinding
    private lateinit var sharedPreferences: SharedPreferences
    var banner_id: String? = null
    var ads_status: String? = null

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)

        ads_status = sharedPreferences.getString(Constant.adStatus, "")
        banner_id = sharedPreferences.getString(Constant.adx_banner_id, "")

        setupToolbar()
        setupViews()
        setupClickListeners()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.apply {
            setDisplayShowTitleEnabled(true)
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
            title = getString(R.string.settings) // Use string resource
        }
    }

    private fun setupViews() {
        binding.textViewAppVersion.text = getString(R.string.app_version, BuildConfig.VERSION_NAME)

        // Update current language display
        binding.tvLanguage.text = LocaleHelper.getCurrentLanguageName(this)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun setupClickListeners() {
        binding.cardViewLanguage.setOnClickListener {
            showLanguageSelectionDialog()
        }

        binding.cardViewPrivacy.setOnClickListener {
            val browserIntent = Intent(
                Intent.ACTION_VIEW,
                Uri.parse("https://twilighttechies.github.io/statussaver/privacy-app.html")
            )
            startActivity(browserIntent)
        }

        binding.chipShare.setOnClickListener {
            shareApp()
        }

        binding.chipRateDev.setOnClickListener {
            rateApp()
        }

        binding.cardViewFeedback.setOnClickListener {
            sendFeedback()
        }

        binding.cardViewDisclaimer.setOnClickListener {
            showDisclaimer()
        }
    }

    private fun showLanguageSelectionDialog() {
        try {
            val languageMap = LocaleHelper.getLanguageMap()
            val languages = languageMap.values.toTypedArray()
            val currentLangCode = LocaleHelper.getPersistedLanguage(this)
            val currentIndex = languageMap.entries.indexOfFirst { it.key == currentLangCode }

            MaterialAlertDialogBuilder(this)
                .setTitle(getString(R.string.select_language))
                .setSingleChoiceItems(languages, currentIndex) { dialog, which ->
                    val selectedLangCode = languageMap.entries.elementAt(which).key
                    dialog.dismiss()

                    // Show progress dialog during language change
                    val progressDialog = MaterialAlertDialogBuilder(this)
                        .setMessage(getString(R.string.changing_language))
                        .setCancelable(false)
                        .create()
                    progressDialog.show()

                    /* Change language with slight delay to show progress */
                    Handler(Looper.getMainLooper()).postDelayed({
                        progressDialog.dismiss()
                        changeLanguage(selectedLangCode)
                    }, 500)
                }
                .setNegativeButton(getString(R.string.cancel)) { dialog, _ ->
                    dialog.dismiss()
                }
                .show()
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Error showing language dialog", Toast.LENGTH_SHORT).show()
        }
    }

    private fun shareApp() {
        try {
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name))
                val shareMessage = getString(R.string.share_message, BuildConfig.APPLICATION_ID)
                putExtra(Intent.EXTRA_TEXT, shareMessage)
            }
            startActivity(Intent.createChooser(shareIntent, getString(R.string.choose_app)))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun rateApp() {
        try {
            startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("market://details?id=${BuildConfig.APPLICATION_ID}")
                )
            )
        } catch (e: ActivityNotFoundException) {
            startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://play.google.com/store/apps/details?id=${BuildConfig.APPLICATION_ID}")
                )
            )
        }
    }

    private fun sendFeedback() {
        val emailIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/email"
            putExtra(Intent.EXTRA_EMAIL, arrayOf("marcojansen723@gmail.com"))
            putExtra(Intent.EXTRA_SUBJECT, getString(R.string.feedback))
            putExtra(Intent.EXTRA_TEXT, getString(R.string.feedback_template))
        }
        startActivity(Intent.createChooser(emailIntent, getString(R.string.send_feedback)))
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun showDisclaimer() {
        MaterialAlertDialogBuilder(this)
            .setTitle(getString(R.string.disclaimer))
            .setMessage(Html.fromHtml(getString(R.string.disclaimer_text), Html.FROM_HTML_MODE_LEGACY))
            .setNegativeButton(getString(R.string.ok)) { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
        }
        return super.onOptionsItemSelected(item)
    }
}
