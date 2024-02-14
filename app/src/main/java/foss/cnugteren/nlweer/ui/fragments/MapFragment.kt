package foss.cnugteren.nlweer.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.preference.PreferenceManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import foss.cnugteren.nlweer.ALL_ITEMS
import foss.cnugteren.nlweer.MainActivity
import foss.cnugteren.nlweer.R
import foss.cnugteren.nlweer.databinding.FragmentKnmiBinding
import java.text.SimpleDateFormat
import java.util.*

class MapFragment : Fragment() {

    private var _binding: FragmentKnmiBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    var url: String = ""
    private var imageWidth: Int = 1
    private var imageHeight: Int = 1
    private var coordinates: Array<Float> = arrayOf(0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentKnmiBinding.inflate(inflater, container, false)
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

        val root = binding.root
        // Pull down to refresh the page
        val pullToRefresh = root.findViewById<SwipeRefreshLayout>(R.id.pullToRefresh)
        pullToRefresh.setOnRefreshListener(SwipeRefreshLayout.OnRefreshListener {
            refreshPage()
            pullToRefresh.isRefreshing = false
        })

        // Do display floating navigation buttons
        activity.toggleNavigationButtons(true)

        // The web-viewer for the content
        val gifView = binding.gifView

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
        if (gifView.drawCircles && !coordinates.contentEquals(arrayOf(0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f))) {
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun setLocation(lat: Float?, lon: Float?) {
        if (lat != null && lon != null) { // Only sets if valid
            val gifView = binding.gifView
            gifView.lat = lat
            gifView.lon = lon
        }
    }

    fun refreshPage() {
        val gifView = binding.gifView
        gifView.clearCache(false)
        loadPage()
    }

    fun mapUrl(): String {
        var mapUrl = url // base URL

        // Needs time-zone based replacement
        if ("<DDHH>" in mapUrl) {
            val hourFormatter = SimpleDateFormat("HH")
            val daysFormatter = SimpleDateFormat("dd")
            val time = Calendar.getInstance(TimeZone.getTimeZone("UTC"))

            val dayNow = daysFormatter.format(time.time)
            val hourNow = if (hourFormatter.format(time.time).toInt() < 12) "00" else "12"
            time.add(Calendar.HOUR, 12)
            val dayPlus12h = daysFormatter.format(time.time)
            val hourPlus12h = if (hourFormatter.format(time.time).toInt() < 12) "00" else "12"
            time.add(Calendar.HOUR, 12)
            val dayPlus24h = daysFormatter.format(time.time)
            val hourPlus24h = if (hourFormatter.format(time.time).toInt() < 12) "00" else "12"

            // Replace the date with the current day of the month and 00 or 12 depending on the hour
            mapUrl = mapUrl.replace("<DDHH>+24", dayPlus24h + hourNow)
            mapUrl = mapUrl.replace("<DDHH>+12", dayPlus12h + hourPlus12h)
            mapUrl = mapUrl.replace("<DDHH>", dayNow + hourPlus24h)
        }
        return mapUrl
    }

    fun loadPage() {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        val backgroundColour = " " + sharedPreferences.getString("background_colour", "black") + " "

        val gifView = binding.gifView
        // Centers the image both horizontally and vertically using CSS
        gifView.loadDataWithBaseURL(null,"""
            <html>
                <head>
                    <style type='text/css'>
                        html {
                           width: 100%;
                           height: 100%;
                           background:""".trimIndent() + backgroundColour + """url(""".trimIndent() + mapUrl() + """) center center no-repeat;
                        }
                    </style>
                </head>
                <body></body>
            </html>
        """.trimIndent(),
            "text/html",  "UTF-8", null)
    }
}