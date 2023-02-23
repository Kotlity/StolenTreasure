package net.peakgames.ginrummyplu

import android.content.Context
import android.util.Log
import com.google.android.gms.ads.identifier.AdvertisingIdClient
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow

object GoogleAdvertisingId {

    fun getGoogleAdvertisingId(applicationContext: Context) = callbackFlow {
        try {
            val gadid = AdvertisingIdClient.getAdvertisingIdInfo(applicationContext).id.toString()
            trySend(gadid)
        } catch (e: Exception) { e.printStackTrace() }
        finally {
            awaitClose { close() }
        }
    }
}