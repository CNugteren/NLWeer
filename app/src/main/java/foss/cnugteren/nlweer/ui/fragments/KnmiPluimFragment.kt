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

class WebClientKnmiPluim : WebViewClient() {
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
                "document.getElementsByClassName('breadcrumb')[0].style.display='none';" +
                "document.getElementsByClassName('site-header')[0].style.display='none';" +
                "document.getElementsByClassName('site-footer')[0].style.display='none';" +
                "document.getElementsByClassName('morelinks')[0].style.display='none';" +
                "document.getElementsByClassName('columns')[0].style.display='none';" +
                "document.getElementsByClassName('columns')[1].style.display='none';" +
                "document.getElementsByClassName('columns')[3].style.display='none';" +
                "document.getElementsByClassName('chart-legend__wrp')[0].style.display='none';" +
                "}\n)()")
    }
}

class KnmiPluimFragment : Fragment() {

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
        val webViewClientModified = WebClientKnmiPluim()
        webView.settings.javaScriptEnabled = true
        webView.webViewClient = webViewClientModified

        loadPage()

        return root
    }

    private fun getURL(): String {
        return "https://www.knmi.nl/nederland-nu/weer/waarschuwingen-en-verwachtingen/weer-en-klimaatpluim"
    }

    private fun refreshPage() {
        webView.clearCache(false)
        loadPage()
    }

    private fun loadPage() {
        webView.loadUrl(getURL())
    }
}