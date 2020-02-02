package com.nanicky.devteam.splash

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import com.nanicky.devteam.R
import com.nanicky.devteam.common.BaseActivity
import com.nanicky.devteam.getStarted.GetStartedActivity
import com.nanicky.devteam.main.MainActivity
import com.nanicky.devteam.onBoarding.OnBoardingActivity
import org.koin.android.ext.android.inject

class SplashActivity : BaseActivity() {

    private val preferences: SharedPreferences by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        goToNextScreen()
        return
        setContentView(R.layout.activity_splash)

    }

    private fun goToNextScreen() {
        val nextActivity =
            if (!preferences.getBoolean(OnBoardingActivity.HAS_SEEN_ON_BOARDING, false)) {
                OnBoardingActivity::class.java
            } else if (!isPermissionGranted(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                GetStartedActivity::class.java
            } else {
                MainActivity::class.java
            }
        val intent = Intent(this, nextActivity)
        startActivity(intent)
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
    }
}
