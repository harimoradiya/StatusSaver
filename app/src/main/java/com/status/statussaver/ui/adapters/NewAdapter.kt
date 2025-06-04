package com.status.statussaver.ui.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.google.android.material.chip.Chip
import com.google.android.material.snackbar.Snackbar
import com.status.saver.video.R
import com.status.statussaver.data.interfaces.OnCardViewItemClickListenerAds
import com.status.statussaver.data.model.StatusDataModel
import com.status.statussaver.utils.Utils
import java.io.File
import java.util.*

class NewAdapter(
    private val context: Context,
    private val iswapp: Boolean,
    private val onItemClickListenerAds: OnCardViewItemClickListenerAds,
    private val isShimmer: Boolean = false
) : ListAdapter<StatusDataModel, RecyclerView.ViewHolder>(StatusDiffCallback()) {

    private val glideRequestOptions = RequestOptions()
        .placeholder(R.color.colorIconShortcutBackground)
        .diskCacheStrategy(DiskCacheStrategy.ALL)
        .centerCrop()

    inner class ShimmerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    inner class NewViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val statusImage: ImageView = itemView.findViewById(R.id.imageView)

        val statusTypeChip: MaterialCardView = itemView.findViewById(R.id.iconplayer)


        fun bind(data: StatusDataModel) {
            Log.d("NewAdapter", "bind: ${data.toString()}")
            Glide.with(context)
                .load(data.filepath)
                .apply(glideRequestOptions)
                .into(statusImage)


            val isVideo = isVideoFile(data.filename)
            statusTypeChip.visibility = if (isVideo) View.VISIBLE else View.GONE


            val isDownloaded = isFileDownloaded(data.filepath)
//            downloadStatusChip.text = if (isDownloaded) "Downloaded" else "Not Downloaded"
//            downloadStatusChip.setChipBackgroundColorResource(
//                if (isDownloaded) android.R.color.holo_green_light
//                else android.R.color.holo_orange_light
//            )

            statusImage.setOnClickListener {
                onItemClickListenerAds.onCardViewListener(ArrayList(currentList), adapterPosition, iswapp)
            }

//            downloadButton.setOnClickListener {
//                if (Utils.copyFileInSavedDir(context, data.filepath, true)) {
//                    downloadStatusChip.text = "Downloaded"
//                    downloadStatusChip.setChipBackgroundColorResource(android.R.color.holo_green_light)
//                    Snackbar.make(itemView, "Saved successfully!", Snackbar.LENGTH_SHORT).show()
//                }
//            }


        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (isShimmer) VIEW_TYPE_SHIMMER else VIEW_TYPE_ITEM
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_SHIMMER -> ShimmerViewHolder(
                LayoutInflater.from(parent.context).inflate(R.layout.shimmer_recent, parent, false)
            )
            else -> NewViewHolder(
                LayoutInflater.from(parent.context).inflate(R.layout.row_recent, parent, false)
            )
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is NewViewHolder && !isShimmer) {
            holder.bind(getItem(position))
        }
    }

    companion object {
        private const val VIEW_TYPE_SHIMMER = 0
        private const val VIEW_TYPE_ITEM = 1
    }

    private fun isVideoFile(filename: String): Boolean {
        return filename.lowercase().matches(Regex(".+\\.(mp4|webm|ogg|mpk|avi|mkv|flv|mpg|wmv|vob|ogv|mov|qt|rm|rmvb|asf|m4p|m4v|mp2|mpeg|mpe|mpv|m2v|3gp|f4p|f4a|f4b|f4v)$"))
    }

    private fun formatTimeAgo(date: Date): String {
        val now = System.currentTimeMillis()
        val diff = now - date.time
        return when {
            diff < 60_000 -> "Just now"
            diff < 3600_000 -> "${diff / 60_000}m ago"
            diff < 86400_000 -> "${diff / 3600_000}h ago"
            else -> "${diff / 86400_000}d ago"
        }
    }

    private fun formatExpiryTime(expiryDate: Date): String {
        val now = System.currentTimeMillis()
        val diff = expiryDate.time - now
        return when {
            diff <= 0 -> "Expired"
            diff < 3600_000 -> "${diff / 60_000}m"
            else -> "${diff / 3600_000}h"
        }
    }

    private fun isFileDownloaded(filepath: String): Boolean {
        val sourceFileName = File(filepath).name
        val savedDir = Utils.downloadWhatsAppDir
        val savedFile = File(savedDir, sourceFileName)
        return savedFile.exists()
    }
}

class StatusDiffCallback : DiffUtil.ItemCallback<StatusDataModel>() {
    override fun areItemsTheSame(oldItem: StatusDataModel, newItem: StatusDataModel): Boolean {
        return oldItem.filepath == newItem.filepath
    }

    override fun areContentsTheSame(oldItem: StatusDataModel, newItem: StatusDataModel): Boolean {
        return oldItem == newItem
    }
}