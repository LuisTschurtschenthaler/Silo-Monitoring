package com.layer8studios.silomonitoring.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.layer8studios.silomonitoring.adapters.ViewPagerAdapter
import com.layer8studios.silomonitoring.databinding.ActivityMainBinding


class MainActivity
    : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.viewPager.adapter = ViewPagerAdapter(supportFragmentManager)
        binding.tabLayout.setupWithViewPager(binding.viewPager)
        binding.tabLayout.setScrollPosition(0, 0f, true)
        binding.viewPager.currentItem = 0

        binding.fab.setOnClickListener { view ->

        }
    }

}