package com.projectcenterfvt.historicalpenza.data

import android.arch.lifecycle.LiveData
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.NetworkInfo
import com.projectcenterfvt.historicalpenza.utils.Singleton


class ConnectionListener(context: Context) : LiveData<Connection>() {

    private val networkReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            intent?.extras?.let {
                val activeNetwork = it.get(ConnectivityManager.EXTRA_NETWORK_INFO) as NetworkInfo
                parseNetworkInfo(activeNetwork)
            }
        }
    }

    init {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val info = cm.activeNetworkInfo
        parseNetworkInfo(info)

        val filter = IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
        context.registerReceiver(networkReceiver, filter)
    }

    private fun parseNetworkInfo(info: NetworkInfo) {
        if (info.isConnectedOrConnecting) {
            when (info.type) {
                ConnectivityManager.TYPE_WIFI -> value = Connection.WIFI_CONNECTED
                ConnectivityManager.TYPE_MOBILE -> value = Connection.MOBILE_CONNECTED
            }
        } else {
            value = Connection.NOT_CONNECTED
        }
    }

    override fun setValue(value: Connection?) {
        if (value != this.value) {
            super.setValue(value)
        }
    }

    companion object : Singleton<ConnectionListener, Context>(::ConnectionListener)
}

enum class Connection { NOT_CONNECTED, WIFI_CONNECTED, MOBILE_CONNECTED }