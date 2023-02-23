package net.peakgames.ginrummyplu

import android.content.Context
import com.appsflyer.AppsFlyerConversionListener
import com.appsflyer.AppsFlyerLib
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import net.peakgames.ginrummyplu.Constants.APPS_FLYER_KEY

object AppsFlyerStuff {

    fun getAppsFlyerConversion(context: Context) = callbackFlow {
        try {
            AppsFlyerLib.getInstance().init(APPS_FLYER_KEY, object: AppsFlyerConversionListener {

                override fun onConversionDataSuccess(p0: MutableMap<String, Any>?) {
                    trySend(p0)
                }

                override fun onConversionDataFail(p0: String?) {
                    trySend(null)
                }

                override fun onAppOpenAttribution(p0: MutableMap<String, String>?) {

                }

                override fun onAttributionFailure(p0: String?) {

                }
            }, context)
            AppsFlyerLib.getInstance().start(context)
        } catch (e: Exception) { e.printStackTrace() }
        finally {
            awaitClose { close() }
        }
    }
}