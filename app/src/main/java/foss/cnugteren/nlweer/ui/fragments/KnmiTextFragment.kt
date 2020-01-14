package foss.cnugteren.nlweer.ui.fragments

import org.jsoup.Jsoup

class KnmiTextFragment : BaseFragment() {

    override fun getURL(): String {
        return ""
    }

    // TODO: Fill in
    override fun loadPage() {
        Jsoup.connect("https://www.google.co.in/search?q=this+is+a+test").get().run {
            //2. Parses and scrapes the HTML response
            select("div.rc").forEachIndexed { index, element ->
                val titleAnchor = element.select("h3 a")
                val title = titleAnchor.text()
                val url = titleAnchor.attr("href")
                //3. Dumping Search Index, Title and URL on the stdout.
                println("$index. $title ($url)")
            }
        }
    }

    // These values don't matter for this text fragment
    override fun imageWidth(): Int { return 1 }
    override fun imageHeight(): Int { return 1 }
    override fun coordinates(): Array<Float> { return arrayOf(0.0f, 0.0f, 1.0f, 1.0f, 0.0f, 0.0f) }
}