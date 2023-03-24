package com.example.statussaver.ui.activities

import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import android.widget.MediaController
import android.widget.VideoView
import androidx.appcompat.app.AppCompatActivity
import com.example.statussaver.databinding.ActivityVideoPreviewBinding

class VideoPreviewActivity: AppCompatActivity() {

    var displayVV: VideoView? = null
    var videoPath: String? = null

private lateinit var binding: ActivityVideoPreviewBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        );

        binding = ActivityVideoPreviewBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.backIV.setOnClickListener {
           onBackPressed()
        }
        videoPath = intent.getStringExtra("videoPath")

        binding.displayVV.setVideoPath(videoPath)

        val mediaController = MediaController(this)
        mediaController.setAnchorView(displayVV)

        binding.displayVV.setMediaController(mediaController)

        binding.displayVV.start()
    }




}