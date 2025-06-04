package com.status.statussaver.ui.activities

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.status.saver.video.databinding.ActivityHelpBinding
class HelpActivity: BaseActivity() {

    private lateinit var binding: ActivityHelpBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHelpBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.tbHelp)
        supportActionBar?.setDisplayShowTitleEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.title = "Help"
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId ==android.R.id.home){
            finish()
        }

        return super.onOptionsItemSelected(item)

    }
}