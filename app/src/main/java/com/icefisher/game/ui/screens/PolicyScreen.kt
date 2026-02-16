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
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.icefisher.game.R
import com.icefisher.game.ui.theme.DeepBlue
import com.icefisher.game.ui.theme.IceBlue
import com.icefisher.game.ui.theme.SnowWhite

@Composable
fun PolicyScreen(
    policyLink: String,
    onBackClick: () -> Unit
) {
    var isInitialLoading by remember { mutableStateOf(true) }
    
    BackHandler {
        onBackClick()
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DeepBlue)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(DeepBlue)
                .padding(8.dp)
        ) {
            IconButton(
                onClick = onBackClick,
                modifier = Modifier.align(Alignment.CenterStart)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_back),
                    contentDescription = "Back",
                    tint = SnowWhite,
                    modifier = Modifier.size(28.dp)
                )
            }
            
            Text(
                text = "Privacy Policy",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = SnowWhite,
                modifier = Modifier.align(Alignment.Center)
            )
        }
        
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
        ) {
            AndroidView(
                factory = { context ->
                    WebView(context).apply {
                        settings.apply {
                            javaScriptEnabled = true
                            domStorageEnabled = true
                            loadWithOverviewMode = true
                            useWideViewPort = true
                            cacheMode = WebSettings.LOAD_NO_CACHE
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
                        
                        loadUrl(policyLink)
                    }
                },
                modifier = Modifier.fillMaxSize()
            )
            
            if (isInitialLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.White),
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
}
