package com.status.statussaver.ui.activities

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.transition.platform.MaterialContainerTransform
import com.status.saver.video.databinding.ActivityPreviewBinding
import com.status.statussaver.data.model.StatusDataModel
import com.status.statussaver.ui.adapters.FullscreenImageAdapter
import com.status.statussaver.utils.Utils
import kotlin.properties.Delegates


class PreviewActivity : BaseActivity() {
    private val TAG = PreviewActivity::class.simpleName
    private lateinit var binding: ActivityPreviewBinding
    var imageList: ArrayList<StatusDataModel>? = null
    private var position by Delegates.notNull<Int>()

    var fullscreenImageAdapter: FullscreenImageAdapter<StatusDataModel>? = null
    var statusdownload: String? = null
    var iswapp: Boolean? = null
    var folderPath: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportPostponeEnterTransition()
        window.requestFeature(Window.FEATURE_ACTIVITY_TRANSITIONS)
        window.sharedElementEnterTransition = MaterialContainerTransform().apply {
            duration = 300L
            scrimColor = Color.TRANSPARENT
            fadeMode = MaterialContainerTransform.FADE_MODE_THROUGH
        }
        binding = ActivityPreviewBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding = ActivityPreviewBinding.inflate(layoutInflater)
        setContentView(binding.root)
        imageList = intent.getParcelableArrayListExtra<StatusDataModel>("statusList")
        position = intent.getIntExtra("position", 0)
        statusdownload = intent.getStringExtra("statusdownload")
        folderPath = intent.getStringExtra("folderpath")
        iswapp = intent.getBooleanExtra("iswapp", true)

        Log.e(TAG, position.toString())
        Log.e(TAG, imageList?.size.toString())
        fullscreenImageAdapter = FullscreenImageAdapter<StatusDataModel>(
            activity = this,
            mData = imageList ?: arrayListOf(),
            getFilePath = {
                it.filepath
            } // Lambda to extract filepath from your model
        )
        binding.viewPager.adapter = fullscreenImageAdapter
        binding.viewPager.currentItem = position
        binding.viewPager.post {
            supportStartPostponedEnterTransition()
        }
        binding.backIV.setOnClickListener {
            finish()
        }
        binding.share.setOnClickListener {
            if (imageList!!.size > 0) {
                Utils.shareFile(
                    this@PreviewActivity,
                    Utils.isVideoFile(this, imageList!![binding.viewPager.currentItem].filepath),
                    imageList!![binding.viewPager.currentItem].filepath
                )
            } else {
                finish()
            }
        }
        binding.repost.setOnClickListener {
            imageList?.get(binding.viewPager.currentItem)?.let { it1 ->
                imageList?.get(binding.viewPager.currentItem)?.filepath?.let { it2 ->
                    Utils.isVideoFile(
                        this, it2
                    )
                }?.let { it3 ->
                    Utils.repostWhatsApp(
                        this,
                        it3, it1.filepath, iswapp!!
                    )
                }
            };

        }


        binding.fbSave.setOnClickListener {
            Utils.copyFileInSavedDir(this@PreviewActivity, imageList?.get(binding.viewPager.currentItem)?.filepath, true)
            Toast.makeText(this@PreviewActivity, "Saved successfully!", Toast.LENGTH_SHORT).show()
        }


    }


}
