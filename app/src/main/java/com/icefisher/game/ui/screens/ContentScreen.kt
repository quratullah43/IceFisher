package com.icefisher.game.ui.screens

import android.graphics.Bitmap
import android.webkit.CookieManager
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.icefisher.game.ui.theme.IceBlue

@Composable
fun ContentScreen(
    contentLink: String,
    showBackButton: Boolean = false,
    onBackClick: () -> Unit = {}
) {
    var isInitialLoading by remember { mutableStateOf(true) }
    var browserView by remember { mutableStateOf<WebView?>(null) }
    
    BackHandler(enabled = true) {
        browserView?.let { view ->
            if (view.canGoBack()) {
                view.goBack()
            } else if (showBackButton) {
                onBackClick()
            }
        }
    }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        AndroidView(
            factory = { context ->
                WebView(context).apply {
                    browserView = this
                    
                    settings.apply {
                        javaScriptEnabled = true
                        domStorageEnabled = true
                        loadWithOverviewMode = true
                        useWideViewPort = true
                        setSupportZoom(true)
                        builtInZoomControls = true
                        displayZoomControls = false
                        allowFileAccess = true
                        allowContentAccess = true
                        mediaPlaybackRequiresUserGesture = false
                        mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
                        cacheMode = WebSettings.LOAD_NO_CACHE
                        setSupportMultipleWindows(true)
                        javaScriptCanOpenWindowsAutomatically = true
                        databaseEnabled = true
                        setGeolocationEnabled(true)
                    }
                    
                    CookieManager.getInstance().setAcceptCookie(true)
                    CookieManager.getInstance().setAcceptThirdPartyCookies(this, true)
                    
                    webViewClient = object : WebViewClient() {
                        override fun onPageStarted(view: WebView?, requestLink: String?, favicon: Bitmap?) {
                            super.onPageStarted(view, requestLink, favicon)
                        }
                        
                        override fun onPageFinished(view: WebView?, requestLink: String?) {
                            super.onPageFinished(view, requestLink)
                            if (isInitialLoading) {
                                isInitialLoading = false
                            }
                        }
                        
                        override fun shouldOverrideUrlLoading(
                            view: WebView?,
                            request: WebResourceRequest?
                        ): Boolean {
                            return false
                        }
                    }
                    
                    webChromeClient = WebChromeClient()
                    
                    loadUrl(contentLink)
                }
            },
            modifier = Modifier.fillMaxSize()
        )
        
        if (isInitialLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(48.dp),
                    color = IceBlue,
                    strokeWidth = 4.dp
                )
            }
        }
    }
}
