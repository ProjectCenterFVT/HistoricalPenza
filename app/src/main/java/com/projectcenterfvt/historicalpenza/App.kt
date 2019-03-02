package com.projectcenterfvt.historicalpenza

import android.app.Application
import com.projectcenterfvt.historicalpenza.data.Connection
import com.projectcenterfvt.historicalpenza.data.ConnectionListener
import timber.log.Timber

class App : Application() {

    override fun onCreate() {
        super.onCreate()

        Timber.plant(object : Timber.DebugTree() {
            override fun createStackElementTag(element: StackTraceElement): String? {
                return "${super.createStackElementTag(element)}:${element.lineNumber}"
            }
        })
    }

}