package net.peakgames.utilsstufflibrary

import android.app.Activity
import android.content.Intent
import android.webkit.WebView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.onesignal.OneSignal

object Utils {

    fun Activity.navigateToAnotherActivityFromActivity(activity: AppCompatActivity) {
        Intent(this, activity::class.java).apply {
            startActivity(this)
            finish()
        }
    }

    fun Fragment.navigateToAnotherActivityFromFragment(activity: AppCompatActivity) {
        Intent(requireActivity(), activity::class.java).apply {
            startActivity(this)
            requireActivity().finish()
        }
    }

    fun <T> pushOneSignal(value: T) {
        OneSignal.sendTag("push", value.toString())
    }

    fun Fragment.onBackPressedCase(webView: WebView) {
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object: OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (webView.canGoBack()) webView.goBack()
            }
        })
    }
}