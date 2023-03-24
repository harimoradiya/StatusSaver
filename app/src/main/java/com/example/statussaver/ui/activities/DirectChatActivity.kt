package com.example.statussaver.ui.activities

import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.statussaver.databinding.ActivityDchatBinding
import com.google.android.material.snackbar.Snackbar

class DirectChatActivity: AppCompatActivity() {

    private lateinit var binding: ActivityDchatBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDchatBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.tbDrirectChat)
        supportActionBar?.setDisplayShowTitleEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)


        supportActionBar?.title = "Direct Chat"

        binding.mbSend.setOnClickListener(View.OnClickListener {
            if (!appInstall("com.whatsapp.w4b") && !appInstall("com.whatsapp")) {
                Snackbar.make(
                    binding.clDirectChat, "Please Install WhatsApp",
                    Snackbar.LENGTH_SHORT
                )
                    .show()
            } else {
                if (TextUtils.isEmpty(binding.etPhoneNumber.text.toString())) {
                    Snackbar.make(
                        binding.clDirectChat, "Enter phone number",
                        Snackbar.LENGTH_SHORT
                    )
                        .show()
                    return@OnClickListener
                }
               else if (TextUtils.isEmpty(binding.etMsg.text.toString())) {
                    Snackbar.make(
                        binding.clDirectChat, "Enter Message",
                        Snackbar.LENGTH_SHORT
                    )
                        .show()
                    return@OnClickListener
                } else {
                    sendMessage(binding.etMsg.text.toString())
                    return@OnClickListener
                }
            }
        })


    }

    fun sendMessage(message: String) {
        try {
            val intent = Intent("android.intent.action.VIEW")
            intent.data = Uri.parse(
                "http://api.whatsapp.com/send?phone=" + binding.ccp.selectedCountryCode + binding.etPhoneNumber.text
                    .toString() + "&text=" + message
            )
            startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(
                this@DirectChatActivity,
                "Number not available on whatsapp",
                Toast.LENGTH_SHORT
            ).show()
        }
    }


    fun appInstall(targetPackage: String): Boolean {
        val packages: List<ApplicationInfo>
        val pm: PackageManager
        pm = packageManager
        packages = pm.getInstalledApplications(0)
        for (packageInfo in packages) {
            if (packageInfo.packageName == targetPackage) return true
        }
        return false
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId ==android.R.id.home){
            finish()
        }

        return super.onOptionsItemSelected(item)

    }
}