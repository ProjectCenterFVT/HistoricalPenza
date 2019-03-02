package com.projectcenterfvt.historicalpenza.data

import android.content.Context
import com.projectcenterfvt.historicalpenza.R
import com.projectcenterfvt.historicalpenza.utils.Singleton

class Preferences private constructor(context: Context) {

    private val preferences = context.getSharedPreferences(
            context.getString(R.string.preference_file_key), Context.MODE_PRIVATE)

    var token: String
        get() = preferences.getString(PREFERENCES_TOKEN, "")!!
        set(value) = preferences.edit().putString(PREFERENCES_TOKEN, value).apply()

    var showGreeting: Boolean
        get() = preferences.getBoolean(PREFERENCES_SHOW_GREETING, true)
        set(value) = preferences.edit().putBoolean(PREFERENCES_SHOW_GREETING, value).apply()

    var shouldNotify: Boolean
        get() = preferences.getBoolean(PREFERENCES_SHOULD_NOTIFY, true)
        set(value) = preferences.edit().putBoolean(PREFERENCES_SHOULD_NOTIFY, value).apply()

    companion object : Singleton<Preferences, Context>(::Preferences) {

        private const val PREFERENCES_TOKEN = "token"
        private const val PREFERENCES_SHOW_GREETING = "showGreeting"
        private const val PREFERENCES_SHOULD_NOTIFY = "shouldNotify"

    }

}
