package com.example.statussaver.ui.activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.documentfile.provider.DocumentFile
import com.example.statussaver.data.model.StatusDataModel
import com.example.statussaver.databinding.ActivityPreviewBinding
import com.example.statussaver.ui.adapters.FullscreenImageAdapter
import com.example.statussaver.utils.Utils
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.io.File
import kotlin.properties.Delegates


class PreviewActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPreviewBinding
    var imageList: ArrayList<StatusDataModel>? = null
    private var position by Delegates.notNull<Int>()

    var fullscreenImageAdapter: FullscreenImageAdapter? = null
    var statusdownload: String? = null
    var folderPath: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        );

        binding = ActivityPreviewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding = ActivityPreviewBinding.inflate(layoutInflater)
        setContentView(binding.root)
        imageList = intent.getParcelableArrayListExtra<StatusDataModel>("images")
        position = intent.getIntExtra("position", 0)
        statusdownload = intent.getStringExtra("statusdownload")
        folderPath = intent.getStringExtra("folderpath")

        Log.e("position", position.toString())

        Log.e("images", imageList?.size.toString())
        fullscreenImageAdapter = imageList?.let { FullscreenImageAdapter(this@PreviewActivity, it) }
        binding.viewPager.adapter = fullscreenImageAdapter
        binding.viewPager.currentItem = position

        binding.backIV.setOnClickListener {
            finish()
        }



        binding.share.setOnClickListener {
            if (imageList!!.size > 0) {
                Utils.shareFile(
                    this@PreviewActivity,
                    Utils.isVideoFile(this, imageList!![binding.viewPager.currentItem].filename),
                    imageList!![binding.viewPager.currentItem].filename
                )
            } else {
                finish()
            }
        }
        binding.repost.setOnClickListener {
            imageList?.get(binding.viewPager.currentItem)?.let { it1 ->
                imageList?.get(binding.viewPager.currentItem)?.filename?.let { it2 ->
                    Utils.isVideoFile(
                        this, it2
                    )
                }?.let { it3 ->
                    Utils.repostWhatsApp(
                        this,
                        it3, it1.filename
                    )
                }
            };

        }


    }




}
