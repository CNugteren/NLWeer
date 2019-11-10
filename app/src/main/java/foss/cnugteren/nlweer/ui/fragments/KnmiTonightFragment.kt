package foss.cnugteren.nlweer.ui.fragments

class KnmiTonightFragment : BaseFragment() {

    override fun getURL(): String {
        return "https://cdn.knmi.nl/knmi/map/current/weather/forecast/kaart_verwachtingen_Morgen_nacht.gif"
    }

    override fun imageWidth(): Int { return 425 }
    override fun imageHeight(): Int { return 467 }
    override fun coordinates(): Array<Float> { return arrayOf(50.70f, 3.00f, 53.67f, 7.57f) }
}