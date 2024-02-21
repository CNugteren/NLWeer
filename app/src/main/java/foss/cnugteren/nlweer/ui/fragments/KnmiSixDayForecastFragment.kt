package foss.cnugteren.nlweer.ui.fragments

import android.graphics.Color
import android.os.AsyncTask
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.preference.PreferenceManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import foss.cnugteren.nlweer.MainActivity
import foss.cnugteren.nlweer.R
import org.jsoup.Jsoup
import org.jsoup.nodes.Document

class WebClientKnmiSixDayForecast : WebViewClient() {
    // From https://stackoverflow.com/questions/14423981/android-webview-display-only-some-part-of-website

    override fun shouldOverrideUrlLoading(
        view: WebView,
        url: String
    ): Boolean {
        view.loadUrl(url)
        return true
    }

    override fun onPageFinished(view: WebView, url: String) {
        /*view.loadUrl("javascript:(function() {" +
                "document.getElementsByClassName('breadcrumb')[0].style.display='none';" +
                "document.getElementsByClassName('weather-small')[0].style.display='none';" +
                "document.getElementsByClassName('site-header')[0].style.display='none';" +
                "document.getElementsByClassName('site-footer')[0].style.display='none';" +
                "document.getElementsByClassName('columns')[0].style.display='none';" +
                "document.getElementsByClassName('columns')[1].style.display='none';" +
                "document.getElementsByClassName('columns')[3].style.display='none';" +
                "document.getElementsByClassName('columns')[4].style.display='none';" +
                "document.getElementsByClassName('morelinks')[0].style.display='none';" +
                "document.getElementsByClassName('banner-visual-wrp')[0].style.display='none';" +
                "}\n)()")*/

        /*val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(view.context)
        val darkMode = sharedPreferences.getString("dark_mode", "dark_mode_no")
        if (darkMode == "dark_mode_yes") {
            view.setBackgroundColor(Color.parseColor("#2e2e2e")); // matches Android's dark mode colours
            view.loadUrl(
                "javascript:document.body.style.setProperty(\"color\", \"white\");"
            );
        }*/
    }
}

class KnmiSixDayForecastFragment : Fragment() {

    private lateinit var webView: WebView

    private lateinit var root: View

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        root = inflater.inflate(R.layout.fragment_knmi_sixdayforecast, container, false)

        // Pull down to refresh the page
        val pullToRefresh = root.findViewById<SwipeRefreshLayout>(R.id.pullToRefresh)
        pullToRefresh.setOnRefreshListener(SwipeRefreshLayout.OnRefreshListener {
            refreshPage()
            pullToRefresh.isRefreshing = false
        })

        // Do display floating navigation buttons
        val activity = this.activity as MainActivity
        activity.toggleNavigationButtons(true)

        // The web-viewer for the content
        webView = root.findViewById(R.id.web_view) as WebView
        val webViewClientModified = WebClientKnmiSixDayForecast()
        webView.settings.javaScriptEnabled = true
        webView.webViewClient = webViewClientModified

        loadPage()

