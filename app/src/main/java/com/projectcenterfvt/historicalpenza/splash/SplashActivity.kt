package com.projectcenterfvt.historicalpenza.splash

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.projectcenterfvt.historicalpenza.sign_in.SignInActivity
import com.projectcenterfvt.historicalpenza.data.Preferences
import com.projectcenterfvt.historicalpenza.R
import com.projectcenterfvt.historicalpenza.map.MapActivity

class SplashActivity : AppCompatActivity() {

    private val preferences by lazy {
        Preferences.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme_Launcher)
        super.onCreate(savedInstanceState)

        if (preferences.token.isEmpty()) {
            val intent = Intent(this, SignInActivity::class.java)
            startActivity(intent)
        } else {
            val intent = Intent(this, MapActivity::class.java)
            startActivity(intent)
        }

        finish()
    }
}
