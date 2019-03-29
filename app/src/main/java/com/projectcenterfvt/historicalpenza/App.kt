package com.projectcenterfvt.historicalpenza

import android.app.Application
import android.arch.lifecycle.ProcessLifecycleOwner
import com.projectcenterfvt.historicalpenza.managers.BillingManager
import timber.log.Timber

class App : Application() {

    lateinit var billingManager: BillingManager

    override fun onCreate() {
        super.onCreate()

        Timber.plant(object : Timber.DebugTree() {
            override fun createStackElementTag(element: StackTraceElement): String? {
                return "${super.createStackElementTag(element)}:${element.lineNumber}"
            }
        })

        billingManager = BillingManager.getInstance(this)
    }

}