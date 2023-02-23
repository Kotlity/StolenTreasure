package net.peakgames.ginrummyplu

import android.content.Context
import com.facebook.applinks.AppLinkData
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow

object FacebookDeepLinkStuff {

    fun getFacebookDeepLink(context: Context) = callbackFlow {
        try {
            AppLinkData.fetchDeferredAppLinkData(context) { appLinkData ->
                trySend(appLinkData?.targetUri.toString())
            }
        } catch (e: Exception) { e.printStackTrace() }
        finally {
            awaitClose { close() }
        }
    }
}