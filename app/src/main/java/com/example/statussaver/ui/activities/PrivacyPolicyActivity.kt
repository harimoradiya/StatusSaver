package com.example.statussaver.ui.activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.statussaver.databinding.ActivityPrivacyPolicyBinding


class PrivacyPolicyActivity : AppCompatActivity() {


    private lateinit var binding: ActivityPrivacyPolicyBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPrivacyPolicyBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)


        binding.buttonBrowsePrivacyPolicyAndTermsOfService.setOnClickListener {
            startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://developer.android.com/")
                )
            )
        }

        binding.floatingButtonAgree.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }

    }
}