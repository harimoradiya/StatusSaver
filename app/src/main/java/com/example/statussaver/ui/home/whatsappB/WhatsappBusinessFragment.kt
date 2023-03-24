package com.example.statussaver.ui.home.whatsappB

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.storage.StorageManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.documentfile.provider.DocumentFile
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.statussaver.R
import com.example.statussaver.data.model.StatusDataModel
import com.example.statussaver.databinding.FragmentWhatsappBinding
import com.example.statussaver.ui.adapters.RecentStatusAdapter
import com.example.statussaver.utils.SharedPrefs
import com.example.statussaver.utils.Utils
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
 * Use the [WhatsappBusinessFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class WhatsappBusinessFragment : Fragment() {
    private var _binding: FragmentWhatsappBinding? = null
    private val binding get() = _binding!!
    private var REQUEST_ACTION_OPEN_DOCUMENT_TREE: Int = 101
    var statusImageList = java.util.ArrayList<StatusDataModel>()
    var filesToDelete: ArrayList<StatusDataModel> = ArrayList<StatusDataModel>()

    var mLayoutManager: RecyclerView.LayoutManager? = null
    var myAdapter: RecentStatusAdapter? = null
    var path: File? = null
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

            if (SharedPrefs.getWBTree(activity) != "") {
                myAdapter?.notifyDataSetChanged()
                Presenter().execute()

            }
            swipeRefreshLayout.isRefreshing = false


        }

        binding.allowStorageTv.setOnClickListener {

            if (SharedPrefs.getWBTree(requireContext()).equals("")) {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    AllowStoragePermission()

                }
                else {
                    Presenter().execute()
                }

            } else {
                Presenter().execute()
            }


        }


        if (!SharedPrefs.getWBTree(requireContext()).equals("")) {
            Log.e("Status", "Called")
            Presenter().execute()
        }

        return binding.root
    }


    @RequiresApi(Build.VERSION_CODES.Q)
    fun AllowStoragePermission() {
        binding.sAccessBtn.visibility = View.VISIBLE
        val storageManager = requireContext().getSystemService(Context.STORAGE_SERVICE) as StorageManager
        val intent = storageManager.primaryStorageVolume.createOpenDocumentTreeIntent()

        path = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                .toString() + File.separator + resources.getString(R.string.app_name) + File.separator + "WABusiness"
        )
        val targetDirectory = getWhatsappBusinessFolder()
        if (targetDirectory != null) {
            var uri = intent.getParcelableExtra<Uri>("android.provider.extra.INITIAL_URI")
            var scheme = uri.toString()
            scheme = scheme.replace("/root/", "/document/")
            scheme += "%3A$targetDirectory"
            uri = Uri.parse(scheme)
            Log.w("TAG", "askPermission: uri::$uri")
            intent.putExtra("android.provider.extra.INITIAL_URI", uri)
            startActivityForResult(intent, 101)
        }

        else if(path!!.exists()){
            Snackbar.make(
                binding.clMainTop, "You haven't setup your whatsapp business yet.",
                Snackbar.LENGTH_SHORT
            )
                .show()
        }

        else {
            Snackbar.make(
                binding.clMainTop, "Please Install WhatsApp Business For Download Status!",
                Snackbar.LENGTH_SHORT
            )
                .setAction("Click") {
                    val browserIntent =
                        Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse("https://play.google.com/store/apps/details?id=com.whatsapp.w4b")
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
                }catch (e: Exception) {
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
                    myAdapter = RecentStatusAdapter(requireContext(), statusImageList, false)
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
        val treeUri = SharedPrefs.getWBTree((requireActivity()))
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

    //    @RequiresApi(api = Build.VERSION_CODES.Q)
    //    private void askPermission() {
    //        StorageManager storageManager = (StorageManager) getContext().getSystemService(STORAGE_SERVICE);
    //        Intent intent = storageManager.getPrimaryStorageVolume().createOpenDocumentTreeIntent();
    //
    //        String targetDirectory = getWhatsappBusinessFolder();
    //        if (targetDirectory != null) {
    //            Uri uri = intent.getParcelableExtra("android.provider.extra.INITIAL_URI");
    //            String scheme = uri.toString();
    //            scheme = scheme.replace("/root/", "/document/");
    //            scheme += "%3A" + targetDirectory;
    //            uri = Uri.parse(scheme);
    //
    //            Log.w("TAG", "askPermission: uri::" + uri);
    //            intent.putExtra("android.provider.extra.INITIAL_URI", uri);
    //            startActivityForResult(intent, 101);
    //        } else {
    //
    //            Toast.makeText(getContext(), "Install Whatsapp Business First", Toast.LENGTH_SHORT).show();
    //
    //        }
    //    }
    private fun getWhatsappBusinessFolder(): String? {
        val oldBusinessPath =
            Environment.getExternalStorageDirectory().absolutePath + "/WhatsApp Business/Media/.Statuses"
        val newBusinessPath =
            Environment.getExternalStorageDirectory().absolutePath + "/Android/media/com.whatsapp.w4b/WhatsApp Business/Media/.Statuses"
        return if (File(oldBusinessPath).exists()) {
            "WhatsApp Business%2FMedia%2F.Statuses"
        } else if (File(newBusinessPath).exists()) {
            "Android%2Fmedia%2Fcom.whatsapp.w4b%2FWhatsApp Business%2FMedia%2F.Statuses"
        } else {
            null
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        super.onActivityResult(requestCode, resultCode, intent)
        if (requestCode == 101 && resultCode == -1 && intent!!.data != null) {
            val data = intent.data
            try {
                requireContext().contentResolver.takePersistableUriPermission(
                    data!!,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }
            SharedPrefs.setWBTree(context, data.toString())
            Presenter().execute()
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}