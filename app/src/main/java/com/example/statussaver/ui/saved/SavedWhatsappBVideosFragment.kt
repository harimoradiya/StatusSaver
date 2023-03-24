package com.example.statussaver.ui.saved

import android.content.IntentSender
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.statussaver.R
import com.example.statussaver.data.interfaces.OnDeleteListener
import com.example.statussaver.data.model.StatusDataModel
import com.example.statussaver.databinding.FragmentPhotosBinding
import com.example.statussaver.ui.adapters.SavedStatusAdapter
import com.example.statussaver.utils.Utils
import kotlinx.coroutines.*
import me.zhanghai.android.fastscroll.FastScrollerBuilder
import org.apache.commons.io.comparator.LastModifiedFileComparator
import java.io.File
import java.util.*
import kotlin.collections.ArrayList
import kotlin.coroutines.CoroutineContext

/**
 * A simple [Fragment] subclass.
 * Use the [SavedWhatsappBVideosFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SavedWhatsappBVideosFragment : Fragment(), OnDeleteListener {
    private var _binding: FragmentPhotosBinding? = null
    private val binding get() = _binding!!
    var mLayoutManager: RecyclerView.LayoutManager? = null
    var statusDataModel: java.util.ArrayList<StatusDataModel> = java.util.ArrayList()
    var filesToDelete: java.util.ArrayList<StatusDataModel> = java.util.ArrayList()
    var myAdapter: SavedStatusAdapter? = null
    var file: File? = null
    var images = java.util.ArrayList<String>()
    var delete_position = 0
    var id: Long = 0
    var path: File? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentPhotosBinding.inflate(inflater, container, false)
        val swipeRefreshLayout = binding.swipeToRefresh
        FastScrollerBuilder(binding.myRecyclerView).build()
//        binding.myRecyclerView.setHasFixedSize(true)
//        mLayoutManager = GridLayoutManager(activity, 2)
//        binding.myRecyclerView.layoutManager = mLayoutManager

        swipeRefreshLayout.setOnRefreshListener {
            loadMedia()

            swipeRefreshLayout.isRefreshing = false


        }


        return binding.root
    }


    override fun onResume() {
        super.onResume()
        loadMedia()
        myAdapter?.notifyDataSetChanged()
    }

    fun loadMedia() {
        try {
            images.clear()
            path = File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                    .toString() + File.separator + resources.getString(R.string.app_name) + File.separator + "WABusiness"
            )

            if (path!!.exists()){
                if (path!!.isDirectory) {
                    val listFile: Array<File> = path!!.listFiles()
                    for (i in listFile.indices.reversed()) {
                        images.add(listFile[i].absolutePath)
                    }
                }
            }
            if (images.size == 0) {

                binding.isEmptyList.setVisibility(View.VISIBLE)
                binding.txt.text = "No data available for this moment."

                return
            }
            binding.myRecyclerView.visibility = View.VISIBLE
            binding.isEmptyList.setVisibility(View.GONE)

            myAdapter = SavedStatusAdapter(
                requireActivity(),
                images,
                this@SavedWhatsappBVideosFragment
            )
            binding.myRecyclerView.adapter = myAdapter

            binding.myRecyclerView.setLayoutManager(
                GridLayoutManager(
                    context, 2
                )
            )
            binding.myRecyclerView.setAdapter(myAdapter)
            binding.myRecyclerView.setItemViewCacheSize(images.size)
        } catch (e: Exception) {


            Log.e("error", e.toString())
        }
    }


    @RequiresApi(Build.VERSION_CODES.R)
    override fun getClick(position: Int) {


        delete_position = position

        val type: String
        type = if (Utils.isVideoFile(images[position])) {
            "video"
        } else {
            "image"
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val uri: Uri = Utils.getImageUriId(requireActivity(), images[position], type, id)
            try {
                Utils.deleteAPI30(requireActivity(), uri)
            } catch (e: IntentSender.SendIntentException) {
                e.printStackTrace()
            }
        } else {
            val alertDialog = AlertDialog.Builder(
                requireActivity()
            )
            alertDialog.setTitle("Delete")
            alertDialog.setMessage("Are You Sure to Delete this File?")
            alertDialog.setPositiveButton(
                "Yes"
            ) { dialog, which ->
                dialog.dismiss()
                File(images[position]).delete()
                MediaScannerConnection.scanFile(activity, arrayOf(images[position]), null, null)
                images.removeAt(position)
                myAdapter?.notifyDataSetChanged()
                if (images.size == 0) {
                    binding.isEmptyList.visibility = View.VISIBLE
                    binding.txt.text = "You haven't save any status from Whatsapp Business!"
                }
                dialog.dismiss()
            }
            alertDialog.setNegativeButton(
                "No"
            ) { dialogInterface, i -> dialogInterface.dismiss() }
            alertDialog.show()
        }


    }


}