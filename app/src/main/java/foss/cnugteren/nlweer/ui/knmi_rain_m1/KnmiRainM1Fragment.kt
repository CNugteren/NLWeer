package foss.cnugteren.nlweer.ui.knmi_rain_m1

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import foss.cnugteren.nlweer.R

class KnmiRainM1Fragment : Fragment() {

    private lateinit var homeViewModel: KnmiRainM1ViewModel
    private lateinit var gifView: WebView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        homeViewModel =
            ViewModelProviders.of(this).get(KnmiRainM1ViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_knmi_rain_m1, container, false)

        gifView = root.findViewById(R.id.gif_view) as WebView
        gifView.loadUrl("https://cdn.knmi.nl/knmi/map/general/weather-map.gif")

        return root
    }
}