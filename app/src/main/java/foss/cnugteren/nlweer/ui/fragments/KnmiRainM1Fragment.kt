package foss.cnugteren.nlweer.ui.fragments

class KnmiRainM1Fragment : BaseFragment() {
    override val currentViewIndex = 0

    override fun getURL(): String {
        return "https://cdn.knmi.nl/knmi/map/general/weather-map.gif"
    }

    override fun imageWidth(): Int { return 425 }
    override fun imageHeight(): Int { return 445 }
    override fun coordinates(): Array<Float> { return arrayOf(50.60f, 1.85f, 54.05f, 7.20f, -0.10f, 0.09f) }
}