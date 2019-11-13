package foss.cnugteren.nlweer.ui.fragments

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import foss.cnugteren.nlweer.R
import android.content.SharedPreferences
import androidx.preference.EditTextPreference
import androidx.preference.Preference
import androidx.preference.PreferenceManager


class SettingsFragment : PreferenceFragmentCompat(),
    SharedPreferences.OnSharedPreferenceChangeListener {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.fragment_settings, rootKey)
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)

        // Sets the initial values as summaries
        val latString = sharedPreferences.getString("location_latitude", null)
        val lonString = sharedPreferences.getString("location_longitude", null)
        findPreference<Preference>("location_latitude")?.summary = latString
        findPreference<Preference>("location_longitude")?.summary = lonString
    }

    override fun onResume() {
        super.onResume()
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        sharedPreferences.registerOnSharedPreferenceChangeListener(this)
    }

    override fun onPause() {
        super.onPause()
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(this)
    }

    // If the value of the settings change, sets the new values as summaries
    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String) {
        val pref = findPreference<Preference>(key)
        if (pref is EditTextPreference) {
            pref.summary = sharedPreferences.getString(key, "")
        }
    }
}