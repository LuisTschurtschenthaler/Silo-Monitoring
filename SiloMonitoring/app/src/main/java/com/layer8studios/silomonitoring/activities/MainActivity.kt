package com.layer8studios.silomonitoring.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.anjlab.android.iab.v3.BillingProcessor
import com.anjlab.android.iab.v3.BillingProcessor.IPurchasesResponseListener
import com.anjlab.android.iab.v3.PurchaseInfo
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.material.tabs.TabLayoutMediator
import com.layer8studios.silomonitoring.adapters.ViewPagerAdapter
import com.layer8studios.silomonitoring.databinding.ActivityMainBinding
import com.layer8studios.silomonitoring.models.Silo
import com.layer8studios.silomonitoring.utils.*


class MainActivity
    : AppCompatActivity(), BillingProcessor.IBillingHandler {

    companion object {
        var boughtPro = false
    }

    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: ViewPagerAdapter
    private lateinit var billingProcessor: BillingProcessor
    private var isStart = true


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if(!Preferences.isInitialized())
            Preferences.init(this)

        Utils.checkSilos()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        MobileAds.initialize(this)
        billingProcessor = BillingProcessor(this, LICENSE_KEY, this)
        billingProcessor.initialize()

        adapter = ViewPagerAdapter(this)
        binding.viewPager.adapter = adapter
        TabLayoutMediator(binding.pageIndicator, binding.viewPager) { _, _ -> }.attach()
        updateLayouts()

        binding.buttonNew.setOnClickListener {
            val intent = Intent(this, CreateSiloActivity::class.java)
            startActivityForResult(intent, 0)
        }
    }

    override fun onDestroy() {
        billingProcessor.release()
        super.onDestroy()
    }

    override fun onResume() {
        if(isStart && !isProVersion()) {
            loadInterstitial()
            isStart = false
        }

        adapter.update()
        binding.viewPager.adapter = adapter
        updateLayouts()

        super.onResume()
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == 0 && resultCode == RESULT_OK) {
            adapter.update()
            updateLayouts()
            binding.viewPager.currentItem = adapter.itemCount - 1
        }
    }


    fun removeItem(silo: Silo) {
        adapter.remove(silo)
        binding.viewPager.adapter = adapter
        updateLayouts()
    }

    fun isProVersion() = billingProcessor.isPurchased(PRODUCT_ID)

    fun purchaseProVersion() {
        billingProcessor.purchase(this, PRODUCT_ID)
    }

    override fun onProductPurchased(productId: String, details: PurchaseInfo?) {
        boughtPro = true
        recreate()
    }

    override fun onBillingInitialized() {
        billingProcessor.loadOwnedPurchasesFromGoogleAsync(null)
    }

    override fun onPurchaseHistoryRestored() { }

    override fun onBillingError(errorCode: Int, error: Throwable?) { }


    private fun updateLayouts() {
        binding.linearLayoutTexts.visibility = if(adapter.itemCount > 0) View.GONE else View.VISIBLE
    }

    private fun loadInterstitial() {
        val adRequest = AdRequest.Builder().build()
        InterstitialAd.load(this, "ca-app-pub-6111292602356970/7702380870", adRequest, object: InterstitialAdLoadCallback() {
            override fun onAdLoaded(ad: InterstitialAd) {
                ad.show(this@MainActivity)
            }
        })
    }
}