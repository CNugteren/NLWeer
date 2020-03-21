package foss.cnugteren.nlweer.ui.fragments

class BuienradarHailFragment : BaseFragment() {

    override fun getURL(): String {
        return "https://image.buienradar.nl/2.0/image/animation/RadarMapHailNL?w=550&h=510"
    }

    override fun imageWidth(): Int { return 550 }
    override fun imageHeight(): Int { return 510 }
    override fun coordinates(): Array<Float> { return arrayOf(49.50f, 0.15f, 54.80f, 10.25f, 0.02f, -0.01f) }
}