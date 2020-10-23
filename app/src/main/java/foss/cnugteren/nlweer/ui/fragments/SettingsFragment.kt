package foss.cnugteren.nlweer.ui.fragments

import android.Manifest
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent.getIntent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.core.app.ActivityCompat
import androidx.preference.*
import com.google.android.material.navigation.NavigationView
import foss.cnugteren.nlweer.*
import foss.cnugteren.nlweer.R


class SettingsFragment : PreferenceFragmentCompat(),
    SharedPreferences.OnSharedPreferenceChangeListener {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.fragment_settings, rootKey)
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        enableDisableLocationProviderButton()

        // Don't display floating navigation buttons
        val activity = this.activity as MainActivity
        activity.toggleNavigationButtons(false)

        // Sets the initial values as summaries
        val latString = sharedPreferences.getString("location_latitude", null)
        val lonString = sharedPreferences.getString("location_longitude", null)
        val locationProvider = findPreference<ListPreference>("location_provider")?.entry
        val language = findPreference<ListPreference>("language")?.entry
        val backgroundColour = findPreference<ListPreference>("background_colour")?.entry
        val locationShape = findPreference<ListPreference>("location_shape")?.entry
        val locationColour = findPreference<ListPreference>("location_colour")?.entry
        findPreference<EditTextPreference>("location_latitude")?.summary = latString
        findPreference<EditTextPreference>("location_longitude")?.summary = lonString
        findPreference<ListPreference>("language")?.summary = language
        findPreference<ListPreference>("location_provider")?.summary = locationProvider
        findPreference<ListPreference>("background_colour")?.summary = backgroundColour
        findPreference<ListPreference>("location_shape")?.summary = locationShape
        findPreference<ListPreference>("location_colour")?.summary = locationColour

        // Sets the views listed in the navigation menu
        val navViewsVisibleKNMI = findPreference<MultiSelectListPreference>("settings_nav_view_knmi")
        if (navViewsVisibleKNMI != null) {
            val navItemsKNMI = sharedPreferences.getStringSet("settings_nav_view_knmi", setOf("not_yet_set"))
            setNavViews(navViewsVisibleKNMI, KNMI_ITEMS, navItemsKNMI == setOf("not_yet_set"))
        }
        val navViewsVisibleBuienradar = findPreference<MultiSelectListPreference>("settings_nav_view_buienradar")
        if (navViewsVisibleBuienradar != null) {
            val navItemsBuienradar = sharedPreferences.getStringSet("settings_nav_view_buienradar", setOf("not_yet_set"))
            setNavViews(navViewsVisibleBuienradar, BUIENRADAR_ITEMS, navItemsBuienradar == setOf("not_yet_set"))
        }

        // Sets the default view list options
        val defaultViewSelection = findPreference<ListPreference>("settings_default_view_listpreference")
        if (defaultViewSelection != null) {
            setListPreferenceData(defaultViewSelection)
            defaultViewSelection.onPreferenceClickListener = (object : Preference.OnPreferenceClickListener {
                override fun onPreferenceClick(preference: Preference?): Boolean {
                    setListPreferenceData(defaultViewSelection)
                    return false
                }
            })
        }
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

    private fun enableDisableLocationProviderButton() {
        // The 'choose location provider' button needs to be disabled/enabled appropriately
        val locationProviderSetting = findPreference<ListPreference>("location_provider")
        val showMyLocation = findPreference<SwitchPreferenceCompat>("location_enable")
        val useAutoLocation = findPreference<SwitchPreferenceCompat>("gps_enable")
        if (locationProviderSetting != null && showMyLocation != null && useAutoLocation != null) {
            locationProviderSetting.isEnabled = (showMyLocation.isChecked && useAutoLocation.isChecked)
        }
    }

    private fun setNavViews(multiSelect: MultiSelectListPreference,
                            items: Array<Array<Int>>, do_set_defaults: Boolean) {
        // Add all the items possible to show in the nav menu to the list of options
        val entries = mutableListOf<String>()
        val values = mutableListOf<String>()
        val defaults = mutableListOf<String>()
        for (item in items) {
            entries.add(getString(item[0]))
            values.add(item[1].toString())
            if (item[2] == 1) {
                defaults.add(item[1].toString())
            }
        }
        multiSelect.entries = entries.toTypedArray()
        multiSelect.entryValues = values.toTypedArray()
        if (do_set_defaults) {
            multiSelect.values = defaults.toSet()
        }
    }

    private fun setListPreferenceData(defaultViewSelection: ListPreference) {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        val sourceEnableKNMI = sharedPreferences.getBoolean("knmi_enable", true)
        val sourceEnableBuienradar = sharedPreferences.getBoolean("buienradar_enable", false)

        // Retrieves the view to be able to get the nav-menu to check for visibility
        val activity = this.activity as MainActivity
        val navView: NavigationView = activity.findViewById(R.id.nav_view)
        val menu = navView.menu

        // Add all the items that are currently visible in the nav menu to the list of options
        val entries = mutableListOf(getString(R.string.menu_empty))
        val values = mutableListOf(R.id.nav_empty.toString())
        if (sourceEnableKNMI) {
            for (item in KNMI_ITEMS) {
                if (menu.findItem(item[1]).isVisible) {
                    entries.add("KNMI: " + getString(item[0]))
                    values.add(item[1].toString())
                }
            }
        }
        if (sourceEnableBuienradar) {
            for (item in BUIENRADAR_ITEMS) {
                if (menu.findItem(item[1]).isVisible) {
                    entries.add("Buienradar: " + getString(item[0]))
                    values.add(item[1].toString())
                }
            }
        }

        defaultViewSelection.entries = entries.toTypedArray()
        defaultViewSelection.entryValues = values.toTypedArray()
        defaultViewSelection.setDefaultValue(R.id.nav_knmi_rain_m1.toString())
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String) {
        val pref = findPreference<Preference>(key)

        // If the value of the settings change, sets the new values as summaries
        if (pref is EditTextPreference) {
            pref.summary = sharedPreferences.getString(key, "")
        }
        if (pref is ListPreference &&
            (key == "language" || key == "background_colour" || key == "location_shape" || key == "location_colour" || key == "location_provider")) {
            pref.summary = findPreference<ListPreference>(key)?.entry
        }

        // Select the language
        if (pref is ListPreference && key == "language") {
            val activity = this.activity as MainActivity
            // Now restart the activity (old style, new style with activity.recreate() doesn't work)
            val intent = activity.intent
            activity.finish()
            startActivity(intent)
        }

        // Change data sources for display in the menu
        if (pref is SwitchPreferenceCompat && (key == "buienradar_enable" || key == "knmi_enable")) {
            val activity = this.activity as MainActivity
            activity.setMenuItemsVisibility()
            changeDefaultViewIfDisabled(sharedPreferences)
        }

        // Change individual maps for display in the menu
        if (pref is MultiSelectListPreference && (key == "settings_nav_view_knmi" || key == "settings_nav_view_buienradar")) {
            val activity = this.activity as MainActivity
            activity.setMenuItemsVisibility()
            changeDefaultViewIfDisabled(sharedPreferences)
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
                val locationProvider = sharedPreferences.getString("location_provider", "network")
                if (locationProvider == "gps") {
                    ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),1)
                }
                else { // network
                    ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION),1)
                }
            }
            activity.setLocationManager()
        }

        // Set/unset the kind of location provider
        if (pref is ListPreference && key == "location_provider") {
            val activity = this.activity as MainActivity
            val locationProvider = pref.value
            if (locationProvider == "gps") {
                ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),1)
            }
            else { // network
                ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION),1)
            }
            activity.setLocationManager()
        }

        // Toggle the location provider button
        enableDisableLocationProviderButton()
    }

    fun changeDefaultViewIfDisabled(sharedPreferences: SharedPreferences) {
        val defaultViewId = sharedPreferences.getString("settings_default_view_listpreference", (R.id.nav_knmi_rain_m1).toString())?.toInt()
        if (defaultViewId != null && defaultViewId != R.id.nav_empty) {
            val activity = this.activity as MainActivity
            val navView: NavigationView = activity.findViewById(R.id.nav_view)
            val menu = navView.menu
            val parentMenuId = ALL_ITEMS[ALL_ITEMS.indexOfFirst { item -> item[1] == defaultViewId }][3]
            if (!menu.findItem(defaultViewId).isVisible || !menu.findItem(parentMenuId).isVisible) {
                val pref = findPreference<ListPreference>("settings_default_view_listpreference")
                if (pref != null) {
                    pref.value = R.id.nav_empty.toString()
                }
            }
        }
    }
}