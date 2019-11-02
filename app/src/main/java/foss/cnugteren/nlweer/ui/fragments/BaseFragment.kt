package foss.cnugteren.nlweer.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import androidx.fragment.app.Fragment
import foss.cnugteren.nlweer.R
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout

open class BaseFragment : Fragment() {

    private lateinit var gifView: WebView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_knmi, container, false)

        // Pull down to refresh the page
        val pullToRefresh = root.findViewById<SwipeRefreshLayout>(R.id.pullToRefresh)
        pullToRefresh.setOnRefreshListener(SwipeRefreshLayout.OnRefreshListener {
            refresh() // your code
            pullToRefresh.setRefreshing(false)
        })

        // The web-viewer for the content
        gifView = root.findViewById(R.id.gif_view) as WebView
        gifView.loadUrl(getURL())
        gifView.settings.loadWithOverviewMode = true
        gifView.settings.useWideViewPort = true
        return root
    }

    open fun getURL(): String {
        return "" // Implemented in derived classes
    }

    fun refresh() {
        gifView.loadUrl(getURL())
    }
}