package net.peakgames.ginrummyplu

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.preference.PreferenceManager
import net.peakgames.ginrummyplu.Constants.isAppAlreadyOpened
import net.peakgames.utilsstufflibrary.Utils.navigateToAnotherActivityFromActivity

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {
    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        setContentView(R.layout.activity_splash)
        isAppAlreadyOpenedSharedPreferencesStuff()
        navigation()
    }

    private fun isAppAlreadyOpenedSharedPreferencesStuff() {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        isAppAlreadyOpened = sharedPreferences.getBoolean("isAppAlreadyOpened", false)
        val sharedPreferencesEditor = sharedPreferences.edit()
        sharedPreferencesEditor.putBoolean("isAppAlreadyOpened", true).apply()
    }

    private fun navigation() {
        Handler(Looper.getMainLooper()).postDelayed({
            navigateToAnotherActivityFromActivity(LoadingActivity())
        }, 3000)
    }
}