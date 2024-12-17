package br.gohan.cifrafinder.presenter.ui.components

import android.content.Context
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient


fun webViewClientConfig(
    context: Context,
    searchUrl: String,
    pageHeight: (Int) -> Unit
): WebView {
    return WebView(context).apply {
        webViewClient = CustomWebViewClient(pageHeight)
        settings.javaScriptEnabled = true
        settings.domStorageEnabled = true
        //settings.javaScriptCanOpenWindowsAutomatically = true
        layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        loadUrl(searchUrl)
    }
}

class CustomWebViewClient(private val fullPageHeight: (Int) -> Unit) : WebViewClient() {

    override fun onPageFinished(view: WebView?, url: String?) {
        super.onPageFinished(view, url)
        view?.evaluateJavascript(
            """
            (function() {
                return document.body.scrollHeight;
            })();
            """.trimIndent()
        ) { heightString ->
            heightString?.toIntOrNull()?.let { height ->
                fullPageHeight(height)
            }
        }
    }
}
