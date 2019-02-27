package com.projectcenterfvt.historicalpenza.Activity

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.projectcenterfvt.historicalpenza.Managers.PreferencesManager
import com.projectcenterfvt.historicalpenza.R

class SplashActivity : AppCompatActivity() {

    private val preferences by lazy {
        PreferencesManager(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        if (preferences.firstTime) {
            val intent = Intent(this, SignInActivity::class.java)
            startActivity(intent)
        } else {
            val intent = Intent(this, MapActivity::class.java)
            startActivity(intent)
        }

        finish()
    }
}
