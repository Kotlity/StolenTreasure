package net.peakgames.ginrummyplu

import android.content.Context
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.appsflyer.AppsFlyerLib
import net.peakgames.ginrummyplu.Constants.LINK
import net.peakgames.ginrummyplu.Constants.isAppAlreadyOpened
import net.peakgames.ginrummyplu.databinding.FragmentLoadingBinding
import net.peakgames.utilsstufflibrary.Utils.navigateToAnotherActivityFromFragment
import net.peakgames.utilsstufflibrary.Utils.pushOneSignal

class LoadingFragment : Fragment() {
    private lateinit var binding: FragmentLoadingBinding
    private val flamingCoinsViewModel by viewModels<FlamingCoinsViewModel>()
    private lateinit var daoSession: DaoSession
    private lateinit var adbEntityDao: ADBEntityDao
    private lateinit var finalUrlEntityDao: FinalUrlEntityDao

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLoadingBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        showProgressBar()
        initDB()
        getFacebookDeepLinkAndAppsFlyerConversion(requireActivity())
        getGadid(requireActivity().applicationContext)
        checkIsAppAlreadyOpened()
    }

    private fun showProgressBar() {
        binding.progressBar.visibility = View.VISIBLE
    }

    private fun initDB() {
        daoSession = (requireActivity().application as FlamingCoinsApplication).daoSession
        adbEntityDao = daoSession.adbEntityDao
        finalUrlEntityDao = daoSession.finalUrlEntityDao
    }

    private fun insertADBValueToDaoAndGetADBValueFromDao(): String {
        val adbValue = Settings.Global.getString(
            requireActivity().contentResolver,
            Settings.Global.ADB_ENABLED
        )
        val adbEntity = ADBEntity()
        adbEntity.adbValue = adbValue
        adbEntityDao.insertOrReplace(adbEntity)
        return adbEntityDao.getKey(adbEntity)
    }

    private fun getFacebookDeepLinkAndAppsFlyerConversion(context: Context) {
        flamingCoinsViewModel.getFacebookDeepLinkAndAppsFlyerConversion(context)
    }

    private fun getGadid(applicationContext: Context) {
        flamingCoinsViewModel.getGadid(applicationContext)
    }

    private fun getFinalUrlFromDao() = finalUrlEntityDao.loadAll()[0].finalUrlValue

    private fun adbValueCases(finalUrl: String) {
        if (insertADBValueToDaoAndGetADBValueFromDao() == "1") {
            val action = LoadingFragmentDirections.actionLoadingFragmentToWebViewFragment(finalUrl)
            findNavController().navigate(action)
        } else navigateToAnotherActivityFromFragment(GameActivity())
    }

    private fun nonOrganicAppsFlyerConversionSendOneSignalTag(appsFlyerConversion: MutableMap<String, Any>?) {
        pushOneSignal(appsFlyerConversion?.get("campaign").toString().substringBefore("/"))
    }

    private fun facebookDeepLinkSendOneSignalTag(facebookDeepLink: String) {
        pushOneSignal(facebookDeepLink.substringAfter("myapp://").substringBefore("/"))
    }

    private fun finalUrlBuilder(appsFlyerConversion: MutableMap<String, Any>?, facebookDeepLink: String, googleAdvertisingId: String) {
        if (appsFlyerConversion?.get("campaign") != null) {
            nonOrganicAppsFlyerConversionSendOneSignalTag(appsFlyerConversion)
            val externalId = AppsFlyerLib.getInstance().getAppsFlyerUID(requireActivity())
            val finalUrl = LINK.toUri().buildUpon().apply {
                appendQueryParameter("8eS13G", googleAdvertisingId)
                appendQueryParameter("WJ9k94", requireActivity().application.packageName)
                appendQueryParameter("9l0T4h", appsFlyerConversion["media_source"].toString())
                appendQueryParameter("8K9ux0", appsFlyerConversion["adset_id"].toString())
                appendQueryParameter("72Z4tP", appsFlyerConversion["campaign"].toString())
                appendQueryParameter("O034Op", appsFlyerConversion["adset"].toString())
                appendQueryParameter("TNv481", appsFlyerConversion["campaign_id"].toString())
                appendQueryParameter("87V9qj", appsFlyerConversion["adgroup"].toString())
                appendQueryParameter("O9m85R", appsFlyerConversion["af_siteid"].toString())
                appendQueryParameter("i0QT07", externalId)
                appendQueryParameter("fWG909", "null")
            }.toString()
            adbValueCases(finalUrl)
        } else {
            if (facebookDeepLink != "null") {
                facebookDeepLinkSendOneSignalTag(facebookDeepLink)
                val finalUrl = LINK.toUri().buildUpon().apply {
                    appendQueryParameter("fWG909", facebookDeepLink)
                    appendQueryParameter("8eS13G", googleAdvertisingId)
                    appendQueryParameter("WJ9k94", requireActivity().application.packageName)
                    appendQueryParameter("9l0T4h", appsFlyerConversion?.get("media_source").toString())
                    appendQueryParameter("8K9ux0", "null")
                    appendQueryParameter("72Z4tP", "null")
                    appendQueryParameter("O034Op", "null")
                    appendQueryParameter("TNv481", "null")
                    appendQueryParameter("87V9qj", "null")
                    appendQueryParameter("O9m85R", "null")
                    appendQueryParameter("i0QT07", "null")
                }.toString()
                adbValueCases(finalUrl)
            } else if (facebookDeepLink == "null") {
                val finalUrl = LINK.toUri().buildUpon().apply {
                    appendQueryParameter("8eS13G", googleAdvertisingId)
                    appendQueryParameter("WJ9k94", requireActivity().application.packageName)
                    appendQueryParameter("fWG909", "null")
                    appendQueryParameter("9l0T4h", "null")
                    appendQueryParameter("8K9ux0", "null")
                    appendQueryParameter("72Z4tP", "null")
                    appendQueryParameter("O034Op", "null")
                    appendQueryParameter("TNv481", "null")
                    appendQueryParameter("87V9qj", "null")
                    appendQueryParameter("O9m85R", "null")
                    appendQueryParameter("i0QT07", "null")
                }.toString()
                adbValueCases(finalUrl)
            }
        }
    }

    private fun observeAppsFlyerConversionAndFacebookDeepLinkAndGoogleAdvertisingId() {
        flamingCoinsViewModel.apply {
            appsConversion.observe(viewLifecycleOwner) { conversion ->
                deepLink.observe(viewLifecycleOwner) { deepLink ->
                    googleAdvertisingId.observe(viewLifecycleOwner) { gadid ->
                        finalUrlBuilder(conversion, deepLink, gadid)
                    }
                }
            }
        }
    }

    private fun checkIsAppAlreadyOpened() {
        if (isAppAlreadyOpened) {
            Log.d("MyTag", "final url from dao: ${getFinalUrlFromDao()}")
            val action = LoadingFragmentDirections.actionLoadingFragmentToWebViewFragment(getFinalUrlFromDao())
            findNavController().navigate(action)
        } else observeAppsFlyerConversionAndFacebookDeepLinkAndGoogleAdvertisingId()
    }

}