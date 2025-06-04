package com.status.statussaver.ui.frags.saved

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.status.saver.video.R
import com.status.statussaver.ui.adapters.SavedStatusViewPagerAdapter
import com.google.android.material.tabs.TabLayoutMediator
import com.status.saver.video.databinding.FragmentSavedStatusBinding
import com.status.saver.video.databinding.FragmentSavedStatusRootBinding

class SavedStatus : Fragment() {
    companion object {

        val tabArray_SavedStatus = arrayOf(
            "Whatsapp ",
            "Whatsapp Business"
        )


        private val tabIcons_SavedStatus = intArrayOf(
          R.drawable.ic_gallery,
          R.drawable.ic_gallery
        )
    }

    private var _binding: FragmentSavedStatusRootBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentSavedStatusRootBinding.inflate(inflater, container, false)

        val viewPager = binding.viewPagerSavedStatus
        val tabLayout = binding.tlSecond

        val adapter = childFragmentManager?.let { SavedStatusViewPagerAdapter(it, lifecycle) }
        viewPager.adapter = adapter

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = tabArray_SavedStatus[position]
            tab.setIcon(tabIcons_SavedStatus[position])
        }.attach()

        viewPager.isUserInputEnabled = false

        return binding.root
    }




}