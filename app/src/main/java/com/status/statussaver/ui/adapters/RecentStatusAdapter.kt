package com.status.statussaver.ui.adapters

import android.content.Context
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.cardview.widget.CardView
import com.google.android.material.button.MaterialButton
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.snackbar.Snackbar
import com.status.saver.video.R
import com.status.statussaver.data.interfaces.OnCardViewItemClickListenerAds
import com.status.statussaver.data.model.StatusDataModel
import com.status.statussaver.utils.Utils
import java.io.File


class RecentStatusAdapter(
    private val context: Context,
    private val mData: ArrayList<StatusDataModel>,
    private val iswapp: Boolean,
    private val onItemClickListenerAds: OnCardViewItemClickListenerAds
) : RecyclerView.Adapter<RecentStatusAdapter.ItemViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.row_recent, parent, false)

        return ItemViewHolder(view)
    }


    override fun getItemCount(): Int {
        return mData.size
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val data = mData[position]
        Log.e("data", data.filepath + "->>>>>>>>>" + data.filename);
        val imagesDir: String
        if (iswapp) {
            imagesDir =
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                    .toString() + File.separator + context.resources.getString(R.string.app_name) + File.separator + "Whatsapp"

        } else {
            imagesDir =
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                    .toString() + File.separator + context.resources.getString(R.string.app_name) + File.separator + "WABusiness"
        }
        val mediaStorageDir: File = File(imagesDir)
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                return
            }
        }
        val path = data.filepath
        val filename = path.substring(path.lastIndexOf("/") + 1)
        val file = File(mediaStorageDir.path + File.separator + File(filename))
        Log.e("Filename", file.path)
        if (file.exists()) {
//            holder.downloadButton.visibility = View.GONE
        }
        if (!Utils.getBack(
                data.filename,
                "((\\.mp4|\\.webm|\\.ogg|\\.mpK|\\.avi|\\.mkv|\\.flv|\\.mpg|\\.wmv|\\.vob|\\.ogv|\\.mov|\\.qt|\\.rm|\\.rmvb\\.|\\.asf|\\.m4p|\\.m4v|\\.mp2|\\.mpeg|\\.mpe|\\.mpv|\\.m2v|\\.3gp|\\.f4p|\\.f4a|\\.f4b|\\.f4v)$)"
            )!!
                .isEmpty()
        ) {
            holder.imagePlayer.visibility = View.VISIBLE
        } else {
            holder.imagePlayer.visibility = View.GONE
        }
        Glide.with(context).load(data.filepath).apply(
            RequestOptions().placeholder(R.color.colorIconShortcutForeground)
                .dontAnimate()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .error(android.R.color.black).optionalTransform(RoundedCorners(5))
        ).into(holder.imageView)




    }


    // Holds the views for adding it to image and text
    inner class ItemViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {

        val imageView: ImageView = itemView.findViewById(R.id.imageView)
        val imagePlayer: ImageView = itemView.findViewById(R.id.iconplayer)
//        val cardView: CardView = itemView.findViewById(R.id.card_view)
//        val downloadButton: MaterialButton = itemView.findViewById(R.id.download_button)

        init {
            imageView.setOnClickListener {
                Log.e("click", absoluteAdapterPosition.toString())
                onItemClickListenerAds.onCardViewListener(mData, absoluteAdapterPosition, iswapp)
            }

        }
    }



    companion object {
        const val ITEMS_PER_AD = 7
        const val AD_FREQUENCY = ITEMS_PER_AD + 1
        const val AD_VIEWTYPE = 0
        const val CARD_VIEWTYPE = 1
    }
}


