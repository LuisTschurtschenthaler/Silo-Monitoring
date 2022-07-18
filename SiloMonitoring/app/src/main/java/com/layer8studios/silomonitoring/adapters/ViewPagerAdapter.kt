package com.layer8studios.silomonitoring.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.layer8studios.silomonitoring.fragments.SiloFragment
import com.layer8studios.silomonitoring.models.Silo
import com.layer8studios.silomonitoring.utils.Preferences


class ViewPagerAdapter(
    private val fragmentActivity: FragmentActivity
): FragmentStateAdapter(fragmentActivity) {

    private var silos: ArrayList<Silo> = Preferences.getSilos()

    fun removeSilo(silo: Silo) {
        val index = silos.indexOf(silo)
        silos.remove(silo)
        notifyItemRemoved(index)
    }

    fun update() {
        this.silos = Preferences.getSilos()
        notifyDataSetChanged()
    }

    override fun createFragment(position: Int): Fragment {
        return SiloFragment.newInstance(silos[position])
    }

    override fun getItemCount(): Int = silos.size

}