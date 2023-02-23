package net.peakgames.ginrummyplu

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import net.peakgames.utilsstufflibrary.Utils.pushOneSignal

class FlamingCoinsViewModel: ViewModel() {

    val appsConversion = MutableLiveData<MutableMap<String, Any>?>()
    val deepLink = MutableLiveData<String>()
    val googleAdvertisingId = MutableLiveData<String>()

    fun getFacebookDeepLinkAndAppsFlyerConversion(context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            AppsFlyerStuff.getAppsFlyerConversion(context).collect { conversion ->
                FacebookDeepLinkStuff.getFacebookDeepLink(context).collect { deep ->
                    appsConversion.postValue(conversion)
                    deepLink.postValue(deep)
                }
            }
        }
    }

    fun getGadid(applicationContext: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            GoogleAdvertisingId.getGoogleAdvertisingId(applicationContext).collect { gadid ->
                googleAdvertisingId.postValue(gadid)
            }
        }
    }
}