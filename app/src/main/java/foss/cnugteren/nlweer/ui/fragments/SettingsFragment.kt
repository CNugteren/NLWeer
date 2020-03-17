package foss.cnugteren.nlweer.ui.fragments

import android.Manifest
import android.app.AlertDialog
import android.content.DialogInterface
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
        enableDisableLocationProviderButton()

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

    fun enableDisableLocationProviderButton() {
        // The 'choose location provider' button needs to be disabled/enabled appropriapatly
        val locationProviderSetting = findPreference("location_provider") as ListPreference
        val showMyLocation = findPreference("location_enable") as SwitchPreferenceCompat
        val useAutoLocation = findPreference("gps_enable") as SwitchPreferenceCompat
        locationProviderSetting.isEnabled = (showMyLocation.isChecked && useAutoLocation.isChecked)
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String) {
        val pref = findPreference(key)

        // If the value of the settings change, sets the new values as summaries
        if (pref is EditTextPreference) {
            pref.summary = sharedPreferences.getString(key, "")
        }

        // Displays Buienradar permission confirmation dialog
        if (pref is SwitchPreferenceCompat && key == "buienradar_enable") {
            if (pref.isChecked) {
                val alertDialog: AlertDialog? = activity?.let {
                    val builder = AlertDialog.Builder(it)
                    builder.setMessage(R.string.settings_buienradar_enable_message)
                    builder.setTitle(R.string.settings_buienradar_enable_title)
                    builder.apply {
                        setPositiveButton(R.string.settings_buienradar_enable_accept,
                            DialogInterface.OnClickListener { _, _ -> })
                        setNegativeButton(R.string.settings_buienradar_enable_decline,
                            DialogInterface.OnClickListener { _, _ -> pref.isChecked = false })
                    }
                    builder.create()
                }
                alertDialog?.show()
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
                else { // Network
                    ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION),1)
                }
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
            else { // Network
                ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION),1)
            }
            activity.setLocationManager()
        }

        // Toggle the location provider button
        enableDisableLocationProviderButton()
    }
}