package foss.cnugteren.nlweer.ui.fragments

import android.content.res.Resources
import android.os.AsyncTask
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import androidx.fragment.app.Fragment
import androidx.preference.PreferenceManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import foss.cnugteren.nlweer.MainActivity
import foss.cnugteren.nlweer.R
import foss.cnugteren.nlweer.databinding.FragmentKnmiSixdayforecastBinding
import foss.cnugteren.nlweer.databinding.FragmentKnmiTextBinding
import org.jsoup.Jsoup
import org.jsoup.nodes.Document

class KnmiSixDayForecastFragment : Fragment() {

    private var _binding: FragmentKnmiSixdayforecastBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

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

        val widthPx = Resources.getSystem().displayMetrics.widthPixels
        val density = Resources.getSystem().displayMetrics.density
        val usablePixels = widthPx / density;

        webView.loadData(
            """
            <html>
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
                <body>
                    <table>
                      <colgroup>
                        <col style="min-width:110px" span="6" />
                      </colgroup>
                      <tr>
                        <td>Wo</td>
                        <td>Do</td>
                        <td>Vr</td>
                      </tr>
                      <tr>
                        <td>21-02-2024</td>
                        <td>22-02-2024</td>
                        <td>23-02-2024</td>
                      </tr>
                      <tr>
                        <td><img alt="" src="https://cdn.knmi.nl/assets/forecast/2-druppels-regen-0ff4b4be825bd046a86a22357884275f87438050fd9793d6ee9180cdc70a25e1.svg" width="60px"/></td>
                        <td><img alt="" src="https://cdn.knmi.nl/assets/forecast/Regen-5cdc504cb0cc00fd9f218c42b88ea0b3ba00d26240de3d4aeacef0f37d5e66ab.svg" width="60px"/></td>
                        <td><img alt="" src="https://cdn.knmi.nl/assets/forecast/2-druppels-regen-dag-01dce4fbd163e0f2ec5b3cfd234895a96595ea7a4dcf9c0d04917ec11874037e.svg" width="60px"/></td>
                      </tr>
                      <tr>
                        <td>Max.</td>
                        <td>Max.</td>
                        <td>Max.</td>
                      </tr>
                      <tr>
                        <td>10°</td>
                        <td>13/15°</td>
                        <td>9/10°</td>
                      </tr>
                      <tr>
                        <td>Min.</td>
                        <td>Min.</td>
                        <td>Min.</td>
                      </tr>                      
                      <tr>
                        <td>8°</td>
                        <td>8/9°</td>
                        <td>3/4°</td>
                      </tr>
                      <tr>
                        <td>Neerslag</td>
                        <td>Neerslag</td>
                        <td>Neerslag</td>
                      </tr>                      
                      <tr>
                        <td>5/10mm</td>
                        <td>10/20mm</td>
                        <td>1/5mm</td>
                      </tr>
                      <tr>
                        <td>Neerslagkans</td>
                        <td>Neerslagkans</td>
                        <td>Neerslagkans</td>
                      </tr>                                            
                      <tr>
                        <td>90%</td>
                        <td>100%</td>
                        <td>70%</td>
                      </tr>
                      <tr>
                        <td>Zonneschijn</td>
                        <td>Zonneschijn</td>
                        <td>Zonneschijn</td>
                      </tr>                      
                      <tr>
                        <td>10%</td>
                        <td>10%</td>
                        <td>30%</td>
                      </tr>
                      <tr>
                        <td>Windkracht</td>
                        <td>Windkracht</td>
                        <td>Windkracht</td>
                      </tr>
                      <tr>
                        <td>Z 4</td>
                        <td>Z 5</td>
                        <td>ZW 5</td>
                      </tr>
                      <tr>
                        <td></td>
                        <td></td>
                        <td></td>
                        <td></td>
                        <td></td>
                        <td></td>
                      </tr>
                      <tr>
                        <td></td>
                        <td></td>
                        <td></td>
                        <td></td>
                        <td></td>
                        <td></td>
                      </tr>
                      <tr>
                        <td></td>
                        <td></td>
                        <td></td>
                        <td></td>
                        <td></td>
                        <td></td>
                      </tr>                                            
                      <tr>
                        <td>Za</td>
                        <td>Zo</td>
                        <td>Ma</td>
                      </tr>
                      <tr>
                        <td>24-02-2024</td>
                        <td>25-02-2024</td>
                        <td>26-02-2024</td>
                      </tr>
                      <tr>
                        <td><img alt="" src="https://cdn.knmi.nl/assets/forecast/2-druppels-regen-0ff4b4be825bd046a86a22357884275f87438050fd9793d6ee9180cdc70a25e1.svg" width="60px"/></td>
                        <td><img alt="" src="https://cdn.knmi.nl/assets/forecast/2-druppels-regen-dag-01dce4fbd163e0f2ec5b3cfd234895a96595ea7a4dcf9c0d04917ec11874037e.svg" width="60px"/></td>
                        <td><img alt="" src="https://cdn.knmi.nl/assets/forecast/2-druppels-regen-dag-01dce4fbd163e0f2ec5b3cfd234895a96595ea7a4dcf9c0d04917ec11874037e.svg" width="60px"/></td>
                      </tr>
                      <tr>
                        <td>Max.</td>
                        <td>Max.</td>
                        <td>Max.</td>
                      </tr>
                      <tr>
                        <td>8/9°</td>
                        <td>8/10°</td>
                        <td>8/11°</td>
                      </tr>
                      <tr>
                        <td>Min.</td>
                        <td>Min.</td>
                        <td>Min.</td>
                      </tr>                      
                      <tr>
                        <td>2/3°</td>
                        <td>1/2°</td>
                        <td>1/5°</td>
                      </tr>
                      <tr>
                        <td>Neerslag</td>
                        <td>Neerslag</td>
                        <td>Neerslag</td>
                      </tr>                      
                      <tr>
                        <td>0/3mm</td>
                        <td>0/5mm</td>
                        <td>0/5mm</td>
                      </tr>
                      <tr>
                        <td>Neerslagkans</td>
                        <td>Neerslagkans</td>
                        <td>Neerslagkans</td>
                      </tr>                                            
                      <tr>
                        <td>50%</td>
                        <td>60%</td>
                        <td>60%</td>
                      </tr>
                      <tr>
                        <td>Zonneschijn</td>
                        <td>Zonneschijn</td>
                        <td>Zonneschijn</td>
                      </tr>                      
                      <tr>
                        <td>20%</td>
                        <td>30%</td>
                        <td>30%</td>
                      </tr>
                      <tr>
                        <td>Windkracht</td>
                        <td>Windkracht</td>
                        <td>Windkracht</td>
                      </tr>
                      <tr>
                        <td>ZW 4</td>
                        <td>Z 4</td>
                        <td>NO 3</td>
                      </tr>

                    </table>
                    <p>""".trimIndent() + getString(R.string.menu_knmi_text_source) + """</p>
                </body>
            </html>
        """.trimIndent(),
            "text/html", "UTF-8"
        )
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
            val webView = binding.webView
            if (htmlDocument == null) {
                webView.loadData(getString(R.string.menu_knmi_text_failed), "text/html", "utf-8")
                return
            }

            val column = arrayOfNulls<String>(15)
            val table = htmlDocument.select("div.weather-map__table-wrp")
            table.forEach { element ->
                element.select("li").forEach { column ->
                    column.select("span.weather-map__table-cell").forEachIndexed { index, item ->
                    }
                }
            }
        }
    }
}