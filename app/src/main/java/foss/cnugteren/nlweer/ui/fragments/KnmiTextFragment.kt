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
    private lateinit var headers: Array<String>
    private lateinit var contents: Array<String>

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
        headers = arrayOf("Vandaag & Morgen", "Vooruitzichten", "Vooruitzichten lange termijn")
        contents = arrayOf("", "", "")

        // Retrieves the data from the URL using JSoup
        val htmlDocument = RetrieveWebPage().execute(getURL()).get()
        htmlDocument?.run {
            select("div.columns.filled-main-content").forEachIndexed { index, group ->

                // Parse the data (0): the main weather report
                if (index == 0) {
                    select("div.weather__text.media__body").forEach { element ->
                        element.select("p").forEach { paragraph ->
                            contents[0] += paragraph.text() + "\n\n"
                        }
                    }
                }

                // Parse the data (1): the mid term report
                if (index == 1) {
                    group.select("div.col-sm-12.col-md-7").forEach { element ->
                        element.select("p").forEach { paragraph ->
                            contents[1] += paragraph.text() + "\n\n"
                        }
                    }
                }

                // Parse the data (2): the long term report
                if (index == 3) {
                    group.select("div.col-sm-12.col-md-7").forEach { element ->
                        element.select("p").forEach { paragraph ->
                            contents[2] += paragraph.text() + "\n\n"
                        }
                    }
                }
            }
        }

        // Displays the found data
        root.findViewById<TextView>(R.id.textViewHeader0).text = headers[0]
        root.findViewById<TextView>(R.id.textViewContent0).text = contents[0].trimEnd()
        root.findViewById<TextView>(R.id.textViewHeader1).text = headers[1]
        root.findViewById<TextView>(R.id.textViewContent1).text = contents[1].trimEnd()
        root.findViewById<TextView>(R.id.textViewHeader2).text = headers[2]
        root.findViewById<TextView>(R.id.textViewContent2).text = contents[2].trimEnd()
    }

    internal inner class RetrieveWebPage : AsyncTask<String, Void, Document>() {
        override fun doInBackground(vararg urls: String): Document? {
            try {
                return Jsoup.connect(urls[0]).get()
            } catch (e: Exception) {
                return null
            }
        }
        override fun onPostExecute(htmlDocument: Document) { }
    }

}