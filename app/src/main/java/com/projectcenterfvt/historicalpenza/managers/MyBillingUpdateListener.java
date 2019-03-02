package com.projectcenterfvt.historicalpenza.managers;

import android.util.Log;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.Purchase;

import java.util.List;

/**
 * Created by MaksimS on 13.08.2018.
 */
public class MyBillingUpdateListener implements BillingManager.BillingUpdatesListener {
    @Override
    public void onBillingClientSetupFinished() {


    }

    @Override
    public void onConsumeFinished(String token, int result) {

        if (result == BillingClient.BillingResponse.OK) {
            //открыть в БД

        }else{
            Log.w("BillingManager","Not bouthe");
        }

    }

    @Override
    public void onPurchasesUpdated(List<Purchase> purchases) {

        for (Purchase p : purchases) {

            //update ui

        }



    }
}