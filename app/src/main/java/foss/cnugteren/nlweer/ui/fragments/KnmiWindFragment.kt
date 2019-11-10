package foss.cnugteren.nlweer.ui.fragments

class KnmiWindFragment : BaseFragment() {

    override fun getURL(): String {
        return "https://cdn.knmi.nl/knmi/map/page/weer/actueel-weer/windkracht.png"
    }

    override fun imageWidth(): Int { return 569 }
    override fun imageHeight(): Int { return 622 }
    override fun coordinates(): Array<Float> { return arrayOf(50.70f, 2.70f, 53.67f, 7.57f) }
}