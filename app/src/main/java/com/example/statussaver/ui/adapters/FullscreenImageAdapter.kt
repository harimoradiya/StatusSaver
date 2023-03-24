package com.example.statussaver.ui.adapters

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import androidx.viewpager.widget.PagerAdapter
import com.bumptech.glide.Glide
import com.example.statussaver.R
import com.example.statussaver.data.model.StatusDataModel
import com.example.statussaver.ui.activities.VideoPreviewActivity
import com.example.statussaver.utils.Utils

class FullscreenImageAdapter(private val activity: Activity, private val mData: ArrayList<StatusDataModel>) :
    PagerAdapter() {
    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val itemView: View =
            LayoutInflater.from(activity).inflate(R.layout.preview_list_item, container, false)
        val imageView = itemView.findViewById<View>(R.id.imageView) as ImageView
        val iconplayer = itemView.findViewById<View>(R.id.iconplayer) as ImageView
        val data = mData[position]
        if (!Utils.getBack(
                data.filename,
                "((\\.mp4|\\.webm|\\.ogg|\\.mpK|\\.avi|\\.mkv|\\.flv|\\.mpg|\\.wmv|\\.vob|\\.ogv|\\.mov|\\.qt|\\.rm|\\.rmvb\\.|\\.asf|\\.m4p|\\.m4v|\\.mp2|\\.mpeg|\\.mpe|\\.mpv|\\.m2v|\\.3gp|\\.f4p|\\.f4a|\\.f4b|\\.f4v)$)"
            )?.isEmpty()!!
        ) {
            iconplayer.visibility = View.VISIBLE
        } else {
            iconplayer.visibility = View.GONE
        }
        Glide.with(activity).load(data.filename).into(imageView)
        iconplayer.setOnClickListener {
            if (!Utils.getBack(
                    data.filename,
                    "((\\.mp4|\\.webm|\\.ogg|\\.mpK|\\.avi|\\.mkv|\\.flv|\\.mpg|\\.wmv|\\.vob|\\.ogv|\\.mov|\\.qt|\\.rm|\\.rmvb\\.|\\.asf|\\.m4p|\\.m4v|\\.mp2|\\.mpeg|\\.mpe|\\.mpv|\\.m2v|\\.3gp|\\.f4p|\\.f4a|\\.f4b|\\.f4v)$)"
                )?.isEmpty()!!
            ) {
                val intent = Intent(activity, VideoPreviewActivity::class.java)
                intent.putExtra("videoPath", data.filename)
                itemView.context.startActivity(intent)
            }
        }
        container.addView(itemView)
        return itemView
    }

    override fun getCount(): Int {
        return mData.size
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as RelativeLayout)
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view === `object` as RelativeLayout
    }

}
