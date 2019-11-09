package foss.cnugteren.nlweer.ui.fragments

class KnmiRainM1Fragment : BaseFragment() {

    override fun getURL(): String {
        return "https://cdn.knmi.nl/knmi/map/general/weather-map.gif"
    }

    override fun imageWidth(): Int { return 425 }
    override fun imageHeight(): Int { return 445 }
}