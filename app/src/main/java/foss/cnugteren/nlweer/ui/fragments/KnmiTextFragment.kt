package foss.cnugteren.nlweer.ui.fragments

import android.os.AsyncTask
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import foss.cnugteren.nlweer.MainActivity
import foss.cnugteren.nlweer.R
import foss.cnugteren.nlweer.databinding.FragmentKnmiTextBinding
import org.jsoup.Jsoup
import org.jsoup.nodes.Document


class KnmiTextFragment : Fragment() {

    private var _binding: FragmentKnmiTextBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentKnmiTextBinding.inflate(inflater, container, false)

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

        loadPage()

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun getURL(): String {
        return "https://www.knmi.nl/nederland-nu/weer/verwachtingen"
    }

    fun refreshPage() {
        loadPage()
    }

    private fun loadPage() {
        val root = binding.root
        val headers = arrayOf("Vandaag & Morgen", "Vooruitzichten", "Vooruitzichten lange termijn")
        root.findViewById<TextView>(R.id.textViewHeader0).text = headers[0]
        root.findViewById<TextView>(R.id.textViewHeader1).text = headers[1]
        root.findViewById<TextView>(R.id.textViewHeader2).text = headers[2]
        root.findViewById<TextView>(R.id.textViewContent0).text = getString(R.string.menu_knmi_text_loading)
        root.findViewById<TextView>(R.id.textViewContent1).text = getString(R.string.menu_knmi_text_loading)
        root.findViewById<TextView>(R.id.textViewContent2).text = getString(R.string.menu_knmi_text_loading)
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
            // It may be that the user has already moved on to the next fragment
            if (_binding == null) {
                return
            }

            val root = binding.root
            if (htmlDocument == null) {
                root.findViewById<TextView>(R.id.textViewContent0).text = getString(R.string.menu_knmi_text_failed)
                root.findViewById<TextView>(R.id.textViewContent1).text = getString(R.string.menu_knmi_text_failed)
                root.findViewById<TextView>(R.id.textViewContent2).text = getString(R.string.menu_knmi_text_failed)
                return
            }
            val contents = arrayOf("", "", "", "")

            htmlDocument.run {
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

                    // The long term report
                    if (index == 4) {
                        group.select("div.col-sm-12.col-md-7").forEach { element ->
                            element.select("p").forEach { paragraph ->
                                contents[2] += paragraph.text() + "\n\n"
                            }
                            element.select("span.meta").forEach { paragraph ->
                                contents[2] += "Laatste update: " + paragraph.text() + "\n\n"
                            }
                        }
                    }
                }
            }

            // Displays the found data
            root.findViewById<TextView>(R.id.textViewContent0).text = contents[0].trimEnd()
            root.findViewById<TextView>(R.id.textViewContent1).text = contents[1].trimEnd()
            root.findViewById<TextView>(R.id.textViewContent2).text = contents[2].trimEnd() + "\n\n"
        }
    }
}
