package net.peakgames.ginrummyplu

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.*
import androidx.navigation.fragment.navArgs
import net.peakgames.ginrummyplu.Constants.DOMAIN_LINK
import net.peakgames.ginrummyplu.databinding.FragmentWebViewBinding
import net.peakgames.utilsstufflibrary.Utils.navigateToAnotherActivityFromFragment
import net.peakgames.utilsstufflibrary.Utils.onBackPressedCase

class WebViewFragment : Fragment() {
    private lateinit var binding: FragmentWebViewBinding
    private val args by navArgs<WebViewFragmentArgs>()
    private var vCallback: ValueCallback<Array<Uri>>? = null
    private lateinit var daoSession: DaoSession
    private lateinit var finalUrlEntityDao: FinalUrlEntityDao

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentWebViewBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initDB()
        onBackPressedCase(binding.webView)
        setWebViewSettings()
        setWebViewAcceptCookies()
        setClientsToWebView()
        handleWebViewSavedInstanceState(savedInstanceState)
    }

    private fun initDB() {
        daoSession = (requireActivity().application as FlamingCoinsApplication).daoSession
        finalUrlEntityDao = daoSession.finalUrlEntityDao
    }

    private fun insertFinalUrlToDao(finalUrl: String?) {
        val finalUrlEntity = FinalUrlEntity()
        finalUrlEntity.finalUrlValue = finalUrl
        finalUrlEntityDao.insertOrReplace(finalUrlEntity)
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun setWebViewSettings() {
        binding.webView.settings.apply {
            javaScriptEnabled = true
            domStorageEnabled = true
            userAgentString.replace("wv", "")
        }
    }

    private fun setWebViewAcceptCookies() {
        CookieManager.getInstance().apply {
            setAcceptCookie(true)
            setAcceptThirdPartyCookies(binding.webView, true)
        }
    }

    private fun setClientsToWebView() {
        binding.webView.apply {
            webViewClient = object: WebViewClient() {
                override fun onPageFinished(view: WebView?, url: String?) {
                    super.onPageFinished(view, url)
                    if (DOMAIN_LINK == url) navigateToAnotherActivityFromFragment(GameActivity())
                    else insertFinalUrlToDao(url)
                    CookieManager.getInstance().flush()
                }
            }
            webChromeClient = object: WebChromeClient() {
                override fun onShowFileChooser(webView: WebView?, filePathCallback: ValueCallback<Array<Uri>>?, fileChooserParams: FileChooserParams?): Boolean {
                    vCallback = filePathCallback
                    createChooser()
                    return true
                }
            }
        }
    }

    private fun createChooser() {
        val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "image/*"
        }
        startActivityForResult(Intent.createChooser(intent, "Image Chooser"), 1)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (vCallback == null) return
            vCallback!!.onReceiveValue(WebChromeClient.FileChooserParams.parseResult(resultCode, data))
            vCallback = null
        }
        else if (resultCode == Activity.RESULT_CANCELED) vCallback?.onReceiveValue(null)
    }

    private fun handleWebViewSavedInstanceState(savedInstanceState: Bundle?) {
        if (savedInstanceState == null) {
            val finalUrl = args.finalUrl
            binding.webView.loadUrl(finalUrl)
        }
        else binding.webView.restoreState(savedInstanceState)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        binding.webView.saveState(outState)
    }

}