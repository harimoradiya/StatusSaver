package com.status.statussaver.ui.activities

import android.annotation.SuppressLint
import android.media.MediaPlayer.OnCompletionListener
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.MediaController
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.status.saver.video.R
import com.status.saver.video.databinding.ActivityPreviewBinding
import com.status.statussaver.data.model.StatusDataModel
import com.status.statussaver.ui.activities.PreviewActivity
import com.status.statussaver.ui.adapters.FullscreenImageAdapter
import com.status.statussaver.utils.Utils
import java.io.File
import kotlin.properties.Delegates


class SavedPreviewActivity : BaseActivity() {

    private lateinit var binding: ActivityPreviewBinding

    var iswapp:Boolean? =null
    var fullscreenImageAdapter : FullscreenImageAdapter<String>? = null
    var savedImagesList : ArrayList<String>? = null
    var position by Delegates.notNull<Int>()




    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        );

        binding = ActivityPreviewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        position = intent.getIntExtra("position",0)
        savedImagesList = intent.getStringArrayListExtra("saved_list")
//        val path = intent.getStringExtra("path")
//        val type = intent.getStringExtra("type")
//        val activity = intent.getStringExtra("activity")
//        iswapp = intent.getBooleanExtra("iswapp",true)

        fullscreenImageAdapter = FullscreenImageAdapter<String>(
            activity = this,
            mData = savedImagesList ?: arrayListOf(),
            getFilePath = { it } // Lambda to extract filepath from your model
        )

        binding.viewPager.adapter = fullscreenImageAdapter
        binding.viewPager.currentItem = position
        binding.viewPager.post {
            supportStartPostponedEnterTransition()
        }


        binding.fbSave.icon = resources.getDrawable(R.drawable.baseline_delete_forever_24,null)

        binding.fbSave.text = "Delete"


        binding.fbSave.setOnClickListener {
            val alertDialog = MaterialAlertDialogBuilder(this@SavedPreviewActivity)
                .setTitle("Delete")
                .setMessage("Are you sure you want to delete this file?")
                .setPositiveButton("Yes") { dialog, which ->
                    dialog.dismiss()
                    val file = File(savedImagesList?.get(binding.viewPager.currentItem))
                    if (file.exists()) {
                        file.delete()
                        fullscreenImageAdapter?.notifyDataSetChanged()
                        Toast.makeText(this, "Deleted successfully!", Toast.LENGTH_SHORT).show()

                        finish()
                    }
                }
                .setNegativeButton("No") { dialogInterface, i ->
                    dialogInterface.dismiss()
                }
                .create()

            alertDialog.show()
        }
        binding.backIV.setOnClickListener {
            finish()
        }


        binding.share.setOnClickListener {
            if (savedImagesList!!.isNotEmpty()) {
                Utils.shareFile(
                    this@SavedPreviewActivity,
                    Utils.isVideoFile(this, savedImagesList!![binding.viewPager.currentItem].toString()),
                    savedImagesList!![binding.viewPager.currentItem].toString()
                )
            } else {
                finish()
            }
        }
        binding.repost.setOnClickListener {
            savedImagesList?.get(binding.viewPager.currentItem)?.let { it1 ->
                savedImagesList?.get(binding.viewPager.currentItem)?.toString()?.let { it2 ->
                    Utils.isVideoFile(
                        this, it2
                    )
                }?.let { it3 ->
                    Utils.repostWhatsApp(
                        this,
                        it3, it1.toString(), iswapp!!
                    )
                }
            };

        }



    }




}
