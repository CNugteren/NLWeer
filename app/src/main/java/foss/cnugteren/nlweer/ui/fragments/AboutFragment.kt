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
import foss.cnugteren.nlweer.databinding.FragmentAboutBinding

class AboutFragment: Fragment() {

    private var _binding: FragmentAboutBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAboutBinding.inflate(inflater, container, false)

        // Don't display floating navigation buttons
        val activity = this.activity as MainActivity
        activity.toggleNavigationButtons(false)

        val versionCode = BuildConfig.VERSION_CODE
        val versionName = BuildConfig.VERSION_NAME
        binding.textViewVersion.text = versionName + " (build " + versionCode + ")"

        return binding.root
    }
}