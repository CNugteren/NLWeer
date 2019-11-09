package foss.cnugteren.nlweer.ui.fragments

class KnmiTomorrowFragment : BaseFragment() {

    override fun getURL(): String {
        return "https://cdn.knmi.nl/knmi/map/current/weather/forecast/kaart_verwachtingen_Morgen_dag.gif"
    }

    override fun imageWidth(): Int { return 425 }
    override fun imageHeight(): Int { return 467 }
}