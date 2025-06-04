package com.status.statussaver.ui.activities

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.preference.PreferenceManager
import android.provider.Settings
import android.util.Log
import android.view.MenuItem
import android.view.View
import androidx.annotation.RequiresApi
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import com.status.saver.video.BuildConfig
import com.status.saver.video.R
import com.status.saver.video.databinding.ActivityMainBinding
import com.status.saver.video.databinding.NavHeaderMainBinding
import com.status.statussaver.ui.frags.chat.KeypadFragment
import com.status.statussaver.ui.frags.status.HomeFragment
import com.status.statussaver.ui.frags.saved.SavedStatus
import com.status.statussaver.utils.Constant
import androidx.core.net.toUri
import com.google.android.material.bottomnavigation.BottomNavigationView


class MainActivity : BaseActivity() {

    private var adapter: ViewPager2Adapter? = null

    private lateinit var binding: ActivityMainBinding
    private val PERMISSIONS_LIST = arrayOf(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )
    private var TAG: String = "MainActivity"
    private val context: Context? = null
    private lateinit var sharedPreferences: SharedPreferences
    var banner_id: String? = null
    var adx_banner_id: String? = null
    var ads_status: String? = null

    companion object {
        private const val SETTINGS_REQUEST_CODE = 1001
    }

    @SuppressLint("RestrictedApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this@MainActivity)



        ads_status = sharedPreferences.getString(Constant.adStatus, "")

        banner_id = sharedPreferences.getString(Constant.banner_id, "")
        adx_banner_id = sharedPreferences.getString(Constant.adx_banner_id, "")

        banner_id?.let { Log.e(TAG, it) }
        adx_banner_id?.let { Log.e(TAG, it) }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S_V2) {
            Log.e("permission", "Android 13 Permission Granted")
        } else {
            if (!checkPermission()) {
                requestPermission();
            } else {
                Log.e("permission", "Permission Granted")
            }

        }
        setSupportActionBar(binding.toolbar)
        val viewPager2: ViewPager2 = binding.vp2
        val toggle = ActionBarDrawerToggle(
            this,
            binding.container,
            binding.toolbar,
            0,
            0
        )
        binding.container.addDrawerListener(toggle)
        toggle.isDrawerIndicatorEnabled = true
        toggle.syncState()
        // navView is NavigationView
        val navigationView = binding.container
        val viewHeader = binding.navigationView.getHeaderView(0)
        val navViewHeaderBinding: NavHeaderMainBinding = NavHeaderMainBinding.bind(viewHeader)
        navViewHeaderBinding.versionName.text =
            resources.getString(R.string.app_version, BuildConfig.VERSION_NAME)
        adapter = ViewPager2Adapter(this)
        viewPager2.adapter = adapter;
        viewPager2.offscreenPageLimit = 3;

        viewPager2.isUserInputEnabled = false;


