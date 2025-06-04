package com.status.statussaver.ui.frags.status.whatsappB

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Build.VERSION.SDK_INT
import android.os.Bundle
import android.os.Environment
import android.os.storage.StorageManager
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.status.saver.video.BuildConfig
import com.status.saver.video.R
import com.status.saver.video.databinding.FragmentWhatsappBinding
import com.status.statussaver.data.interfaces.OnCardViewItemClickListenerAds
import com.status.statussaver.data.model.StatusDataModel
import com.status.statussaver.di.WhatsAppBusinessModule
import com.status.statussaver.di.WhatsAppModule
import com.status.statussaver.ui.activities.MainActivity
import com.status.statussaver.ui.activities.PreviewActivity
import com.status.statussaver.ui.adapters.NewAdapter
import com.status.statussaver.ui.adapters.RecentStatusAdapter
import com.status.statussaver.ui.frags.status.whatsapp.WhatsAppUiState
import com.status.statussaver.ui.frags.status.whatsapp.WhatsAppViewModel
import com.status.statussaver.ui.frags.status.whatsapp.WhatsappFragment
import com.status.statussaver.utils.Utils
import kotlinx.coroutines.launch
import java.io.File

class WhatsappBusinessFragment : Fragment() {
    val TAG = WhatsappBusinessFragment::class.simpleName
    private var _binding: FragmentWhatsappBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: WhatsAppBusinessViewModel

    private lateinit var myRecyclerView: RecyclerView
    private lateinit var shimmerRecyclerView: RecyclerView
    private lateinit var shimmerLayout: ShimmerFrameLayout
    private lateinit var allowStorageTv: MaterialButton
    private lateinit var sAccessBtn: LinearLayout
    private lateinit var isEmptyList: LinearLayout

    private var myAdapter: NewAdapter? = null
    private var shimmerAdapter: NewAdapter? = null
    private val REQUEST_ACTION_OPEN_DOCUMENT_TREE = 102

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = WhatsAppBusinessModule.provideViewModel(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentWhatsappBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews(view)
        setupShimmerAdapter()
        observeUiState()

        allowStorageTv.setOnClickListener {
            handleStorageAccess()
        }
    }

    private fun setupViews(view: View) {
        myRecyclerView = view.findViewById(R.id.myRecyclerView)
        shimmerRecyclerView = view.findViewById(R.id.shimmer_recycler_view)
        shimmerLayout = view.findViewById(R.id.shimmer_layout)
        allowStorageTv = view.findViewById(R.id.allowStorageTv)
        isEmptyList = view.findViewById(R.id.isEmptyList)
        sAccessBtn = view.findViewById(R.id.sAccessBtn)

        myRecyclerView.layoutManager = GridLayoutManager(context, 3)
        shimmerRecyclerView.layoutManager = GridLayoutManager(context, 3)
    }

    private fun setupShimmerAdapter() {
        shimmerAdapter = NewAdapter(
            requireContext(),
            false,
            object : OnCardViewItemClickListenerAds {
                override fun onCardViewListener(
                    mData: ArrayList<StatusDataModel>,
                    position: Int,
                    isWapp: Boolean
                ) {}
            },
            isShimmer = true
        )
        shimmerRecyclerView.adapter = shimmerAdapter
        // Submit empty list to show shimmer items
        shimmerAdapter?.submitList(List(12) {
            StatusDataModel(
                filename = "placeholder_${it}",
                filepath = "placeholder_path_${it}"
            )
        })
    }

