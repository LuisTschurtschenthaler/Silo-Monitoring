package com.layer8studios.silomonitoring.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.layer8studios.silomonitoring.fragments.SiloFragment
import com.layer8studios.silomonitoring.models.Silo


class ViewPagerAdapter(
    private val silos: List<Silo>,
    private val fragmentActivity: FragmentActivity
): FragmentStateAdapter(fragmentActivity) {

    override fun createFragment(position: Int): Fragment {
        return SiloFragment.newInstance(silos[position])
    }

    override fun getItemCount(): Int = silos.size

}