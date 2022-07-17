package com.layer8studios.silomonitoring.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.tabs.TabLayoutMediator
import com.layer8studios.silomonitoring.adapters.ViewPagerAdapter
import com.layer8studios.silomonitoring.databinding.ActivityMainBinding
import com.layer8studios.silomonitoring.models.Silo
import java.time.LocalDate


class MainActivity
    : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val silos = listOf(
            Silo("Silo 1", "Weizen", 10.0, 1.0, LocalDate.of(2022, 7, 15))
        )

        binding.viewPager.adapter = ViewPagerAdapter(silos, this)
        TabLayoutMediator(binding.pageIndicator, binding.viewPager) { _, _ -> }.attach()


        binding.buttonNew.setOnClickListener {
            val intent = Intent(this, CreateSiloActivity::class.java)
            startActivityForResult(intent, 0)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(resultCode == RESULT_OK)
            binding.viewPager.currentItem = binding.viewPager.adapter?.itemCount!! - 1
    }

}