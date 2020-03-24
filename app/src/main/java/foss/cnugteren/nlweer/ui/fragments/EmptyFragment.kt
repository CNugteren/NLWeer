package foss.cnugteren.nlweer.ui.fragments

class EmptyFragment : BaseFragment() {
    override fun getURL(): String {
        return ""
    }

    override fun imageWidth(): Int { return 10 }
    override fun imageHeight(): Int { return 10 }
    override fun coordinates(): Array<Float> { return arrayOf(0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f) }
}
