package foss.cnugteren.nlweer.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import foss.cnugteren.nlweer.MainActivity
import foss.cnugteren.nlweer.R

class WebClientBuienradarPluim : WebViewClient() {
    // From https://stackoverflow.com/questions/14423981/android-webview-display-only-some-part-of-website

    override fun shouldOverrideUrlLoading(
        view: WebView,
        url: String
    ): Boolean {
        view.loadUrl(url)
        return true
    }

    override fun onPageFinished(view: WebView, url: String) {
        view.loadUrl("javascript:(function() {" +
                "document.getElementById('header').style.display='none';" +
                "document.getElementById('footer').style.display='none';" +
                "document.getElementById('adholderContainerFooterSmall').style.display='none';" +
                "document.getElementsByClassName('control-block')[0].style.display='none';" +
                "document.getElementsByClassName('side-content')[0].style.display='none';" +
                "}\n)()")
    }
}

class BuienradarPluimFragment : Fragment() {

    private lateinit var webView: WebView

    private lateinit var root: View

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        root = inflater.inflate(R.layout.fragment_knmi_pluim, container, false)

        // Pull down to refresh the page
        val pullToRefresh = root.findViewById<SwipeRefreshLayout>(R.id.pullToRefresh)
        pullToRefresh.setOnRefreshListener(SwipeRefreshLayout.OnRefreshListener {
            refreshPage()
            pullToRefresh.isRefreshing = false
        })

        // Do display floating navigation buttons
        val activity = this.activity as MainActivity
        activity.toggleNavigationButtons(true)

        // The web-viewer for the content
        webView = root.findViewById(R.id.web_view) as WebView
        val webViewClientModified = WebClientBuienradarPluim()
        webView.settings.javaScriptEnabled = true
        webView.webViewClient = webViewClientModified

        loadPage()

        return root
    }

    private fun getURL(): String {
        return "https://www.buienradar.nl/nederland/verwachtingen/de-pluim"
    }

    private fun refreshPage() {
        webView.clearCache(false)
        loadPage()
    }

    private fun loadPage() {
        webView.loadUrl(getURL())
    }
}