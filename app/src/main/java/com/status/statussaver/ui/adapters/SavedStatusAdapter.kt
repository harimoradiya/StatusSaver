package com.status.statussaver.ui.adapters

import android.R.attr.path
import android.app.Activity
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
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.status.saver.video.R
import com.status.statussaver.data.interfaces.OnDeleteListener
import com.status.statussaver.ui.activities.SavedPreviewActivity
import com.status.statussaver.utils.Utils
import java.io.File
import java.net.URLDecoder


class SavedStatusAdapter(
    private val activity: Activity?,
    private val mData: ArrayList<String>, private val onDeleteListener: OnDeleteListener,
    private val iswapp: Boolean
) : RecyclerView.Adapter<SavedStatusAdapter.ItemViewHolder>() {
    val TAG = SavedStatusAdapter::class.simpleName

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


        Log.e(TAG, "data - $data")



        // Check if the file exists
        val file = File(data)
        if (!file.exists()) {
            Log.e(TAG, "File does not exist: $data")
            return
        }

        // Load the image using Glide
        if (activity != null) {
            Glide.with(activity)
                .load(file.path)
                .error(R.drawable.b)
                .into(holder.imageView)
        }



        if (Utils.isVideoFile(file.path)) {
            holder.imagePlayer.visibility = View.VISIBLE
        } else {
            holder.imagePlayer.visibility = View.GONE
        }




        holder.imageView.setOnClickListener {
            val intent = Intent(activity, SavedPreviewActivity::class.java)
            if (Utils.isVideoFile(mData.get(position))) {
                intent.putExtra("type", "video")
            } else {
                intent.putExtra("type", "image")
            }
            intent.putExtra("iswapp", iswapp)
            intent.putExtra("position", position)
            intent.putStringArrayListExtra("saved_list",mData)
            activity?.startActivity(intent)
        }
        //  Log.e("SavedStatusAdapter", data.filepath + "->>>>>>>>>" + data.filename);

//        var file = File(data.filename)
//        if (!file.isDirectory()) {
//            if (!Utils.getBack(
//                    data.filename,
//                    "((\\.mp4|\\.webm|\\.ogg|\\.mpK|\\.avi|\\.mkv|\\.flv|\\.mpg|\\.wmv|\\.vob|\\.ogv|\\.mov|\\.qt|\\.rm|\\.rmvb\\.|\\.asf|\\.m4p|\\.m4v|\\.mp2|\\.mpeg|\\.mpe|\\.mpv|\\.m2v|\\.3gp|\\.f4p|\\.f4a|\\.f4b|\\.f4v)$)"
//                )!!
//                    .isEmpty()
//            ) {
//                try {
//                    if (activity != null) {
//                        Glide.with(activity).load(file).apply(
//                            RequestOptions().placeholder(R.color.colorIconShortcutForeground).error(android.R.color.black)
//                                .optionalTransform(
//                                    RoundedCorners(1)
//                                )
//                        ).into(holder.imageView)
//                    }
//                } catch (e: Exception) {
//                    e.printStackTrace()
//                }
//                holder.imagePlayer.visibility = View.VISIBLE
//            } else if (!Utils.getBack(
//                    data.filename,
//                    "((\\.3ga|\\.aac|\\.aif|\\.aifc|\\.aiff|\\.amr|\\.au|\\.aup|\\.caf|\\.flac|\\.gsm|\\.kar|\\.m4a|\\.m4p|\\.m4r|\\.mid|\\.midi|\\.mmf|\\.mp2|\\.mp3|\\.mpga|\\.ogg|\\.oma|\\.opus|\\.qcp|\\.ra|\\.ram|\\.wav|\\.wma|\\.xspf)$)"
//                )!!
//                    .isEmpty()
//            ) {
//                holder.imagePlayer.setVisibility(View.GONE)
//            } else if (!Utils.getBack(
//                    data.filename,
//                    "((\\.jpg|\\.png|\\.gif|\\.jpeg|\\.bmp)$)"
//                )!!.isEmpty()
//            ) {
//                holder.imagePlayer.setVisibility(View.GONE)
//                if (activity != null) {
//                    Glide.with(activity).load(file).apply(
//                        RequestOptions().placeholder(R.color.colorIconShortcutForeground).error(android.R.color.black)
//                            .optionalTransform(
//                                RoundedCorners(1)
//                            )
//                    ).into(holder.imageView)
//                }
//            }
//
//
//        holder.llshareImage.setOnClickListener {
//            share(data, activity)
//        }
//        holder.lldeleteImage.setOnClickListener {
//
//            onDeleteListener.getClick(position)
//        }
//        }


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
        val imagePlayer: MaterialCardView = itemView.findViewById(R.id.iconplayer)
//        val llshareImage: MaterialButton = itemView.findViewById(R.id.shareButton)
//        val lldeleteImage: MaterialButton = itemView.findViewById(R.id.deleteButton)

    }


    fun share(path: String?, activity: Activity?) {
        if (activity != null) {
            Utils.mShare(path, activity)
        }
    }

    fun updateData(newData: ArrayList<String>) {
        mData.clear()
        mData.addAll(newData)
        notifyDataSetChanged()
    }

//    fun delete(position: Int, activity: Activity?) {
//        val alertDialog = AlertDialog.Builder(
//            activity!!
//        )
//        alertDialog.setTitle("Delete")
//        alertDialog.setMessage("Are You Sure to Delete this File?")
//        alertDialog.setPositiveButton(
//            "Yes"
//        ) { dialog, which ->
//            dialog.dismiss()
//            val file: File = File(mData[position].filename)
//            if (file.exists()) {
//                file.delete()
//                mData.removeAt(position)
//                notifyItemChanged(position)
//            }
//        }
//        alertDialog.setNegativeButton(
//            "No"
//        ) { dialogInterface, i -> dialogInterface.dismiss() }
//        alertDialog.show()
//    }

}