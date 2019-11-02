package foss.cnugteren.nlweer.ui.fragments

class KnmiTonightFragment : BaseFragment() {

    override fun getURL(): String {
        return "https://cdn.knmi.nl/knmi/map/current/weather/forecast/kaart_verwachtingen_Morgen_nacht.gif"
    }
}