package com.layer8studios.silomonitoring.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.tabs.TabLayoutMediator
import com.layer8studios.silomonitoring.adapters.ViewPagerAdapter
import com.layer8studios.silomonitoring.databinding.ActivityMainBinding
import com.layer8studios.silomonitoring.utils.Preferences


class MainActivity
    : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: ViewPagerAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if(!Preferences.isInitialized())
            Preferences.init(this)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        adapter = ViewPagerAdapter(this)
        binding.viewPager.adapter = adapter
        TabLayoutMediator(binding.pageIndicator, binding.viewPager) { _, _ -> }.attach()


        binding.buttonNew.setOnClickListener {
            val intent = Intent(this, CreateSiloActivity::class.java)
            startActivityForResult(intent, 0)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(resultCode == RESULT_OK) {
            adapter.update()
            binding.viewPager.currentItem = binding.viewPager.adapter?.itemCount!!
        }
    }

}