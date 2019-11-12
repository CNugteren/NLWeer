package foss.cnugteren.nlweer.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import foss.cnugteren.nlweer.DrawWebView
import androidx.fragment.app.Fragment
import foss.cnugteren.nlweer.R
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import android.view.ViewTreeObserver
import androidx.preference.PreferenceManager

abstract class BaseFragment : Fragment() {

    private lateinit var gifView: DrawWebView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_knmi, container, false)

        // Pull down to refresh the page
        val pullToRefresh = root.findViewById<SwipeRefreshLayout>(R.id.pullToRefresh)
        pullToRefresh.setOnRefreshListener(SwipeRefreshLayout.OnRefreshListener {
            refreshPage()
            pullToRefresh.isRefreshing = false
        })

        // The web-viewer for the content
        gifView = root.findViewById(R.id.gif_view) as DrawWebView

        // Sets the scale of the image
        root.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                root.viewTreeObserver.removeOnGlobalLayoutListener(this)
                gifView.fullWidth = root.width
                gifView.fullHeight = root.height
                gifView.imageWidth = imageWidth()
                gifView.imageHeight = imageHeight()
                gifView.imageCoordinates = coordinates()
                gifView.setWidthFittingScale()
                loadPage()
            }
        })

        // Set the location (latitude and longitude)
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        gifView.drawCircles = sharedPreferences.getBoolean("location_enable", false)
        if (gifView.drawCircles) {
            val latString = sharedPreferences.getString("location_latitude", null)?.toFloatOrNull()
            val lonString = sharedPreferences.getString("location_longitude", null)?.toFloatOrNull()
            if (latString != null && lonString != null) {
                gifView.lat = latString
                gifView.lon = lonString
            }
        }

        return root
    }

    abstract fun imageWidth(): Int // Implemented in derived classes
    abstract fun imageHeight(): Int // Implemented in derived classes
    abstract fun coordinates(): Array<Float> // Implemented in derived classes

    abstract fun getURL(): String // Implemented in derived classes

    fun refreshPage() {
        gifView.clearCache(false)
        loadPage()
    }

    fun loadPage() {
        // Centers the image both horizontally and vertically using CSS
        gifView.loadDataWithBaseURL(null,"""
            <html>
                <head>
                    <style type='text/css'>
                        html {
                           width: 100%;
                           height: 100%;
                           background: black url(""".trimIndent() + getURL() + """) center center no-repeat;
                        }
                    </style>
                </head>
                <body></body>
            </html>
        """.trimIndent(),
            "text/html",  "UTF-8", null)
    }
}