    private fun observeUiState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    when (state) {
                        is WhatsAppBusinessUiState.Loading -> {
                            Log.d(TAG, "observeUiState: $state")
                            showLoading()
                        }
                        is WhatsAppBusinessUiState.Success -> {
                            Log.d(TAG, "observeUiState: $state")
                            showContent(state.statuses)
                        }
                        is WhatsAppBusinessUiState.Empty -> {
                            Log.d(TAG, "observeUiState: $state")
                            showEmpty()
                        }
                        is WhatsAppBusinessUiState.Error -> {
                            Log.d(TAG, "observeUiState: $state")
                            showError(state.message)
                        }

                        is WhatsAppBusinessUiState.Initial -> {
                            Log.d(TAG, "observeUiState: $state")
                            Unit
                        }

                        WhatsAppBusinessUiState.NeedsPermission -> {
                            Log.d(TAG, "observeUiState: $state")
                        }
                        is WhatsAppBusinessUiState.StorageAccessRequired -> {
                            Log.d(TAG, "observeUiState: $state")
                        }
                        WhatsAppBusinessUiState.StoragePermissionRequired -> {
                            Log.d(TAG, "observeUiState: $state")
                        }
                        WhatsAppBusinessUiState.WhatsAppBusinessNotInstalled -> {
                            Log.d(TAG, "observeUiState: $state")

                        }
                    }
                }
            }
        }
    }

    private fun showLoading() {
        Log.d(TAG, "showLoading: ")
        shimmerLayout.visibility = View.VISIBLE
        shimmerLayout.startShimmer()
        myRecyclerView.visibility = View.GONE
        sAccessBtn.visibility = View.GONE
        isEmptyList.visibility = View.GONE
    }

    private fun showContent(statusList: List<StatusDataModel>) {
        Log.d(TAG, "showContent: ")
        if (myAdapter == null) {
            myAdapter = NewAdapter(
                requireContext(),
                false, // isWapp = false for WhatsApp Business
                object : OnCardViewItemClickListenerAds {
                    override fun onCardViewListener(
                        mData: ArrayList<StatusDataModel>,
                        position: Int,
                        isWapp: Boolean
                    ) {
                        startActivity(Intent(context, PreviewActivity::class.java).apply {
                            putParcelableArrayListExtra("statusList", mData)
                            putExtra("position", position)
                            putExtra("isWapp", false) // WhatsApp Business
                        })
                    }
                }
            )
            myRecyclerView.adapter = myAdapter
            myRecyclerView.setItemViewCacheSize(statusList.size)
        }

        myAdapter?.submitList(statusList)

        shimmerLayout.stopShimmer()
        shimmerLayout.visibility = View.GONE
        myRecyclerView.visibility = View.VISIBLE
        sAccessBtn.visibility = View.GONE
        isEmptyList.visibility = View.GONE
    }

    private fun showEmpty() {
        shimmerLayout.stopShimmer()
        shimmerLayout.visibility = View.GONE
        myRecyclerView.visibility = View.GONE
        sAccessBtn.visibility = View.GONE
        isEmptyList.visibility = View.VISIBLE
    }

    private fun showError(message: String) {
        view?.let {
            Toast.makeText(context ?: requireContext(), message, Toast.LENGTH_SHORT).show()
        }
    }

    private fun showAccessButton() {
        shimmerLayout.stopShimmer()
        shimmerLayout.visibility = View.GONE
        myRecyclerView.visibility = View.GONE
        sAccessBtn.visibility = View.VISIBLE
        isEmptyList.visibility = View.GONE
    }

    private fun handleStorageAccess() {
        if (SDK_INT >= Build.VERSION_CODES.S_V2) {
            handleAndroid13Permission()
        } else {
            handleLegacyPermission()
        }
    }

    private fun handleAndroid13Permission() {
        if (Utils.appInstalledOrNot(requireActivity(), "com.whatsapp.w4b")) {
            requestWhatsAppBusinessFolderAccess()
        } else {
            showWhatsAppBusinessNotInstalledMessage()
        }
    }

    private fun handleLegacyPermission() {
        if (!checkPermission()) {
            showPermissionDialog()
        } else {
            if (Utils.appInstalledOrNot(requireActivity(), "com.whatsapp.w4b")) {
                requestWhatsAppBusinessFolderAccess()
            } else {
                showWhatsAppBusinessNotInstalledMessage()
            }
        }
    }

    private fun showWhatsAppBusinessNotInstalledMessage() {
        Toast.makeText(
            context ?: requireContext(),
            "Please Install WhatsApp Business For Download Status!",
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun requestWhatsAppBusinessFolderAccess() {
        val sm = requireActivity().getSystemService(Context.STORAGE_SERVICE) as StorageManager
        val statusDir: String = getWhatsappBusinessFolder() ?: run {
            showError("WhatsApp Business status folder not found")
            return
        }

        val intent = if (SDK_INT >= Build.VERSION_CODES.Q) {
            sm.primaryStorageVolume.createOpenDocumentTreeIntent().apply {
                var uri = getParcelableExtra<Uri>("android.provider.extra.INITIAL_URI")
                var scheme = uri.toString()
                scheme = scheme.replace("/root/", "/document/")
                scheme += "%3A$statusDir"
                uri = Uri.parse(scheme)
                putExtra("android.provider.extra.INITIAL_URI", uri)
            }
        } else {
            Intent(Intent.ACTION_OPEN_DOCUMENT_TREE).apply {
                putExtra(
                    "android.provider.extra.INITIAL_URI",
                    "content://com.android.externalstorage.documents/document/primary%3A$statusDir".toUri()
                )
            }
        }
        intent.addFlags(
            Intent.FLAG_GRANT_WRITE_URI_PERMISSION or
                    Intent.FLAG_GRANT_READ_URI_PERMISSION or
                    Intent.FLAG_GRANT_PREFIX_URI_PERMISSION or
                    Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION
        )
        startActivityForResult(intent, REQUEST_ACTION_OPEN_DOCUMENT_TREE)
    }

    private fun showPermissionDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.permissiondenied)
            .setMessage(R.string.summary_permission)
            .setPositiveButton(R.string.go_setting) { _, _ ->
                startActivity(
                    Intent(
                        Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                        Uri.parse("package:" + BuildConfig.APPLICATION_ID)
                    )
                )
            }
            .show()
    }

    private fun getWhatsappBusinessFolder(): String? {
        // WhatsApp Business folder paths
        val oldPath = Environment.getExternalStorageDirectory().absolutePath + "/WhatsApp Business/Media/.Statuses"
        val newPath = Environment.getExternalStorageDirectory().absolutePath + "/Android/media/com.whatsapp.w4b/WhatsApp Business/Media/.Statuses"

        return when {
            File(oldPath).exists() -> "WhatsApp%20Business%2FMedia%2F.Statuses"
            File(newPath).exists() -> "Android%2Fmedia%2Fcom.whatsapp.w4b%2FWhatsApp%20Business%2FMedia%2F.Statuses"
            else -> null
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_ACTION_OPEN_DOCUMENT_TREE && resultCode == Activity.RESULT_OK) {
            data?.data?.let { uri ->
                try {
                    requireContext().contentResolver.takePersistableUriPermission(
                        uri,
                        Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                    )
                    viewModel.saveTreeUri(uri)
                    // Trigger data loading after permission is granted
                    viewModel.loadStatuses()
                } catch (e: Exception) {
                    e.printStackTrace()
                    showError("Failed to grant permission: ${e.message}")
                }
            }
        }
    }

    private fun checkPermission(): Boolean {
        val writePermission = ContextCompat.checkSelfPermission(
            requireContext(),
            "android.permission.WRITE_EXTERNAL_STORAGE"
        )
        val readPermission = ContextCompat.checkSelfPermission(
            requireContext(),
            "android.permission.READ_EXTERNAL_STORAGE"
        )
        return writePermission == 0 && readPermission == 0
    }

    override fun onResume() {
        super.onResume()
        // Check permissions and load data when fragment resumes
        viewModel.checkAndRequestPermission()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        WhatsAppBusinessModule.clearViewModel()
    }
}