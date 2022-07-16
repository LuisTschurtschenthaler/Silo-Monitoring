package com.layer8studios.silomonitoring.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter


class ViewPagerAdapter(supportFragmentManager: FragmentManager)
    : FragmentPagerAdapter(supportFragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    private val fragments = ArrayList<Fragment>()
    private val fragmentTitles = ArrayList<String>()


    override fun getItem(position: Int): Fragment = fragments[position]

    override fun getCount(): Int = fragments.size

    override fun getPageTitle(position: Int): CharSequence = fragmentTitles[position]

    fun addFragment(fragment: Fragment, title: String): ViewPagerAdapter {
        fragments.add(fragment)
        fragmentTitles.add(title)
        return this
    }

}