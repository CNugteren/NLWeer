package foss.cnugteren.nlweer.ui.fragments

import android.content.res.Resources
import android.graphics.Color
import android.os.AsyncTask
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.URLUtil
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
import java.lang.IllegalArgumentException
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.min

class KnmiSixDayForecastFragment : Fragment() {

    private var _binding: FragmentKnmiSixdayforecastBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val knmiUrl get() = "https://www.knmi.nl"

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

        // The web-viewer for the content
        val webView = binding.webView
        webView.settings.javaScriptEnabled = true

        val backgroundColor = if ((requireActivity() as MainActivity).appIsInDarkMode) {
            androidx.core.content.ContextCompat.getColor(requireContext(), R.color.darkBackground)
        } else {
            Color.rgb(250, 250, 250)
        }
        root.setBackgroundColor(backgroundColor)
        webView.setBackgroundColor(backgroundColor)

        loadPage()

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
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
        val activity = this.activity as MainActivity
        webView.loadData(HtmlBuilder().buildHtmlPageWithLoadingMessage(activity.appIsInDarkMode), "text/html", "utf-8")
        RetrieveWebPage(activity).execute(getURL())
    }

    internal inner class RetrieveWebPage(activity: MainActivity) : AsyncTask<String, Void, Document>() {
        private var mainActivity: MainActivity = activity

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
            // It may be that the user has already moved on to the next fragment
            if (_binding == null) {
                return
            }
            val webView = binding.webView
            val htmlBuilder = HtmlBuilder()
            if (htmlDocument == null) {
                webView.loadData(htmlBuilder.buildHtmPageWithLoadingError(mainActivity.appIsInDarkMode), "text/html", "utf-8")
                return
            }

            val weatherForecastTable = htmlDocument.select("div.weather-forecast__table").firstOrNull()
            if (weatherForecastTable == null){
                webView.loadData(htmlBuilder.buildHtmPageWithLoadingError(mainActivity.appIsInDarkMode), "text/html", "utf-8")
                return
            }

            try {
                val tableData = getTableData(weatherForecastTable)
                val htmlPageToShow = htmlBuilder.buildHtmlPageWithTables(tableData, mainActivity.appIsInDarkMode)
                webView.loadData(htmlPageToShow, "text/html", "UTF-8")
            }
            catch (ex: Exception) {
                webView.loadData(htmlBuilder.buildHtmPageWithLoadingError(mainActivity.appIsInDarkMode), "text/html", "utf-8")
            }
        }

        // Get the weather data per day of the week
        private fun getTableData(weatherForecastTable: Element) : Array<Array<String>> {
            val weatherPerDay = weatherForecastTable.children()
            val weatherOfFirstDay = weatherPerDay.firstOrNull()?.select("article.weather-forecast__day")
            if (weatherOfFirstDay == null) {
                throw IllegalArgumentException("Forecast first day not found")
            }
            val numberOfMetrics  = weatherOfFirstDay.select("div.weather-forecast__metrics").firstOrNull()?.childrenSize()
            if (numberOfMetrics == null) {
                throw IllegalArgumentException("Forecast info cannot be found")
            }

            // Day of the week + date + weather icon + 2 rows for each property
            val numberOfRows = 2 * numberOfMetrics
            val tableData = Array<Array<String>>(weatherPerDay.size, { Array<String>(numberOfRows, {""}) })


            weatherPerDay.forEachIndexed { colIndex, column ->
                var rowIndex = 0

                val weatherMetrics = column.select("div.weather-forecast__metrics").firstOrNull()
                if (weatherMetrics == null) {
                    throw IllegalArgumentException("Forecast banner not found")
                }

                weatherMetrics.children().forEach { rowItem ->
                    // If cell is only an image, get the src link
                    if (rowItem.tagName() == "img") {
                        var source = rowItem.attr("src");
                        if (source.startsWith('/')) {
                            source = "$knmiUrl$source";
                        }
                        tableData[colIndex][rowIndex] = source;
                        rowIndex++
                    }
                    else {
                        // Item contains just text; split into header and data, if applicable
                        getCellHeaderAndValue(rowItem).forEach { item ->
                            tableData[colIndex][rowIndex] = item
                            rowIndex++
                        }
                    }
                }
            }

            return tableData
        }

        // Item contains just text; split into header and data, if applicable
        private fun getCellHeaderAndValue(rowItem: Element) : List<String> {
            return rowItem.text().split(' ', ignoreCase =  false, limit =  2)
        }
    }

    internal inner class HtmlBuilder {

        // Width in pixels of column containing KNMI weather data
        private val columnWidth get() = 110

        // Image size in pixels
        private val imageSize get() = 54

        // Font size to be used in HTML; in pixels
        private val fontSize get() = 14

        // Row height used for every other row for aesthetic reasons; in pixels
        private val paddedRowHeight get() = 24

        fun buildHtmlPageWithTables(tableData: Array<Array<String>>, appIsInDarkMode: Boolean) : String {
            val columnsPerTable = calculateColumnsPerTable(tableData)
            val tablesHtml = getHtmlTables(tableData, columnsPerTable)

            return getHtmlPageWithContent(tablesHtml, appIsInDarkMode)
        }

        private fun calculateColumnsPerTable(tableData: Array<Array<String>>): Int {
            val webView = binding.webView
            val widthPx = Resources.getSystem().displayMetrics.widthPixels
            val density = Resources.getSystem().displayMetrics.density
            val effectiveWidth = floor((widthPx / density).toDouble());
            val usableWidth = effectiveWidth - (webView.marginStart + webView.marginEnd) / density
            val columnsPerRow = floor(usableWidth / columnWidth).toInt()

            return min(columnsPerRow, tableData.size)
        }

        private fun getHtmlTables(tableData: Array<Array<String>>, columnsPerTable: Int) : String {
            val numberOfTables = ceil(tableData.size.toDouble() / columnsPerTable).toInt()
            val numberOfRowsPerTable = tableData[0].size
            var htmlTable = ""
            var totalNumberOfColumns = tableData.size

            for (tableNumber in 0..<numberOfTables) {
                htmlTable += """
                    <table>
                        <colgroup>
                            <col style="min-width:""".trimIndent() + columnWidth + """px" span="""" + totalNumberOfColumns + """" />
                        </colgroup>""".trimMargin()

                for (row in 0..<numberOfRowsPerTable) {
                    htmlTable += """<tr>"""
                    var column = tableNumber * columnsPerTable
                    // Loop until either all columns for the current table have been processed,
                    // or all columns have already been processed
                    val endColumn = min((tableNumber + 1 ) * columnsPerTable, totalNumberOfColumns)
                    while (column < endColumn) {
                        var content = tableData[column][row]
                        if (URLUtil.isValidUrl(content)) {
                            content = """<img alt="" src="""" + content + """" width="""" + imageSize + """px"/>"""
                        }
                        htmlTable += "<td>$content</td>"

                        column++
                    }
                    htmlTable += """</tr>"""
                }

                htmlTable += "</table>"
                htmlTable += "<p></p>"
                // Add additional spacing between tables
                if (tableNumber < numberOfTables - 1) {
                    htmlTable += """<br style="line-height: 10px">"""
                }
            }

            return htmlTable
        }

        fun buildHtmlPageWithLoadingMessage(appIsInDarkMode: Boolean) : String {
            val loadingMessageHtml = "<p>" + getString(R.string.menu_knmi_text_loading) + "</p>" + """<br style="line-height: 10px">"""
            return getHtmlPageWithContent(loadingMessageHtml, appIsInDarkMode)
        }

        fun buildHtmPageWithLoadingError(appIsInDarkMode: Boolean) : String {
            val errorMessageHtml = "<p>" + getString(R.string.menu_knmi_text_failed) + "</p>" + """<br style="line-height: 10px">"""
            return getHtmlPageWithContent(errorMessageHtml, appIsInDarkMode)
        }

        private fun getHtmlPageWithContent(content: String, appIsInDarkMode: Boolean) : String {
            var backgroundColor = "rgb(250, 250, 250)"
            var textColor = "black"
            if (appIsInDarkMode) {
            backgroundColor = "rgb(48, 48, 48)" // Android dark mode color (defined in colors.xml as darkBackground)
            textColor = "rgb(193, 193, 193)" // Android dark mode color
            }

            return """<html>
                <head>
                    <style type='text/css'>
                        body {
                          font-size: """.trimIndent() + fontSize + """px;
                          color:""".trimIndent() + textColor + """;
                          background-color:""".trimIndent() + backgroundColor + """;
                        }
                        tr {
                          font-size: """.trimIndent() + fontSize +  """px;
                        }    
                        tr:nth-child(1) {
                          font-weight: bold;
                          color:""".trimIndent() + textColor + """;                          
                        }
                        tr:nth-child(2n) {
                          color: grey;
                        }                                            
                        tr:nth-child(2n+3) {
                          height: """.trimIndent() + paddedRowHeight + """px;
                          vertical-align: text-top;
                          color:""".trimIndent() + textColor + """;
                        }
                    </style>
                </head>
                <body>""" + content + """
                    <p>""".trimIndent() + getString(R.string.menu_knmi_text_source) + """</p>
                    </body>""".trimIndent()
        }
    }
}
