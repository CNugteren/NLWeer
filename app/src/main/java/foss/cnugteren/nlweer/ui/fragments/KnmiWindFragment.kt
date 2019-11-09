package foss.cnugteren.nlweer.ui.fragments

class KnmiWindFragment : BaseFragment() {

    override fun getURL(): String {
        return "https://cdn.knmi.nl/knmi/map/page/weer/actueel-weer/windkracht.png"
    }

    override fun imageWidth(): Int { return 569 }
    override fun imageHeight(): Int { return 622 }
}