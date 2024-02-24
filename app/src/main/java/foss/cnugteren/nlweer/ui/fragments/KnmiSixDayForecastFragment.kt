package foss.cnugteren.nlweer.ui.fragments

import android.content.res.Resources
import android.os.AsyncTask
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import androidx.core.view.marginEnd
import androidx.core.view.marginStart
import androidx.fragment.app.Fragment
import androidx.preference.PreferenceManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import foss.cnugteren.nlweer.MainActivity
import foss.cnugteren.nlweer.R
import foss.cnugteren.nlweer.databinding.FragmentKnmiSixdayforecastBinding
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.time.times

class KnmiSixDayForecastFragment : Fragment() {

    private var _binding: FragmentKnmiSixdayforecastBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    // Width in pixels of column containing KNMI weather data
    private val columnWidth get() = 105

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentKnmiSixdayforecastBinding.inflate(inflater, container, false)

        // Pull down to refresh the page
        val root = binding.root
        val pullToRefresh = root.findViewById<SwipeRefreshLayout>(R.id.pullToRefresh)
        pullToRefresh.setOnRefreshListener(SwipeRefreshLayout.OnRefreshListener {
            refreshPage()
            pullToRefresh.isRefreshing = false
        })

        // Do display floating navigation buttons
        val activity = this.activity as MainActivity
        activity.toggleNavigationButtons(true)

        // The web-viewer for the content
        val webView = binding.webView
        webView.settings.javaScriptEnabled = true

        loadPage()

        return root
    }

    private fun getURL(): String {
        return "https://www.knmi.nl/nederland-nu/weer/verwachtingen"
    }

    private fun refreshPage() {
        val webView = binding.webView
        webView.clearCache(false)
        loadPage()
    }

    private fun loadPage() {
        val webView = binding.webView
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(webView.context)
        val darkMode = sharedPreferences.getString("dark_mode", "dark_mode_no")
        var backgroundColor = "white"
        var textColor = "black"
        if (darkMode == "dark_mode_yes") {
            backgroundColor = "rgb(48, 48, 48)" // Android dark mode color
            textColor = "rgb(193, 193, 193)" // Android dark mode color
        }

        RetrieveWebPage().execute(getURL())
    }

    private fun CalculateColumnsPerRow(webView: WebView): Int {
        val widthPx = Resources.getSystem().displayMetrics.widthPixels
        val density = Resources.getSystem().displayMetrics.density
        val effectiveWidth = floor((widthPx / density).toDouble());
        val usableWidth = effectiveWidth - (webView.marginStart + webView.marginEnd) / density
        return floor(usableWidth / columnWidth).toInt()
    }

    internal inner class RetrieveWebPage : AsyncTask<String, Void, Document>() {

        // Retrieves the data from the URL using JSoup (async)
        @Deprecated("Deprecated in Java")
        override fun doInBackground(vararg urls: String): Document? {
            try {
                return Jsoup.connect(urls[0]).get()
            } catch (e: Exception) {
                return null
            }
        }

        // When complete: parses the result
        @Deprecated("Deprecated in Java")
        override fun onPostExecute(htmlDocument: Document?) {
            val webView = binding.webView
            if (htmlDocument == null) {
                webView.loadData(getString(R.string.menu_knmi_text_failed), "text/html", "utf-8")
                return
            }

            val tableWrapperElement = htmlDocument.select("div.weather-map__table-wrp").firstOrNull()
            if (tableWrapperElement == null){
                webView.loadData(getString(R.string.menu_knmi_text_failed), "text/html", "utf-8")
                return
            }

            val tableData = getTableData(tableWrapperElement)
            val columnsPerRow = CalculateColumnsPerRow(webView)
            val htmlTable = getHtmlTable(tableData, columnsPerRow)
            val htmlPageToShow = getHtmlPageWithTable(htmlTable)
            webView.loadData(htmlPageToShow, "text/html", "UTF-8")
        }

        private fun getHtmlTable(tableData: Array<Array<String>>, columnsPerTable: Int) : String {
            val numberOfTables = ceil((tableData.size / columnsPerTable).toDouble()).toInt()
            val numberOfRows = tableData[0].size

            var htmlTable = ""
            var totalNumberOfColumns = tableData.size
            for (i in 0..<numberOfTables) {
                htmlTable += """
                                    <table>
                                        <colgroup>
                                            <col style="min-width:""".trimIndent() + columnWidth + """px" span="6" />
                                          </colgroup>
                                """.trimMargin()

                for (row in 0..<numberOfRows) {
                    htmlTable += """<tr>"""
                    var j = 0
                    while (i * columnsPerTable + j < totalNumberOfColumns) {
                        var content = tableData[j][row]
                        if (content.endsWith(".svg")) {
                            content = """<img alt="" src="""" + content + """" width="60px"/>"""
                        }
                        htmlTable += """<td>""" + content + """</td>"""

                        j++
                    }
                    htmlTable += """</tr>"""
                }

                htmlTable += "</table>"
            }

            return htmlTable
        }

        // Get the weather data per day of the week
        private fun getTableData(tableWrapperElement: Element) : Array<Array<String>> {
            val weatherPerDay = tableWrapperElement.select("li")
            val tableData = Array<Array<String>>(weatherPerDay.size, { Array<String>(15, {""}) })
            weatherPerDay.forEachIndexed { colIndex, column ->
                var rowIndex = 0

                val dayOfTheWeek = column.selectFirst("strong.weather-map__table-cell")
                    ?.text()
                if (dayOfTheWeek != null) {
                    tableData[colIndex][rowIndex] = dayOfTheWeek
                    rowIndex++
                }

                column.select("span.weather-map__table-cell").forEach { rowItem ->
                    // If cell contains image, get the src link
                    val imageItem = rowItem.selectFirst("img")
                    if (imageItem != null) {
                        tableData[colIndex][rowIndex] = imageItem.attr("src")
                        rowIndex++
                    }
                    else {
                        // Item contains just text; split into header and data, if applicable
                        rowItem.text().split(' ', ignoreCase =  false, limit =  2).forEach { item ->
                            tableData[colIndex][rowIndex] = item
                            rowIndex++
                        }
                    }
                }
            }

            return tableData
        }

        private fun getHtmlPageWithTable(table: String) : String {
            val webView = binding.webView
            val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(webView.context)
            val darkMode = sharedPreferences.getString("dark_mode", "dark_mode_no")
            var backgroundColor = "white"
            var textColor = "black"
            if (darkMode == "dark_mode_yes") {
                backgroundColor = "rgb(48, 48, 48)" // Android dark mode color
                textColor = "rgb(193, 193, 193)" // Android dark mode color
            }

            return """<html>
                <head>
                    <style type='text/css'>
                        body {
                          font-size: 14px;
                          color:""".trimIndent() + textColor + """;
                          background-color:""".trimIndent() + backgroundColor + """;
                        }
                        tr:nth-child(2n+3) {
                          height: 30px;
                          vertical-align: text-top;
                          color:""".trimIndent() + textColor + """;
                        }
                        tr:nth-child(1) {
                          font-weight: bold;
                          color:""".trimIndent() + textColor + """;                          
                        }
                        tr:nth-child(19) {
                          font-weight: bold;
                          color:""".trimIndent() + textColor + """;                          
                        }
                        tr:nth-child(2n) {
                          color: grey;
                        }
                        tr {
                          font-size: 14px;
                        }
                    </style>
                </head>
                <body>""" + table + """</body>"""
        }
    }
}