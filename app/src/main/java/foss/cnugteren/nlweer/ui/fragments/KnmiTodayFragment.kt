package foss.cnugteren.nlweer.ui.fragments

class KnmiTodayFragment : BaseFragment() {

    override fun getURL(): String {
        return "https://cdn.knmi.nl/knmi/map/current/weather/forecast/kaart_verwachtingen_Vandaag_dag.gif"
    }

    override fun imageWidth(): Int { return 425 }
    override fun imageHeight(): Int { return 467 }
    override fun coordinates(): Array<Float> { return arrayOf(50.70f, 3.10f, 53.65f, 7.55f, -0.01f, 0.00f) }
}