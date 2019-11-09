package foss.cnugteren.nlweer.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import androidx.fragment.app.Fragment
import foss.cnugteren.nlweer.R
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import kotlin.math.min
import android.view.ViewTreeObserver

abstract class BaseFragment : Fragment() {

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
            refresh()
            pullToRefresh.setRefreshing(false)
        })

        // The web-viewer for the content
        gifView = root.findViewById(R.id.gif_view) as WebView
        gifView.loadUrl(getURL())

        // Sets the scale of the image
        root.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                root.viewTreeObserver.removeOnGlobalLayoutListener(this)
                val scaleWidth = (100f * root.width / imageWidth())
                val scaleHeight = (100f * root.height / imageHeight())
                gifView.setInitialScale(min(scaleWidth, scaleHeight).toInt())
            }
        })

        return root
    }

    abstract fun imageWidth(): Int // Implemented in derived classes
    abstract fun imageHeight(): Int // Implemented in derived classes

    abstract fun getURL(): String // Implemented in derived classes

    fun refresh() {
        gifView.loadUrl(getURL())
    }
}