        return root
    }

    private fun getURL(): String {
        return "https://www.knmi.nl/nederland-nu/weer/verwachtingen"
    }

    private fun refreshPage() {
        webView.clearCache(false)
        loadPage()
    }

    private fun loadPage() {
        //webView.loadUrl(getURL())


        /*val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(webView.context)
        val darkMode = sharedPreferences.getString("dark_mode", "dark_mode_no")
        if (darkMode == "dark_mode_yes") {
            webView.setBackgroundColor(Color.parseColor("#2e2e2e")); // matches Android's dark mode colours
            webView.loadUrl(
                "javascript:document.body.style.setProperty(\"color\", \"white\");"
            );
        }*/
        //webView.isHorizontalScrollBarEnabled = true

        webView.loadData(
            """
            <html>
                <head>
                    <style type='text/css'>
                        html {
                           width: 100%;
                           height: 100%;
                        }
                        .center {
                          display: table-cell;
                          vertical-align: middle;
                        }
                        tr:nth-child(2n+3) {
                          height: 30px;
                          vertical-align: text-top;
                        }
                        tr:nth-child(1) {
                          font-weight: bold;
                        }
                        tr:nth-child(2) {
                          color: grey;
                        }
                        tr:nth-child(2n+2) {
                          color: grey;
                        }
                    </style>
                </head>
                <body>
                    <div class="center">
                    <table>
                      <colgroup>
                        <col style="min-width:120px" span="6" />
                      </colgroup>
                      <tr>
                        <td>Wo</td>
                        <td>Do</td>
                        <td>Vr</td>
                        <td>Za</td>
                        <td>Zo</td>
                        <td>Ma</td>
                      </tr>
                      <tr>
                        <td>21-02-2024</td>
                        <td>22-02-2024</td>
                        <td>23-02-2024</td>
                        <td>24-02-2024</td>
                        <td>25-02-2024</td>
                        <td>26-02-2024</td>
                      </tr>
                      <tr>
                        <td><img alt="" src="https://cdn.knmi.nl/assets/forecast/2-druppels-regen-0ff4b4be825bd046a86a22357884275f87438050fd9793d6ee9180cdc70a25e1.svg" width="60px"/></td>
                        <td><img alt="" src="https://cdn.knmi.nl/assets/forecast/Regen-5cdc504cb0cc00fd9f218c42b88ea0b3ba00d26240de3d4aeacef0f37d5e66ab.svg" width="60px"/></td>
                        <td><img alt="" src="https://cdn.knmi.nl/assets/forecast/2-druppels-regen-dag-01dce4fbd163e0f2ec5b3cfd234895a96595ea7a4dcf9c0d04917ec11874037e.svg" width="60px"/></td>
                        <td><img alt="" src="https://cdn.knmi.nl/assets/forecast/2-druppels-regen-0ff4b4be825bd046a86a22357884275f87438050fd9793d6ee9180cdc70a25e1.svg" width="60px"/></td>
                        <td><img alt="" src="https://cdn.knmi.nl/assets/forecast/2-druppels-regen-dag-01dce4fbd163e0f2ec5b3cfd234895a96595ea7a4dcf9c0d04917ec11874037e.svg" width="60px"/></td>
                        <td><img alt="" src="https://cdn.knmi.nl/assets/forecast/2-druppels-regen-dag-01dce4fbd163e0f2ec5b3cfd234895a96595ea7a4dcf9c0d04917ec11874037e.svg" width="60px"/></td>
                      </tr>
                      <tr>
                        <td>Max.</td>
                        <td>Max.</td>
                        <td>Max.</td>
                        <td>Max.</td>
                        <td>Max.</td>
                        <td>Max.</td>
                      </tr>
                      <tr>
                        <td>10°</td>
                        <td>13/15°</td>
                        <td>9/10°</td>
                        <td>8/9°</td>
                        <td>8/10°</td>
                        <td>8/11°</td>
                      </tr>
                      <tr>
                        <td>Min.</td>
                        <td>Min.</td>
                        <td>Min.</td>
                        <td>Min.</td>
                        <td>Min.</td>
                        <td>Min.</td>
                      </tr>                      
                      <tr>
                        <td>8°</td>
                        <td>8/9°</td>
                        <td>3/4°</td>
                        <td>2/3°</td>
                        <td>1/2°</td>
                        <td>1/5°</td>
                      </tr>
                      <tr>
                        <td>Neerslag</td>
                        <td>Neerslag</td>
                        <td>Neerslag</td>
                        <td>Neerslag</td>
                        <td>Neerslag</td>
                        <td>Neerslag</td>
                      </tr>                      
                      <tr>
                        <td>5/10mm</td>
                        <td>10/20mm</td>
                        <td>1/5mm</td>
                        <td>0/3mm</td>
                        <td>0/5mm</td>
                        <td>0/5mm</td>
                      </tr>
                      <tr>
                        <td>Neerslagkans</td>
                        <td>Neerslagkans</td>
                        <td>Neerslagkans</td>
                        <td>Neerslagkans</td>
                        <td>Neerslagkans</td>
                        <td>Neerslagkans</td>
                      </tr>                                            
                      <tr>
                        <td>90%</td>
                        <td>100%</td>
                        <td>70%</td>
                        <td>50%</td>
                        <td>60%</td>
                        <td>60%</td>
                      </tr>
                      <tr>
                        <td>Zonneschijn</td>
                        <td>Zonneschijn</td>
                        <td>Zonneschijn</td>
                        <td>Zonneschijn</td>
                        <td>Zonneschijn</td>
                        <td>Zonneschijn</td>
                      </tr>                      
                      <tr>
                        <td>10%</td>
                        <td>10%</td>
                        <td>30%</td>
                        <td>20%</td>
                        <td>30%</td>
                        <td>30%</td>
                      </tr>
                      <tr>
                        <td>Windkracht</td>
                        <td>Windkracht</td>
                        <td>Windkracht</td>
                        <td>Windkracht</td>
                        <td>Windkracht</td>
                        <td>Windkracht</td>
                      </tr>
                      <tr>
                        <td>Z 4</td>
                        <td>Z 5</td>
                        <td>ZW 5</td>
                        <td>ZW 4</td>
                        <td>Z 4</td>
                        <td>NO 3</td>
                      </tr>
                    </table></div>
                </body>
            </html>
        """.trimIndent(),
            "text/html", "UTF-8"
        )

        //RetrieveWebPage().execute(getURL())
    }

    /*internal inner class RetrieveWebPage : AsyncTask<String, Void, Document>() {

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
                webView.loadData(getString(R.string.menu_knmi_text_failed), "text/html", "utf-8")
                return
            }

            val table = htmlDocument.select("div.weather-map__table-wrp")
             htmlDocument.head()
            webView.loadData("""
                <html>
                    <head>
                        <link rel="stylesheet" media="all" href="https://cdn.knmi.nl/assets/application-df01943aed6e5fbce0fd7b0f592c8077e84edc232ec9a6376f1e2c3cc73696c3.css" data-turbolinks-track="true" />
                    </head>
                <body>""".trimIndent() + table + """</body>
            </html>
            """.trimIndent(),
                "text/html", "UTF-8"
            )
        }
    }*/
}