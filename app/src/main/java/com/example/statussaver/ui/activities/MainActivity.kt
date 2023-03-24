package com.example.statussaver.ui.activities

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.annotation.RequiresApi
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.navigation.NavController
import androidx.navigation.ui.*
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.example.statussaver.BuildConfig
import com.example.statussaver.R
import com.example.statussaver.databinding.ActivityMainBinding
import com.example.statussaver.databinding.NavHeaderMainBinding
import com.example.statussaver.ui.about.AboutFragment
import com.example.statussaver.ui.home.HomeFragment
import com.example.statussaver.ui.saved.SavedStatus
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar


class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private var adapter: ViewPager2Adapter? = null

    private lateinit var binding: ActivityMainBinding
    private val PERMISSIONS_LIST = arrayOf(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )


    @RequiresApi(Build.VERSION_CODES.M)
    @SuppressLint("RestrictedApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
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
        binding.navigationView.setNavigationItemSelectedListener(this)
        // navView is NavigationView
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
                    supportActionBar?.title = "About"
                    return@setOnItemSelectedListener true
                }

            }
            false
        }



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
                return AboutFragment()
            }
            return HomeFragment()
        }
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


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (grantResults.size > 0) {
            val ReadExternalStorage = grantResults[0] == PackageManager.PERMISSION_GRANTED
            val WriteExternalStorage = grantResults[1] == PackageManager.PERMISSION_GRANTED
            if (ReadExternalStorage && WriteExternalStorage) {

                Snackbar.make(
                    binding.container, "Permission Granted",
                    Snackbar.LENGTH_SHORT
                ).show()
//                if (requestCode == 100) {
//                    startActivity(Intent(this, GalleryActivity::class.java))
//                    return
//                } else if (requestCode == 101) {
//                    startActivity(Intent(this, StatusActivity::class.java))
//                }
            } else {
                askPermission(101)

                Snackbar.make(
                    binding.container, "Please allow permission to continue.",
                    Snackbar.LENGTH_SHORT
                ).show()
            }
        }
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
                supportActionBar?.title = "About"
            }


            R.id.nav_direct_chat -> {
                startActivity(Intent(this@MainActivity, DirectChatActivity::class.java))

            }
            R.id.nav_help -> {
                startActivity(Intent(this@MainActivity, HelpActivity::class.java))
            }

        }
        binding.container.closeDrawers()
        return true
    }


}