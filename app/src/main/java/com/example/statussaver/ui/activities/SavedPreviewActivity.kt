package com.example.statussaver.ui.activities

import android.media.MediaPlayer.OnCompletionListener
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.MediaController
import androidx.appcompat.app.AppCompatActivity
import com.example.statussaver.R
import com.example.statussaver.data.model.StatusDataModel
import com.example.statussaver.databinding.ActivitySavedPreviewBinding
import com.example.statussaver.utils.Utils


class SavedPreviewActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySavedPreviewBinding
    var imageList: ArrayList<StatusDataModel>? = null


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        );

        binding = ActivitySavedPreviewBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val video_view = binding.vvVideo
        val iv_photo = binding.ivPhoto
        val path = intent.getStringExtra("path")
        val type = intent.getStringExtra("type")
        val activity = intent.getStringExtra("activity")


        if (type == "video") {
            video_view.visibility = View.VISIBLE
            val mc = MediaController(this)
            mc.setAnchorView(video_view)
            mc.setMediaPlayer(video_view)
            video_view.setMediaController(mc)
            video_view.setVideoURI(Uri.parse(path))
            video_view.start()
            video_view.setOnCompletionListener(OnCompletionListener { mediaPlayer -> mediaPlayer.start() })
        } else if (type == "image") {
            iv_photo.visibility = View.VISIBLE
            iv_photo.setImageURI(Uri.parse(path))
        }

        binding.backIV.setOnClickListener {
            finish()
        }



        binding.share.setOnClickListener {
            Utils.share(this@SavedPreviewActivity, resources.getString(R.string.app_name), path)

        }
        binding.repost.setOnClickListener {

            if (path != null) {
                Utils.repostWhatsApp(
                    this,
                    false, path
                )
            }

        }


    }




}
