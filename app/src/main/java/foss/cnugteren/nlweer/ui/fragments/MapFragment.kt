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
import androidx.navigation.findNavController
import androidx.preference.PreferenceManager
import foss.cnugteren.nlweer.ALL_ITEMS
import foss.cnugteren.nlweer.MainActivity

class MapFragment : Fragment() {

    private lateinit var gifView: DrawWebView

    var url: String = ""
    private var imageWidth: Int = 1
    private var imageHeight: Int = 1
    private var coordinates: Array<Float> = arrayOf(0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_knmi, container, false)
        val activity = this.activity as MainActivity

        // Iterate over all items to find the current one and set the private variables accordingly
        val navController = activity.findNavController(R.id.nav_host_fragment)
        val thisNavId = navController.currentDestination?.id
        for (item in ALL_ITEMS) {
            if (item.navId == thisNavId) {
                url = item.mapUrl
                imageWidth = item.imageWidth
                imageHeight = item.imageHeight
                coordinates = item.coordinates
                break
            }
        }

        // Pull down to refresh the page
        val pullToRefresh = root.findViewById<SwipeRefreshLayout>(R.id.pullToRefresh)
        pullToRefresh.setOnRefreshListener(SwipeRefreshLayout.OnRefreshListener {
            refreshPage()
            pullToRefresh.isRefreshing = false
        })

        // Do display floating navigation buttons
        activity.toggleNavigationButtons(true)

        // The web-viewer for the content
        gifView = root.findViewById(R.id.gif_view) as DrawWebView

        // Sets the scale of the image
        root.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                root.viewTreeObserver.removeOnGlobalLayoutListener(this)
                gifView.fullWidth = root.width
                gifView.fullHeight = root.height
                gifView.imageWidth = imageWidth
                gifView.imageHeight = imageHeight
                gifView.imageCoordinates = coordinates
                gifView.setWidthFittingScale()
                loadPage()
            }
        })

        // Set the location (latitude and longitude)
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        gifView.drawCircles = sharedPreferences.getBoolean("location_enable", false)
        if (gifView.drawCircles) {
            val gpsEnable = sharedPreferences.getBoolean("gps_enable", false)
            if (!gpsEnable) { // Sets the lat/lon from manual source
                val lat = sharedPreferences.getString("location_latitude", null)?.toFloatOrNull()
                val lon = sharedPreferences.getString("location_longitude", null)?.toFloatOrNull()
                setLocation(lat, lon)
            }
            else { // Sets from the latest known values from the main activity
                val thisActivity = this.activity as MainActivity
                setLocation(thisActivity.gpsLat, thisActivity.gpsLon)
            }
        }

        return root
    }

    fun setLocation(lat: Float?, lon: Float?) {
        if (lat != null && lon != null) { // Only sets if valid
            gifView.lat = lat
            gifView.lon = lon
        }
    }

    fun refreshPage() {
        gifView.clearCache(false)
        loadPage()
    }

    fun loadPage() {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        val backgroundColour = " " + sharedPreferences.getString("background_colour", "black") + " "

        // Centers the image both horizontally and vertically using CSS
        gifView.loadDataWithBaseURL(null,"""
            <html>
                <head>
                    <style type='text/css'>
                        html {
                           width: 100%;
                           height: 100%;
                           background:""".trimIndent() + backgroundColour + """url(""".trimIndent() + url + """) center center no-repeat;
                        }
                    </style>
                </head>
                <body></body>
            </html>
        """.trimIndent(),
            "text/html",  "UTF-8", null)
    }
}