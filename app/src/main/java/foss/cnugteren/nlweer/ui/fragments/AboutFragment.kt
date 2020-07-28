package foss.cnugteren.nlweer.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import foss.cnugteren.nlweer.BuildConfig
import foss.cnugteren.nlweer.MainActivity
import foss.cnugteren.nlweer.R

class AboutFragment: Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_about, container, false)

        // Don't display floating navigation buttons
        val activity = this.activity as MainActivity
        activity.toggleNavigationButtons(false)

        val versionCode = BuildConfig.VERSION_CODE
        val versionName = BuildConfig.VERSION_NAME
        val versionField = root.findViewById<TextView>(R.id.textViewVersion)
        versionField.text = versionName + " (build " + versionCode + ")"

        return root
    }
}