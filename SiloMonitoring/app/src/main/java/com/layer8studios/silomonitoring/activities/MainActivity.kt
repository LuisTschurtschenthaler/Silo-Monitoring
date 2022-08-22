package com.layer8studios.silomonitoring.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.anjlab.android.iab.v3.BillingProcessor
import com.anjlab.android.iab.v3.TransactionDetails
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.material.tabs.TabLayoutMediator
import com.layer8studios.silomonitoring.adapters.ViewPagerAdapter
import com.layer8studios.silomonitoring.databinding.ActivityMainBinding
import com.layer8studios.silomonitoring.models.Silo
import com.layer8studios.silomonitoring.utils.*
import kotlinx.coroutines.CoroutineScope
import kotlin.random.Random


class MainActivity
    : AppCompatActivity() {

    companion object {
        var billingHelper: BillingHelper? = null
    }

    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: ViewPagerAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if(!Preferences.isInitialized())
            Preferences.init(this)

        Utils.checkSilos()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        billingHelper = BillingHelper(applicationContext)
        MobileAds.initialize(this)

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
        //loadInterstitial() //TODO(MAKE BETTER)

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

    private fun loadInterstitial() {
        val adRequest = AdRequest.Builder().build()
        InterstitialAd.load(this, "ca-app-pub-6111292602356970/7702380870", adRequest, object: InterstitialAdLoadCallback() {
            override fun onAdLoaded(ad: InterstitialAd) {
                if(Random.nextInt(0, 100) % 5 == 0)
                    ad.show(this@MainActivity)
            }
        })
    }

}