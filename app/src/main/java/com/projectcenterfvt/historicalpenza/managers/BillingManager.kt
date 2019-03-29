package com.projectcenterfvt.historicalpenza.managers

import android.app.Activity
import android.content.Context
import android.os.Build
import android.util.Log

import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.PurchasesUpdatedListener
import com.projectcenterfvt.historicalpenza.BuildConfig
import com.projectcenterfvt.historicalpenza.data.LandmarksRepository
import com.projectcenterfvt.historicalpenza.utils.Singleton
import com.projectcenterfvt.historicalpenza.utils.toast
import timber.log.Timber

import java.util.ArrayList

class BillingManager private constructor(private val context: Context)
    : PurchasesUpdatedListener {

    private val billingClient: BillingClient = BillingClient
            .newBuilder(context)
            .setListener(this)
            .build()

    private var isServiceConnected = false
    private var billingClientResponseCode: Int = 0

    init {
        startServiceConnection()
    }

    private fun startServiceConnection() {

        billingClient.startConnection(object : BillingClientStateListener {

            override fun onBillingSetupFinished(responseCode: Int) {
                if (responseCode == BillingClient.BillingResponse.OK) {
                    isServiceConnected = true
                }
                billingClientResponseCode = responseCode
            }

            override fun onBillingServiceDisconnected() {
                isServiceConnected = false
            }

        })

    }

    override fun onPurchasesUpdated(responseCode: Int, purchases: List<Purchase>?) {
        when (responseCode) {
            BillingClient.BillingResponse.OK -> {
//                billingUpdatesListener.onPurchasesUpdated(purchases)
                context.toast("Purchased")
            }
            BillingClient.BillingResponse.USER_CANCELED -> {
                Timber.i("User cancelled the purchase flow – skipping")
            }
            else -> {
                Timber.w("Got unknown resultCode: $responseCode")
                context.toast("resultCode: $responseCode")
            }
        }
    }

    private fun executeServiceRequest(runnable: Runnable) {
        if (isServiceConnected) {
            runnable.run()
        } else {
            startServiceConnection()
        }
    }

    /*fun queryPurchases() {
        val queryToExecute = Runnable {
            val purchasesResult = billingClient.queryPurchases(BillingClient.SkuType.INAPP)
            if (areSubscriptionsSupported()) {
                val subscriptionResult = billingClient.queryPurchases(BillingClient.SkuType.SUBS)
                if (subscriptionResult.responseCode == BillingClient.BillingResponse.OK) {
                    purchasesResult.purchasesList.addAll(
                            subscriptionResult.purchasesList)
                } else {
                    // Handle any error response codes.
                }
            } else if (purchasesResult.responseCode == BillingClient.BillingResponse.OK) {
                // Skip subscription purchases query as they are not supported.
            } else {
                // Handle any other error response codes.
            }
            onQueryPurchasesFinished(purchasesResult)
        }
        executeServiceRequest(queryToExecute)
    }

    private fun onQueryPurchasesFinished(result: Purchase.PurchasesResult) {
        // Have we been disposed of in the meantime? If so, or bad result code, then quit
        if (result.responseCode != BillingClient.BillingResponse.OK) {
            Timber.w("Billing client was null or result code (${result.responseCode}) was bad – quitting")
            return
        }

        Timber.d("Query inventory was successful.")

        // Update the UI and purchases inventory with new list of purchases
        // mPurchases.clear();
        onPurchasesUpdated(BillingClient.BillingResponse.OK, result.purchasesList)
    }

    private fun areSubscriptionsSupported(): Boolean {
        val responseCode = billingClient.isFeatureSupported(BillingClient.FeatureType.SUBSCRIPTIONS)
        if (responseCode != BillingClient.BillingResponse.OK) {
            Timber.w("areSubscriptionsSupported() got an error response: $responseCode")
        }
        return responseCode == BillingClient.BillingResponse.OK
    }*/

    fun initiatePurchaseFlow(activity: Activity) {
        val purchaseFlowRequest = Runnable {
            val params = BillingFlowParams.newBuilder().apply {
                setSku(BuildConfig.SKU)
                setType(BillingClient.SkuType.INAPP)
            }.build()

            billingClient.launchBillingFlow(activity, params)
        }
        executeServiceRequest(purchaseFlowRequest)

    }

    interface BillingUpdatesListener {

        fun onBillingClientSetupFinished()

        fun onPurchasesUpdated(purchases: List<Purchase>?)

    }

    companion object : Singleton<BillingManager, Context>(::BillingManager)

}