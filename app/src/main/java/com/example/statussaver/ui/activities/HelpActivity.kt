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
import com.example.statussaver.databinding.ActivityHelpBinding
import com.google.android.material.snackbar.Snackbar

class HelpActivity: AppCompatActivity() {

    private lateinit var binding: ActivityHelpBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHelpBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.tbHelp)
        supportActionBar?.setDisplayShowTitleEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)


        supportActionBar?.title = "Help"



    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId ==android.R.id.home){
            finish()
        }

        return super.onOptionsItemSelected(item)

    }
}