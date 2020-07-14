package foss.cnugteren.nlweer.ui.fragments

class KnmiVisibilityFragment : BaseFragment() {

    override fun getURL(): String {
        return "https://cdn.knmi.nl/knmi/map/page/weer/actueel-weer/zicht.png"
    }

    override fun imageWidth(): Int { return 569 }
    override fun imageHeight(): Int { return 622 }
    override fun coordinates(): Array<Float> { return arrayOf(50.70f, 2.95f, 53.65f, 7.30f, -0.01f, 0.00f) }
}