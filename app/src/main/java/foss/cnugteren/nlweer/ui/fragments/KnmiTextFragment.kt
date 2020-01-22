package foss.cnugteren.nlweer.ui.fragments

import android.os.Bundle
import org.jsoup.Jsoup
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import foss.cnugteren.nlweer.R
import android.os.AsyncTask
import org.jsoup.nodes.Document


class KnmiTextFragment : Fragment() {

    private lateinit var root: View

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        root = inflater.inflate(R.layout.fragment_knmi_text, container, false)

        // Pull down to refresh the page
        val pullToRefresh = root.findViewById<SwipeRefreshLayout>(R.id.pullToRefresh)
        pullToRefresh.setOnRefreshListener(SwipeRefreshLayout.OnRefreshListener {
            refreshPage()
            pullToRefresh.isRefreshing = false
        })

        loadPage()

        return root
    }

    fun getURL(): String {
        return "https://www.knmi.nl/nederland-nu/weer/verwachtingen"
    }

    fun refreshPage() {
        loadPage()
    }

    fun loadPage() {
        val headers = arrayOf("Vandaag & Morgen", "Vooruitzichten", "", "Vooruitzichten lange termijn")
        root.findViewById<TextView>(R.id.textViewHeader0).text = headers[0]
        root.findViewById<TextView>(R.id.textViewHeader1).text = headers[1]
        root.findViewById<TextView>(R.id.textViewHeader3).text = headers[3]
        root.findViewById<TextView>(R.id.textViewContent0).text = getString(R.string.menu_knmi_text_loading)
        root.findViewById<TextView>(R.id.textViewContent1).text = getString(R.string.menu_knmi_text_loading)
        root.findViewById<TextView>(R.id.textViewContent3).text = getString(R.string.menu_knmi_text_loading)
        RetrieveWebPage().execute(getURL())
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
            if (htmlDocument == null) {
                root.findViewById<TextView>(R.id.textViewContent0).text = getString(R.string.menu_knmi_text_failed)
                root.findViewById<TextView>(R.id.textViewContent1).text = getString(R.string.menu_knmi_text_failed)
                root.findViewById<TextView>(R.id.textViewContent3).text = getString(R.string.menu_knmi_text_failed)
                return
            }
            var contents = arrayOf("", "", "", "")

            htmlDocument?.run {
                select("div.columns.filled-main-content").forEachIndexed { index, group ->

                    // The main weather report
                    if (index == 0) {
                        select("div.weather__text.media__body").forEach { element ->
                            element.select("p").forEach { paragraph ->
                                contents[0] += paragraph.text() + "\n\n"
                            }
                        }
                    }

                    // The mid term report
                    if (index == 1) {
                        group.select("div.col-sm-12.col-md-7").forEach { element ->
                            element.select("p").forEach { paragraph ->
                                contents[1] += paragraph.text() + "\n\n"
                            }
                        }
                    }

                    // The icons/numbers week overview
                    if (index == 2) {
                        group.select("div.weather-map__table-wrp").forEach { element ->
                            element.select("li").forEach { column ->
                                var tableData = ""
                                column.select("span.weather-map__table-cell").forEachIndexed { index, item ->
                                    if (index == 0) {
                                        tableData += "[" + item.text() + "] "
                                    }
                                    else {
                                        tableData += item.text() + " "
                                    }
                                }
                                contents[2] += tableData + "\n\n"
                            }
                        }
                    }

                    // The long term report
                    if (index == 3) {
                        group.select("div.col-sm-12.col-md-7").forEach { element ->
                            element.select("p").forEach { paragraph ->
                                contents[3] += paragraph.text() + "\n\n"
                            }
                        }
                    }
                }
            }

            // Displays the found data
            root.findViewById<TextView>(R.id.textViewContent0).text = contents[0].trimEnd()
            root.findViewById<TextView>(R.id.textViewContent1).text = contents[1].trimEnd()
            root.findViewById<TextView>(R.id.textViewContent2).text = contents[2].trimEnd()
            root.findViewById<TextView>(R.id.textViewContent3).text = contents[3].trimEnd() + "\n\n"
        }
    }
}