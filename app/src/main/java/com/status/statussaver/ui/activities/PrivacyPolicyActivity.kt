package com.status.statussaver.ui.activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.status.saver.video.databinding.ActivityPrivacyPolicyBinding
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
                    Uri.parse("https://twilighttechies.github.io/statussaver/privacy-app.html")
                )
            )
        }

        binding.floatingButtonAgree.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }

    }
}