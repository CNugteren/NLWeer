package foss.cnugteren.nlweer.ui.fragments

import android.graphics.Color
import android.os.AsyncTask
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.preference.PreferenceManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.ValueFormatter
import foss.cnugteren.nlweer.MainActivity
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import kotlin.math.pow
import foss.cnugteren.nlweer.R
import foss.cnugteren.nlweer.databinding.FragmentBuienradarChartBinding
import kotlin.math.max


class BuienradarChartFragment : Fragment() {

    private var latitude: Float? = null
    private var longitude: Float? = null
    private var _binding: FragmentBuienradarChartBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentBuienradarChartBinding.inflate(inflater, container, false)
        val root = binding.root

        // Pull down to refresh the page
        val pullToRefresh = root.findViewById<SwipeRefreshLayout>(R.id.pullToRefresh)
        pullToRefresh.setOnRefreshListener(SwipeRefreshLayout.OnRefreshListener {
            refreshPage()
            pullToRefresh.isRefreshing = false
        })

        // Do display floating navigation buttons
        val activity = this.activity as MainActivity
        activity.toggleNavigationButtons(true)

        // Set the location (latitude and longitude)
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        val locationEnabled = sharedPreferences.getBoolean("location_enable", false)
        if (locationEnabled) {
            val gpsEnable = sharedPreferences.getBoolean("gps_enable", false)
            if (!gpsEnable) { // Sets the lat/lon from manual source
                val lat = sharedPreferences.getString("location_latitude", null)?.toFloatOrNull()
                val lon = sharedPreferences.getString("location_longitude", null)?.toFloatOrNull()
                setLocation(lat, lon)
            }
            else { // Sets from the latest known values from the main activity
                setLocation(activity.gpsLat, activity.gpsLon)
            }
        }

        loadPage()

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun setLocation(lat: Float?, lon: Float?) {
        if (lat != null && lon != null) { // Only sets if valid
            latitude = lat
            longitude = lon
        }
    }

    fun getURL(): String {
        return "https://gpsgadget.buienradar.nl/data/raintext?lat=" + latitude.toString() + "&lon=" + longitude.toString()
    }

    fun refreshPage() {
        loadPage()
    }

    fun loadPage() {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        val darkMode = sharedPreferences.getString("dark_mode", "dark_mode_no")
        var textColour = Color.BLACK
        var background = Color.WHITE
        if (darkMode == "dark_mode_yes") {
            textColour = Color.WHITE
            background = Color.rgb(46, 46, 46) // matches Android's dark mode colours
        }

        val chart = binding.buienradarChart

        // Chart styling and formatting
        chart.axisRight.isEnabled = false
        var description = Description()
        description.textColor = textColour
        description.text = ""
        chart.description = description
        chart.setNoDataText(getString(R.string.menu_buienradar_chart_loading))
        chart.setNoDataTextColor(textColour)
        chart.setDrawGridBackground(false)
        chart.xAxis.setDrawGridLines(false)
        chart.axisLeft.setDrawGridLines(false)
        chart.xAxis.position = XAxis.XAxisPosition.BOTTOM
        chart.xAxis.textColor = textColour
        chart.legend.textColor = textColour
        chart.axisLeft.textColor = textColour
        chart.setBackgroundColor(background)
        chart.invalidate()

        if (latitude == null || longitude == null) {
            chart.setNoDataText(getString(R.string.menu_buienradar_chart_no_location))
            chart.invalidate()
        }
        else if (latitude!!.toFloat() > 54.8f ||
            latitude!!.toFloat() < 49.5f ||
            longitude!!.toFloat() > 8.0f ||
            longitude!!.toFloat() < 1.0f) {
            chart.setNoDataText(getString(R.string.menu_buienradar_chart_outside_of_range) + ": " +
                    "lat=%.2f, lon=%.2f".format(latitude, longitude))
            chart.invalidate()
        }
        else {
            RetrieveWebPage().execute(getURL())
        }
    }

    internal inner class RetrieveWebPage : AsyncTask<String, Void, Document>() {

        // Retrieves the data from the URL using JSoup (async)
        override fun doInBackground(vararg urls: String): Document? {
            try {
                return Jsoup.connect(urls[0]).get()
            } catch (e: Exception) {
                return null
            }
        }

        // When complete: parses the result
        override fun onPostExecute(htmlDocument: Document?) {
            val chart = binding.buienradarChart
            if (htmlDocument == null || htmlDocument.text() == "") {
                chart.setNoDataText(getString(R.string.menu_buienradar_chart_error) + ": " +
                        "lat=%.2f, lon=%.2f".format(latitude, longitude))
                chart.invalidate()
                return
            }

            // The data
            val precipitations: ArrayList<BarEntry> = ArrayList()
            val times: ArrayList<String> = ArrayList()
            val buienradarData = htmlDocument.text().split(" ")
            var totalValue = 0f
            var maxPrecipitation = 0f
            buienradarData.forEachIndexed { index, item ->
                val splitted = item.split("|")

                // Conversion formula see https://www.buienradar.nl/overbuienradar/gratis-weerdata
                val value = splitted[0].toFloat()
                val precipitation = 10f.pow((value - 109) / 32)
                precipitations.add(BarEntry(index.toFloat(), precipitation))
                totalValue += value
                maxPrecipitation = max(maxPrecipitation, precipitation)

                val time = splitted[1]
                times.add(time)
            }

            if (totalValue > 0f) {
                val dataSet = BarDataSet(precipitations, getString(R.string.menu_buienradar_chart_unit))
                dataSet.axisDependency = YAxis.AxisDependency.LEFT
                dataSet.setDrawValues(false)
                val colors = IntArray(1); colors[0] = R.color.colorPrimary
                dataSet.setColors(colors, context)
                chart.data = BarData(dataSet)
            }
            chart.setNoDataText(getString(R.string.menu_buienradar_chart_empty) + ": " +
                    "lat=%.2f, lon=%.2f".format(latitude, longitude))

            // The labels on the x-axis
            val formatter: ValueFormatter =
                object : ValueFormatter() {
                    override fun getAxisLabel(value: Float, axis: AxisBase): String {
                        return times[value.toInt()]
                    }
                }
            chart.xAxis.granularity = 1f
            chart.xAxis.valueFormatter = formatter

            // The y-axis scale
            chart.axisLeft.axisMinimum = 0f
            chart.axisLeft.axisMaximum = max(8.0f, maxPrecipitation)

            // Final update
            chart.invalidate()
        }
    }
}