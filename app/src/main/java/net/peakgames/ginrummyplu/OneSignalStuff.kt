package net.peakgames.ginrummyplu

import android.content.Context
import com.onesignal.OneSignal
import net.peakgames.ginrummyplu.Constants.ONE_SIGNAL_APP_ID

object OneSignalStuff {

    fun setDebugLogLevelOneSignal() {
        OneSignal.setLogLevel(OneSignal.LOG_LEVEL.DEBUG, OneSignal.LOG_LEVEL.NONE)
    }

    fun initOneSignal(applicationContext: Context) {
        OneSignal.initWithContext(applicationContext)
        OneSignal.setAppId(ONE_SIGNAL_APP_ID)
    }
}