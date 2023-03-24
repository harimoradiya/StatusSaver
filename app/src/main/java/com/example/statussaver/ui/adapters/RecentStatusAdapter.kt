package com.example.statussaver.ui.adapters

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.example.statussaver.R
import com.example.statussaver.data.model.StatusDataModel
import com.example.statussaver.ui.activities.PreviewActivity
import com.example.statussaver.utils.Utils
import com.google.android.material.snackbar.Snackbar


class RecentStatusAdapter(
    private val context: Context,
    private val mData: ArrayList<StatusDataModel>,
    iswapp: Boolean
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

        var imagesDir: String
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
        Glide.with(context).load(data.filename).apply(
            RequestOptions().placeholder(R.color.colorIconShortcutForeground)
                .error(android.R.color.black).optionalTransform(RoundedCorners(5))
        ).into(holder.imageView)




        holder.downloadImage.setOnClickListener(View.OnClickListener {
            Utils.copyFileInSavedDir(context, data.filename, true)
            Snackbar.make(
                holder.cardView, "Saved successfully!",
                Snackbar.LENGTH_SHORT
            ).show()
        })


//        holder.cardView.setOnClickListener {
//            Log.e("click", item_position.toString())
//
//
//            val intent = Intent(
//                context,
//                PreviewActivity::class.java
//            )
//            intent.putParcelableArrayListExtra("images", mData)
//            intent.putExtra("position",item_position)
//            intent.putExtra("statusdownload", "")
//            context.startActivity(intent)
//
//        }



    }


    // Holds the views for adding it to image and text
   inner class ItemViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {

        val imageView: ImageView = itemView.findViewById(R.id.imageView)
        val imagePlayer: ImageView = itemView.findViewById(R.id.iconplayer)
        val cardView: CardView = itemView.findViewById(R.id.card_view)
        val downloadImage: LinearLayout = itemView.findViewById(R.id.ll_download)
        init {
            cardView.setOnClickListener{
            Log.e("click", absoluteAdapterPosition.toString())
                            val intent = Intent(
                context,
                PreviewActivity::class.java
            )
            intent.putParcelableArrayListExtra("images",mData)
            intent.putExtra("position",absoluteAdapterPosition)
            intent.putExtra("statusdownload", "")
            context.startActivity(intent)
            }
        }
    }

}