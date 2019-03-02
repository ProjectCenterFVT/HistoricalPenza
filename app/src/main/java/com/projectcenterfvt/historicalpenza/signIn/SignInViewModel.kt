package com.projectcenterfvt.historicalpenza.signIn

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.ViewModel
import android.content.Context
import com.projectcenterfvt.historicalpenza.data.Connection
import com.projectcenterfvt.historicalpenza.data.ConnectionListener
import com.projectcenterfvt.historicalpenza.data.network.AuthNetwork
import com.projectcenterfvt.historicalpenza.utils.SingleLiveEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.lang.Exception

class SignInViewModel(context: Context, private val auth: AuthNetwork) : ViewModel() {

    private val viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    private val connectionListener = ConnectionListener.getInstance(context)

    private val _toast = SingleLiveEvent<String>()
    val toast: LiveData<String>
        get() = _toast

    private val _snackbar = SingleLiveEvent<String>()
    val snackbar: LiveData<String>
        get() = _snackbar

    private val _loggedIn = SingleLiveEvent<Any>()
    val loggedIn: LiveData<Any>
        get() = _loggedIn

    fun canLogIn(): Boolean {
        if (connectionListener.value == Connection.NOT_CONNECTED) {
            _snackbar.value = "Нет поключения к Интернету"
            return false
        }
        return true
    }

    fun tryLogIn(tokenId: String) {
        if (connectionListener.value == Connection.NOT_CONNECTED) {
            _snackbar.value = "Нет поключения к Интернету"
            return
        }
        uiScope.launch {
            try {
                auth.logIn(tokenId)
                _loggedIn.call()
            } catch (e: Exception) {
                _toast.value = e.message
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

}