        viewPager2.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                if (position == 0) {
                    binding.bottomNavView.menu.findItem(R.id.nav_home).isChecked = true
                } else if (position == 1) {
                    binding.bottomNavView.menu.findItem(R.id.nav_saved_status).isChecked = true
                } else if (position == 2) {
                    binding.bottomNavView.menu.findItem(R.id.nav_about).isChecked = true
                }
                super.onPageSelected(position)
            }
        })

        binding.bottomNavView.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.nav_home -> {
                    binding.vp2.currentItem = 0
                    supportActionBar?.title = "Status Saver"
                    return@setOnItemSelectedListener true
                }
                R.id.nav_saved_status -> {
                    binding.vp2.currentItem = 1
                    supportActionBar?.title = "Saved Status"
                    return@setOnItemSelectedListener true
                }
                R.id.nav_about -> {
                    binding.vp2.currentItem = 2
                    supportActionBar?.title = "Direct Chat"
                    return@setOnItemSelectedListener true
                }

            }
            false
        }

        binding.navigationView.setNavigationItemSelectedListener(object :
            NavigationView.OnNavigationItemSelectedListener {
            override fun onNavigationItemSelected(item: MenuItem): Boolean {
                when (item.itemId) {
                    R.id.nav_home -> {
                        binding.vp2.currentItem = 0
                        supportActionBar?.title = "Status Saver"

                    }
                    R.id.nav_saved_status -> {
                        binding.vp2.currentItem = 1
                        supportActionBar?.title = "Saved Status"
                    }
                    R.id.nav_about -> {
                        binding.vp2.currentItem = 2
                        supportActionBar?.title = "Direct Chat"
                    }


                    R.id.nav_settings -> {
                        binding.container.closeDrawers()
                        val intent = Intent(this@MainActivity, SettingActivity::class.java)
                        startActivityForResult(intent, SETTINGS_REQUEST_CODE) // Use a request code
                    }
                    R.id.nav_help -> {
                        binding.container.closeDrawers()
                        Handler().postDelayed({
                            val intent = Intent(this@MainActivity, HelpActivity::class.java)
                            startActivity(intent)
                            overridePendingTransition(
                                R.anim.slide_in_right,
                                R.anim.slide_out_left
                            )
                            //doSomethingHere()
                        }, 150)
                    }

                }
                binding.container.closeDrawers()
                return true
            }

        })
        viewPager2.currentItem = 0
        askPermission(100)

    }

    class ViewPager2Adapter(fragmentActivity: FragmentActivity?) :
        FragmentStateAdapter(fragmentActivity!!) {
        override fun getItemCount(): Int {
            return 3
        }


        override fun createFragment(i: Int): Fragment {
            if (i == 0) {
                return HomeFragment()
            }
            if (i == 1) {
                return SavedStatus()
            }
            if (i == 2) {
                return KeypadFragment()
            }
            return HomeFragment()
        }
    }

    private fun checkPermission(): Boolean {

        val result = ContextCompat.checkSelfPermission(
            applicationContext,
            "android.permission.WRITE_EXTERNAL_STORAGE"
        )
        val result1 = ContextCompat.checkSelfPermission(
            applicationContext,
            "android.permission.READ_EXTERNAL_STORAGE"
        )
        return result == 0 && result1 == 0
    }

    private fun requestPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                "android.permission.WRITE_EXTERNAL_STORAGE",
                "android.permission.READ_EXTERNAL_STORAGE"
            ),
            101
        )
    }
    fun askPermission(code: Int) {
        val readStorage =
            ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
        val writeStorage =
            ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        val result = (readStorage == PackageManager.PERMISSION_GRANTED
                && writeStorage == PackageManager.PERMISSION_GRANTED)
        if (!result) {
            ActivityCompat.requestPermissions(this, PERMISSIONS_LIST, code)
        } else {
            if (code == 100) {
                Log.e("tag100", "permission")
//                startActivity(Intent(this, GalleryActivity::class.java))
                return
            } else if (code == 101) {
                Log.e("tag101", "permission")
                return
//                startActivity(Intent(this, StatusActivity::class.java))
            }
        }
    }


    @RequiresApi(Build.VERSION_CODES.M)
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 101) {
            if (grantResults.size > 0 && grantResults[0] == 0) {
                Snackbar.make(
                    binding.flAdPlaceholderMain, "Permission Granted",
                    Snackbar.LENGTH_SHORT
                )
                    .setAnchorView(binding.flAdPlaceholderMain)
                    .show()
                return
            } else {


                if (ActivityCompat.shouldShowRequestPermissionRationale(
                        this@MainActivity,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    )
                ) {
                    // now, user has denied permission (but not permanently!)
                    MaterialAlertDialogBuilder(this)
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
                        .setNegativeButton(R.string.goit) { dialog, _ ->
                            dialog.dismiss()

                        }
                        .apply { show() }

//                    requestPermission()
                } else {
                    MaterialAlertDialogBuilder(this)
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
                        .setNegativeButton(R.string.goit) { dialog, _ ->
                            dialog.dismiss()

                        }
                        .apply { show() }
                }
            }

        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == SETTINGS_REQUEST_CODE && resultCode == RESULT_OK) {
            // Language was changed, recreate the activity
            recreate()
        }
    }


    override fun onResume() {
        super.onResume()
    }






    @Deprecated("Deprecated in Java")
    @Suppress("DEPRECATION")
    override fun onBackPressed() {
        MaterialAlertDialogBuilder(this)
            .setTitle(R.string.close)
            .setMessage(R.string.summary_close)
            .setPositiveButton(android.R.string.yes) { _, _ ->
                super.onBackPressed()
                moveTaskToBack(true)
            }
            .setNegativeButton(android.R.string.no, null)
            .apply { show() }
    }


}