package com.example.statussaver.ui.adapters

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
import com.example.statussaver.R
import com.example.statussaver.data.interfaces.OnDeleteListener
import com.example.statussaver.ui.activities.SavedPreviewActivity
import com.example.statussaver.utils.Utils


class SavedStatusAdapter(
    private val activity: Activity?,
    private val mData: ArrayList<String>,private val onDeleteListener: OnDeleteListener
) : RecyclerView.Adapter<SavedStatusAdapter.ItemViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.row_download, parent, false)

        return ItemViewHolder(view)
    }

    override fun getItemCount(): Int {
        return mData.size
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val data = mData[position]
        Log.e("data",data)
        if (activity != null) {
            Glide.with(activity).load(data)
                .error(R.drawable.b)
                .into(holder.imageView)
        }
        if (Utils.isVideoFile(mData.get(position))) {
            holder.imagePlayer.visibility = View.VISIBLE
        }
        else{
            holder.imagePlayer.visibility = View.GONE
        }




        holder.cardView.setOnClickListener {
            val intent = Intent(activity, SavedPreviewActivity::class.java)
            if (Utils.isVideoFile(mData.get(position))) {
                intent.putExtra("type", "video")
            } else {
                intent.putExtra("type", "image")
            }
            intent.putExtra("path", mData.get(position))
//            intent.putParcelableArrayListExtra("images", mData)
            intent.putExtra("position", mData.get(position))
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
           holder.llshareImage.setOnClickListener {
               share(data, activity)
           }
            holder.lldeleteImage.setOnClickListener {
                onDeleteListener.getClick(position)
            }
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
        val imagePlayer: ImageView = itemView.findViewById(R.id.iconplayer)
        val cardView: CardView = itemView.findViewById(R.id.card_view)
        val llshareImage: LinearLayout = itemView.findViewById(R.id.llshareImage)
        val lldeleteImage: LinearLayout = itemView.findViewById(R.id.lldeleteImage)

//        init {
//            cardView.setOnClickListener {
//                Log.e("click", absoluteAdapterPosition.toString())
//                val intent = Intent(
//                    activity,
//                    PreviewActivity::class.java
//                )
////                intent.putParcelableArrayListExtra("images", mData)
//                intent.putExtra("position", absoluteAdapterPosition)
//                intent.putExtra("statusdownload", "")
//                activity?.startActivity(intent)
//            }
//        }
    }


    fun share(path: String?, activity: Activity?) {
        if (activity != null) {
            Utils.mShare(path, activity)
        }
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