package com.status.statussaver.ui.frags.saved

import android.annotation.SuppressLint
import android.content.IntentSender
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import android.os.Build.VERSION
import android.os.Bundle
import android.os.Environment
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.google.android.material.snackbar.Snackbar

import com.status.saver.video.R
import com.status.saver.video.databinding.FragmentSavedStatusBinding
import com.status.statussaver.data.interfaces.OnDeleteListener
import com.status.statussaver.data.model.StatusDataModel
import com.status.statussaver.di.SavedWhatsappModule
import com.status.statussaver.ui.adapters.SavedStatusAdapter
import com.status.statussaver.ui.frags.saved.SavedWhatsappFragment
import com.status.statussaver.utils.Utils
import kotlinx.coroutines.*
import java.io.File
import java.util.*

class SavedWhatsappBVideosFragment : Fragment(), OnDeleteListener {
    val TAG = SavedWhatsappFragment::class.simpleName
    private lateinit var viewModel: SavedWhatsappViewModel
    private lateinit var myRecyclerView: RecyclerView
    private lateinit var isEmptyList: LinearLayout
    private lateinit var txt: TextView
    private lateinit var cbProgressBar : CircularProgressIndicator

    private var myAdapter: SavedStatusAdapter? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = SavedWhatsappModule.provideViewModel(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_saved_status, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG, "onViewCreated: ")
        setupViews(view)
        observeUiState()
        viewModel.loadWBSavedStatuses()
    }

    private fun setupViews(view: View) {
        myRecyclerView = view.findViewById(R.id.myRecyclerView)
        isEmptyList = view.findViewById(R.id.isEmptyList)
        cbProgressBar = view.findViewById(R.id.circular_progressbar)
        txt = view.findViewById(R.id.txt)
        myRecyclerView.layoutManager = GridLayoutManager(context, 3)
    }

    private fun observeUiState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    when (state) {
                        is SavedWhatsappUiState.Loading -> showLoading()
                        is SavedWhatsappUiState.Success -> {
                            Log.d(TAG, "observeUiState: ${state.statusList}")
                            showContent(state.statusList)
                        }
                        is SavedWhatsappUiState.Empty -> showEmpty()
                        is SavedWhatsappUiState.Error -> showError(state.message)
                        is SavedWhatsappUiState.Initial -> Unit
                    }
                }
            }
        }
    }

    private fun showLoading() {
        myRecyclerView.visibility = View.GONE
        isEmptyList.visibility = View.GONE
        cbProgressBar.visibility = View.VISIBLE
    }

    private fun showContent(statusList: ArrayList<String>) {
        Log.d(TAG, "showContent:   ${statusList.toList()}")
        val filePathList = ArrayList(statusList.map { it })
        if (myAdapter == null) {
            Log.d(TAG, "showContent: adapter is null  ${statusList}")
            myAdapter = SavedStatusAdapter(
                requireActivity(),
                filePathList,
                this@SavedWhatsappBVideosFragment,
                true
            )
            myRecyclerView.adapter = myAdapter
            myRecyclerView.setItemViewCacheSize(filePathList.size)
            myRecyclerView.visibility = View.VISIBLE
            isEmptyList.visibility = View.GONE
            cbProgressBar.visibility = View.GONE

        } else {
            Log.d(TAG, "showContent: adapter is null")
            myAdapter?.updateData(filePathList)
            myAdapter?.notifyDataSetChanged()
            myRecyclerView.visibility = View.VISIBLE
            isEmptyList.visibility = View.GONE
            cbProgressBar.visibility = View.GONE
        }
    }

    private fun showEmpty() {
        myRecyclerView.visibility = View.GONE
        cbProgressBar.visibility = View.GONE
        isEmptyList.visibility = View.VISIBLE


    }

    private fun showError(message: String) {
        Snackbar.make(requireView(), message, Snackbar.LENGTH_LONG).show()
    }





    override fun getClick(position: Int) {
        val statusList =
            (viewModel.uiState.value as? SavedWhatsappUiState.Success)?.statusList ?: return
        val status = statusList[position]
        viewModel.deleteStatus(Uri.parse(status))
        viewModel.loadSavedStatuses()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        SavedWhatsappModule.clearViewModel()
    }

}



