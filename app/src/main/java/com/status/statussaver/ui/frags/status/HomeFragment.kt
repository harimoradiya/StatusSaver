package com.status.statussaver.ui.frags.status

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayoutMediator
import com.status.saver.video.R
import com.status.saver.video.databinding.FragmentHomeBinding
import com.status.statussaver.ui.adapters.ViewPagerAdapter


class HomeFragment : Fragment() {
    companion object {

        val tabArray = arrayOf(
            "Whatsapp",
            "Whatsapp Business"
        )

        val tabIcons = intArrayOf(
            R.drawable.ic_whatsapp_svgrepo_com,
            R.drawable.b
        )
    }
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentHomeBinding.inflate(inflater, container, false)


        val viewPager = binding.viewPager
        val tabLayout = binding.tlMain

        val adapter = ViewPagerAdapter(childFragmentManager, lifecycle)
        viewPager.adapter = adapter

        TabLayoutMediator(tabLayout, viewPager, true) { tab, position ->
            tab.text = tabArray[position]
            tab.setIcon(tabIcons[position])
        }.attach()

        binding.viewPager.isUserInputEnabled = false

        return binding.root
    }




}