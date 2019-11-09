package foss.cnugteren.nlweer.ui.fragments

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import foss.cnugteren.nlweer.R
import android.content.SharedPreferences
import androidx.preference.EditTextPreference
import androidx.preference.Preference


class SettingsFragment : PreferenceFragmentCompat(),
    SharedPreferences.OnSharedPreferenceChangeListener {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.fragment_settings, rootKey)
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String) {
        val pref = findPreference<Preference>(key)
        if (pref is EditTextPreference) {
            pref.summary = sharedPreferences.getString(key, "")
        }
    }
}