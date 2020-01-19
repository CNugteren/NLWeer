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
        var header = "Waiting for data..,"
        var content = ""

        // Retrieves the data from the URL using JSoup
        val htmlDocument = RetrieveWebPage().execute(getURL()).get()
        if (htmlDocument == null) {
            header = "No data found"
            content = ""
        }
        else {
            htmlDocument.run {
                select("div.weather__text.media__body").forEach { element ->
                    element.select("p").forEachIndexed { index, paragraph ->
                        if (index == 0) {
                            header = paragraph.text()
                        } else {
                            content += paragraph.text() + "\n\n"
                        }
                    }
                }
            }
        }

        // Displays the found data
        val headerField = root.findViewById<TextView>(R.id.textViewHeader)
        headerField.text = header
        val contentField = root.findViewById<TextView>(R.id.textViewContent)
        contentField.text = content
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