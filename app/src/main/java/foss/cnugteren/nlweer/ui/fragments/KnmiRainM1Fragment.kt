package foss.cnugteren.nlweer.ui.fragments

class KnmiRainM1Fragment : BaseFragment() {

    override fun getURL(): String {
        return "https://cdn.knmi.nl/knmi/map/general/weather-map.gif"
    }

    override fun imageWidth(): Int { return 425 }
    override fun imageHeight(): Int { return 445 }
    override fun coordinates(): Array<Float> { return arrayOf(50.39f, 1.90f, 53.85f, 7.45f) }
}