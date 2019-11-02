package foss.cnugteren.nlweer.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import androidx.fragment.app.Fragment
import foss.cnugteren.nlweer.R

open class BaseFragment : Fragment() {

    private lateinit var gifView: WebView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_knmi_rain_m1, container, false)

        gifView = root.findViewById(R.id.gif_view) as WebView
        gifView.loadUrl(getURL())
        gifView.settings.loadWithOverviewMode = true
        gifView.settings.useWideViewPort = true
        return root
    }

    open fun getURL(): String {
        return "" // Implemented in derived classes
    }

    fun refresh() {
        gifView.loadUrl(getURL())
    }
}