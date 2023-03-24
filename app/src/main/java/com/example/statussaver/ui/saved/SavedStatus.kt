package com.example.statussaver.ui.saved

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.statussaver.databinding.FragmentSavedStatusBinding
import com.example.statussaver.ui.adapters.SavedStatusViewPagerAdapter
import com.google.android.material.tabs.TabLayoutMediator

class SavedStatus : Fragment() {
    companion object {

        val tabArray_SavedStatus = arrayOf(
            "Whatsapp ",
            "Whatsapp Business"
        )


        private val tabIcons_SavedStatus = intArrayOf(
            com.example.statussaver.R.drawable.baseline_photo_library_24,
            com.example.statussaver.R.drawable.baseline_photo_library_24
        )
    }

    private var _binding: FragmentSavedStatusBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentSavedStatusBinding.inflate(inflater, container, false)

        val viewPager = binding.viewPagerSavedStatus
        val tabLayout = binding.tlSecond

        val adapter = childFragmentManager?.let { SavedStatusViewPagerAdapter(it, lifecycle) }
        viewPager.adapter = adapter

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = tabArray_SavedStatus[position]
            tab.setIcon(tabIcons_SavedStatus[position])
        }.attach()
        return binding.root
    }


}