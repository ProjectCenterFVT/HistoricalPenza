package com.projectcenterfvt.historicalpenza.utils

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider

inline fun <VM : ViewModel> viewModelFactory(crossinline f: () -> VM) =
        object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(aClass: Class<T>):T = f() as T
        }