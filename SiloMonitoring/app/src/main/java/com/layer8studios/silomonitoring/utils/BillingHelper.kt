package com.layer8studios.silomonitoring.utils

import android.app.Activity
import android.content.Context
import com.anjlab.android.iab.v3.BillingProcessor
import com.anjlab.android.iab.v3.TransactionDetails


class BillingHelper internal constructor(
    private val context: Context
) : BillingProcessor.IBillingHandler {

    private var billingProcessor: BillingProcessor = BillingProcessor(context, LICENSE_KEY, this)

    init {
        billingProcessor.initialize()
    }


    override fun onProductPurchased(productId: String, details: TransactionDetails?) { }

    override fun onPurchaseHistoryRestored() { }

    override fun onBillingError(errorCode: Int, error: Throwable?) {
        println(error?.message)
    }

    override fun onBillingInitialized() {
        billingProcessor.loadOwnedPurchasesFromGoogle()

        if(billingProcessor.isPurchased(PRODUCT_ID))
            println("DU BISCH A PREMIUM JO FALZ MIR")
        else println("KOAN PREMIUM HAHHHAHAHHA")
    }

    fun reset() = billingProcessor.consumePurchase(PRODUCT_ID)

    fun isProVersion() = billingProcessor.isPurchased(PRODUCT_ID)

    fun purchaseProVersion(activity: Activity) {
        val isOneTimePurchaseSupported = billingProcessor.isOneTimePurchaseSupported

        if(isOneTimePurchaseSupported)
            billingProcessor.purchase(activity, PRODUCT_ID)
    }


}