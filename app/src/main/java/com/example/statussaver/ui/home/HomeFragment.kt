package com.example.statussaver.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.statussaver.databinding.FragmentHomeBinding
import com.example.statussaver.ui.adapters.ViewPagerAdapter
import com.google.android.material.tabs.TabLayoutMediator




class HomeFragment : Fragment() {
    companion object {

        val tabArray = arrayOf(
            "Whatsapp",
            "Whatsapp Business"
        )


        val tabIcons = intArrayOf(
            com.example.statussaver.R.drawable.ic_whatsapp_svgrepo_com,
            com.example.statussaver.R.drawable.b
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

        val adapter = childFragmentManager?.let { ViewPagerAdapter(it, lifecycle) }
        viewPager.adapter = adapter

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = tabArray[position]
            tab.setIcon(tabIcons[position])
        }.attach()



        return binding.root
    }




}