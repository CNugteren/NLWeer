package foss.cnugteren.nlweer.ui.fragments

class KnmiSunStrengthTomorrowFragment : BaseFragment() {

    override fun getURL(): String {
        return "https://cdn.knmi.nl/knmi/map/page/weer/waarschuwingen_verwachtingen/zonkracht/zonkracht_dag1.gif"
    }

    override fun imageWidth(): Int { return 425 }
    override fun imageHeight(): Int { return 457 }
    override fun coordinates(): Array<Float> { return arrayOf(50.70f, 3.10f, 53.65f, 7.55f, -0.01f, 0.00f) }
}