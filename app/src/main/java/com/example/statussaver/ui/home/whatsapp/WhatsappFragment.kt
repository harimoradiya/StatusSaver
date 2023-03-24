package com.example.statussaver.ui.home.whatsapp

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Build
import android.os.Build.VERSION.SDK_INT
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.storage.StorageManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.documentfile.provider.DocumentFile
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.statussaver.data.model.StatusDataModel
import com.example.statussaver.databinding.FragmentWhatsappBinding
import com.example.statussaver.ui.adapters.RecentStatusAdapter
import com.example.statussaver.utils.SharedPrefs
import com.example.statussaver.utils.SharedPrefs.getWATree
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.*
import me.zhanghai.android.fastscroll.FastScrollerBuilder
import java.io.File
import kotlin.coroutines.CoroutineContext


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [WhatsappFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class WhatsappFragment : Fragment() {

    private var _binding: FragmentWhatsappBinding? = null
    private val binding get() = _binding!!
    private var REQUEST_ACTION_OPEN_DOCUMENT_TREE: Int = 101
    var statusImageList = java.util.ArrayList<StatusDataModel>()
    var filesToDelete: ArrayList<StatusDataModel> = ArrayList<StatusDataModel>()
    var mLayoutManager: RecyclerView.LayoutManager? = null
    var myAdapter: RecentStatusAdapter? = null
    private lateinit var sharedPreferences: SharedPreferences
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentWhatsappBinding.inflate(inflater, container, false)
        val swipeRefreshLayout = binding.swipeToRefresh
        FastScrollerBuilder(binding.myRecyclerView).build()
        binding.myRecyclerView.setHasFixedSize(true)
        mLayoutManager = GridLayoutManager(activity, 2)
        binding.myRecyclerView.layoutManager = mLayoutManager
        swipeRefreshLayout.setOnRefreshListener {

            if (getWATree(activity) != "") {
                myAdapter?.notifyDataSetChanged()
                Presenter().execute()

            }
            swipeRefreshLayout.isRefreshing = false


        }
        sharedPreferences = requireContext().getSharedPreferences("simple", Context.MODE_PRIVATE)
        val s = sharedPreferences.getString("false", "")
        if (s == "true") {
            Presenter().execute()
        }



        binding.allowStorageTv.setOnClickListener {

            if (SharedPrefs.getWATree(requireContext()).equals("")) {

                if (SDK_INT >= Build.VERSION_CODES.Q) {
                    AllowStoragePermission()

                } else {
                    sharedPreferences =
                        requireContext().getSharedPreferences("simple", Context.MODE_PRIVATE)
                    val myEdit: SharedPreferences.Editor = sharedPreferences.edit()
                    myEdit.putString("false", "true")
                    myEdit.apply()
                    Presenter().execute()
                }

            } else {
                Presenter().execute()


            }


        }


        if (!SharedPrefs.getWATree(requireContext()).equals("")) {
            Log.e("Status", "Called")
            Presenter().execute()
        }

        return binding.root
    }


    @RequiresApi(Build.VERSION_CODES.Q)
    fun AllowStoragePermission() {
        val storageManager =
            requireContext().getSystemService(Context.STORAGE_SERVICE) as StorageManager
        val intent = storageManager.primaryStorageVolume.createOpenDocumentTreeIntent()

        val targetDirectory: String = getWhatsappFolder()!!
        if (targetDirectory != null) {
            var uri = intent.getParcelableExtra<Uri>("android.provider.extra.INITIAL_URI")
            var scheme = uri.toString()
            scheme = scheme.replace("/root/", "/document/")
            scheme += "%3A$targetDirectory"
            uri = Uri.parse(scheme)
            Log.w("TAG", "askPermission: uri::$uri")
            intent.putExtra("android.provider.extra.INITIAL_URI", uri)
            startActivityForResult(intent, 101)
        } else {
            Snackbar.make(
                binding.clMainTop, "Please Install WhatsApp For Download Status!",
                Snackbar.LENGTH_SHORT
            )
                .setAction("Click") {
                    val browserIntent =
                        Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse("https://play.google.com/store/apps/details?id=com.whatsapp")
                        )
                    startActivity(browserIntent)
                }.show()
        }

    }

    inner class Presenter : CoroutineScope {
        lateinit var allFiles: Array<DocumentFile?>
        private var job: Job = Job()
        override val coroutineContext: CoroutineContext
            get() = Dispatchers.Main + job // to run code in Main(UI) Thread

        // call this method to cancel a coroutine when you don't need it anymore,
        // e.g. when user closes the screen
        fun cancel() {
            job.cancel()
        }

        fun execute() = launch {
            onPreExecute()
            val result =
                doInBackground() // runs in background thread without blocking the Main Thread
            onPostExecute(result)
        }

        private suspend fun doInBackground(): String =
            withContext(Dispatchers.IO) { // to run code in Background Thread
                // do async work
                delay(1000) // simulate async work
                try {
                    allFiles = emptyArray()
                    statusImageList = ArrayList<StatusDataModel>()
                    allFiles = getFromSdcard()!!
                    for (i in allFiles.indices) {
                        if (!allFiles[i]?.uri.toString().contains(".nomedia")) {
                            statusImageList.add(
                                StatusDataModel(
                                    allFiles[i]?.uri.toString(),
                                    allFiles[i]?.name!!
                                )
                            )
                        }
                    }
                } catch (e: Exception) {
                    Log.e("error", e.toString())
                }
                return@withContext "SomeResult"
            }

        // Runs on the Main(UI) Thread
        private fun onPreExecute() {
            binding.linearProgressBar.visibility = View.VISIBLE
            binding.myRecyclerView.visibility = View.GONE
            binding.sAccessBtn.visibility = View.GONE
            // show progress
        }

        // Runs on the Main(UI) Thread
        private fun onPostExecute(result: String) {
            // hide progress
            Handler().postDelayed({
                if (activity != null) {
                    myAdapter = RecentStatusAdapter(requireContext(), statusImageList, true)
                    binding.myRecyclerView.adapter = myAdapter
                    binding.linearProgressBar.visibility = View.GONE
                    binding.myRecyclerView.visibility = View.VISIBLE
                }
            }, 300)

            if (statusImageList.size == 0) {
                binding.isEmptyList.visibility = View.VISIBLE
            } else {
                binding.isEmptyList.visibility = View.GONE
            }


        }

    }


    private fun getFromSdcard(): Array<DocumentFile?>? {
        val treeUri = SharedPrefs.getWATree((requireActivity()))
        val fromTreeUri =
            DocumentFile.fromTreeUri(requireContext().applicationContext, Uri.parse(treeUri))
        return if (fromTreeUri != null && fromTreeUri.exists() && fromTreeUri.isDirectory
            && fromTreeUri.canRead() && fromTreeUri.canWrite()
        ) {
            fromTreeUri.listFiles()
        } else {
            null
        }
    }

    private fun getWhatsappFolder(): String? {
        val oldPath =
            Environment.getExternalStorageDirectory().absolutePath + "/WhatsApp/Media/.Statuses"
        val newPath =
            Environment.getExternalStorageDirectory().absolutePath + "/Android/media/com.whatsapp/WhatsApp/Media/.Statuses"
        return if (File(oldPath).exists()) {
            "WhatsApp%2FMedia%2F.Statuses"
        } else if (File(newPath).exists()) {
            "Android%2Fmedia%2Fcom.whatsapp%2FWhatsApp%2FMedia%2F.Statuses"
        } else {
            null
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_ACTION_OPEN_DOCUMENT_TREE && resultCode == Activity.RESULT_OK) {
            val uri = data!!.data
            Log.e("onActivityResult: ", "" + data.data)
            try {
                requireContext().contentResolver
                    .takePersistableUriPermission(
                        uri!!,
                        Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                    )
            } catch (e: Exception) {
                e.printStackTrace()
            }
            SharedPrefs.setWATree(requireActivity(), uri.toString())
            Presenter().execute()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}