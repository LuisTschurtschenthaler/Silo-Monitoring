package com.layer8studios.silomonitoring.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.tabs.TabLayoutMediator
import com.layer8studios.silomonitoring.adapters.ViewPagerAdapter
import com.layer8studios.silomonitoring.databinding.ActivityMainBinding
import com.layer8studios.silomonitoring.models.Silo
import com.layer8studios.silomonitoring.receivers.NotificationReceiver
import com.layer8studios.silomonitoring.utils.Preferences
import com.layer8studios.silomonitoring.utils.Utils


class MainActivity
    : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: ViewPagerAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if(!Preferences.isInitialized())
            Preferences.init(this)

        Utils.checkSilos()
        // TODO(REMOVE AFTER TESTS) {
            NotificationReceiver.cancelNotifications(applicationContext)
            NotificationReceiver.scheduleNotifications(applicationContext)
        // }

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        adapter = ViewPagerAdapter(this)
        binding.viewPager.adapter = adapter
        TabLayoutMediator(binding.pageIndicator, binding.viewPager) { _, _ -> }.attach()
        updateLayouts()

        binding.buttonNew.setOnClickListener {
            val intent = Intent(this, CreateSiloActivity::class.java)
            startActivityForResult(intent, 0)
        }
    }

    override fun onResume() {
        super.onResume()

        adapter.update()
        binding.viewPager.adapter = adapter
        updateLayouts()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == 0 && resultCode == RESULT_OK) {
            adapter.update()
            updateLayouts()
            binding.viewPager.currentItem = adapter.itemCount
        }
    }


    fun removeItem(silo: Silo) {
        adapter.remove(silo)
        binding.viewPager.adapter = adapter
        updateLayouts()
    }


    private fun updateLayouts() {
        binding.linearLayoutTexts.visibility = if(adapter.itemCount > 0) View.GONE else View.VISIBLE
    }

}