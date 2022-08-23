package com.layer8studios.silomonitoring.utils

import android.app.Activity
import android.content.Context
import com.anjlab.android.iab.v3.BillingProcessor
import com.anjlab.android.iab.v3.TransactionDetails
import com.layer8studios.silomonitoring.activities.MainActivity


class BillingHelper internal constructor(
    private val mainActivity: MainActivity,
    private val context: Context
) : BillingProcessor.IBillingHandler {

    private var billingProcessor: BillingProcessor = BillingProcessor(context, LICENSE_KEY, this)

    init {
        billingProcessor.initialize()
    }


    override fun onProductPurchased(productId: String, details: TransactionDetails?) {
        MainActivity.boughtPro = true
        mainActivity.recreate()
    }

    override fun onPurchaseHistoryRestored() { }

    override fun onBillingError(errorCode: Int, error: Throwable?) { }

    override fun onBillingInitialized() {
        billingProcessor.loadOwnedPurchasesFromGoogle()
    }

    fun reset() = billingProcessor.consumePurchase(PRODUCT_ID)

    fun isProVersion() = billingProcessor.isPurchased(PRODUCT_ID)

    fun purchaseProVersion(activity: Activity) {
        if(billingProcessor.isOneTimePurchaseSupported)
            billingProcessor.purchase(activity, PRODUCT_ID)
    }


}