package com.layer8studios.silomonitoring.utils

import android.app.Activity
import android.content.Context
import com.anjlab.android.iab.v3.BillingProcessor
import com.anjlab.android.iab.v3.BillingProcessor.IPurchasesResponseListener
import com.anjlab.android.iab.v3.PurchaseInfo
import com.layer8studios.silomonitoring.activities.MainActivity


class BillingHelper internal constructor(
    private val mainActivity: MainActivity,
    private val context: Context
) : BillingProcessor.IBillingHandler, IPurchasesResponseListener {

    private var billingProcessor: BillingProcessor = BillingProcessor(context, LICENSE_KEY, this)

    init {
        billingProcessor.initialize()
    }

    override fun onProductPurchased(productId: String, details: PurchaseInfo?) {     }

    override fun onPurchaseHistoryRestored() { }

    override fun onBillingError(errorCode: Int, error: Throwable?) { }

    override fun onBillingInitialized() {
        billingProcessor.loadOwnedPurchasesFromGoogleAsync(this)
    }

    fun isProVersion() = billingProcessor.isPurchased(PRODUCT_ID)

    fun purchaseProVersion(activity: Activity) {
        billingProcessor.purchase(activity, PRODUCT_ID)
    }

    override fun onPurchasesSuccess() {
        MainActivity.boughtPro = true
        mainActivity.recreate()
    }

    override fun onPurchasesError() {
    }


}