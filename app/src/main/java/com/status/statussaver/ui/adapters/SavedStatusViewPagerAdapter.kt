package com.status.statussaver.ui.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.status.statussaver.ui.frags.saved.SavedWhatsappFragment
import com.status.statussaver.ui.frags.saved.SavedWhatsappBVideosFragment

private const val NUM_TABS = 2
class SavedStatusViewPagerAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle) :
    FragmentStateAdapter(fragmentManager, lifecycle) {

    override fun getItemCount(): Int {
        return NUM_TABS
    }

    override fun createFragment(position: Int): Fragment {
        when (position) {
            0 -> return SavedWhatsappFragment()
        }
        return SavedWhatsappBVideosFragment()
    }
}