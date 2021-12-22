package com.complete.convo.activities

import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import android.webkit.WebView
import android.webkit.WebViewClient
import com.complete.convo.R
import com.complete.convo.databinding.FragmentBlankBinding

class BlankFragment(val msg :String) : Fragment() {
    private var binding: FragmentBlankBinding? = null
    private lateinit var webView : WebView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentBlankBinding.inflate(inflater, container, false)
        val root: View = binding!!.root

        webView = binding!!.webView
        if(savedInstanceState != null){
            webView.restoreState(savedInstanceState)
            Log.d("taget","saved")
        }else{
            webView.loadUrl("https://google.com/search?q=")
        }

        val webSettings = binding!!.webView.settings
        webSettings.javaScriptEnabled = true
        webSettings.allowFileAccess = true
        webView.webViewClient = WebViewClient()

        webView.canGoBack()
        webView.setOnKeyListener(View.OnKeyListener{ v, keyCode, event ->
            if(keyCode == KeyEvent.KEYCODE_BACK && event.action == MotionEvent.ACTION_UP && binding!!.webView.canGoBack()){
                webView.goBack()
                return@OnKeyListener true
            }
            false
        })



        return root
    }

}