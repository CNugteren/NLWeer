package foss.cnugteren.nlweer.ui.fragments

import android.Manifest
import android.os.Bundle
import foss.cnugteren.nlweer.R
import android.content.SharedPreferences
import androidx.core.app.ActivityCompat
import androidx.preference.*
import foss.cnugteren.nlweer.MainActivity


class SettingsFragment : PreferenceFragmentCompat(),
    SharedPreferences.OnSharedPreferenceChangeListener {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.fragment_settings, rootKey)
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)

        // The 'choose location provider' button
        val locationProviderSetting = findPreference("location_provider") as ListPreference
        val showMyLocation = findPreference("location_enable") as SwitchPreferenceCompat
        val useAutoLocation = findPreference("gps_enable") as SwitchPreferenceCompat
        locationProviderSetting.isEnabled = (showMyLocation.isChecked && useAutoLocation.isChecked)

        // Sets the initial values as summaries
        val latString = sharedPreferences.getString("location_latitude", null)
        val lonString = sharedPreferences.getString("location_longitude", null)
        val locationProvider = sharedPreferences.getString("location_provider", null)
        findPreference("location_latitude")?.summary = latString
        findPreference("location_longitude")?.summary = lonString
        findPreference("location_provider")?.summary = locationProvider
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

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String) {
        val pref = findPreference(key)
        val locationProviderSetting = findPreference("location_provider") as ListPreference

        // If the value of the settings change, sets the new values as summaries
        if (pref is EditTextPreference) {
            pref.summary = sharedPreferences.getString(key, "")
        }

        // Set/unset 'show my location'
        if (pref is SwitchPreferenceCompat && key == "location_enable") {
            if (!pref.isChecked) {
                locationProviderSetting.isEnabled = false
            }
        }

        // Set/unset automatic location
        if (pref is SwitchPreferenceCompat && key == "gps_enable") {
            val activity = this.activity as MainActivity
            if (pref.isChecked) {
                val locationProvider = sharedPreferences.getString("location_provider", "Network")
                if (locationProvider == "GPS") {
                    ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),1)
                }
                locationProviderSetting.isEnabled = true
            }
            else {
                locationProviderSetting.isEnabled = false
            }
            activity.setLocationManager()
        }

        // Set/unset the kind of location provider
        if (pref is ListPreference && key == "location_provider") {
            val activity = this.activity as MainActivity
            pref.summary = sharedPreferences.getString(key, "")
            val locationProvider = pref.value
            if (locationProvider == "GPS") {
                ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),1)
            }
            activity.setLocationManager()
        }
    }
}