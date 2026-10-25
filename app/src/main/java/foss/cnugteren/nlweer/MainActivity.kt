package foss.cnugteren.nlweer

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.app.BaseContextWrappingDelegate
import androidx.appcompat.widget.Toolbar
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.preference.PreferenceManager
import androidx.viewpager2.widget.ViewPager2
import foss.cnugteren.nlweer.databinding.ActivityMainBinding
import foss.cnugteren.nlweer.ui.WeatherPagerAdapter
import foss.cnugteren.nlweer.ui.fragments.AboutFragment
import foss.cnugteren.nlweer.ui.fragments.BuienradarChartFragment
import foss.cnugteren.nlweer.ui.fragments.EmptyFragment
import foss.cnugteren.nlweer.ui.fragments.KnmiTextFragment
import foss.cnugteren.nlweer.ui.fragments.MapFragment
import foss.cnugteren.nlweer.ui.fragments.SettingsFragment

class MainActivity : AppCompatActivity() {

    private enum class Overlay {
        NONE, EMPTY, SETTINGS, ABOUT
    }

    private var locationManager : LocationManager? = null
    var gpsLat: Float? = null
    var gpsLon: Float? = null
    var appIsInDarkMode: Boolean = false
    private var baseContextWrappingDelegate: AppCompatDelegate? = null
    private lateinit var binding: ActivityMainBinding
    private lateinit var pager: ViewPager2
    private lateinit var fragmentOverlay: View
    private lateinit var drawerToggle: ActionBarDrawerToggle
    private var pages: List<MapData> = emptyList()
    private var pagerInitialized = false
    private var overlay = Overlay.NONE

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        pager = findViewById(R.id.pager)
        fragmentOverlay = findViewById(R.id.fragment_overlay)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        setupDrawer(toolbar)

        setMenuItemsVisibility()
        setupPager()
        setupNavigationView()
        setLocationManager()
        supportFragmentManager.addOnBackStackChangedListener {
            onOverlayBackStackChanged()
        }

        if (savedInstanceState == null) { // Only when the app starts for the first time
            setStartFragment()
        } else {
            restoreOverlayState()
        }
        setAppTheme()
    }

    private fun setupDrawer(toolbar: Toolbar) {
        drawerToggle = ActionBarDrawerToggle(
            this,
            binding.drawerLayout,
            toolbar,
            R.string.nav_drawer_open,
            R.string.nav_drawer_close
        )
        binding.drawerLayout.addDrawerListener(drawerToggle)
        drawerToggle.syncState()
    }

    private fun setupPager() {
        pager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                if (overlay == Overlay.NONE) {
                    updateActionBarForPage(position)
                }
            }
        })
        pagerInitialized = true
        rebuildPagerPages()
    }

    private fun setupNavigationView() {
        binding.navView.setNavigationItemSelectedListener { item ->
            val index = pages.indexOfFirst { page -> page.navId == item.itemId }
            if (index >= 0) {
                showPagerBeforeNavigation()
                pager.setCurrentItem(index, false)
                binding.drawerLayout.closeDrawers()
                true
            } else {
                false
            }
        }
    }

    private fun buildVisiblePages(): List<MapData> {
        val menu = binding.navView.menu
        return ALL_ITEMS.filter { item ->
            val menuItem = menu.findItem(item.navId)
            val parentMenuItem = menu.findItem(item.parentMenuId)
            menuItem != null && parentMenuItem != null &&
                menuItem.isVisible && parentMenuItem.isVisible
        }
    }

    private fun rebuildPagerPages() {
        val currentNavId = pages.getOrNull(pager.currentItem)?.navId
        pages = buildVisiblePages()
        pager.adapter = WeatherPagerAdapter(this, pages)

        if (pages.isEmpty()) {
            pager.setCurrentItem(0, false)
            if (overlay == Overlay.NONE) {
                showEmptyOverlay()
            }
            return
        }

        val targetIndex = pages.indexOfFirst { page -> page.navId == currentNavId }
            .let { if (it >= 0) it else 0 }
        pager.setCurrentItem(targetIndex, false)

        if (overlay == Overlay.EMPTY) {
            hideEmptyOverlay()
        } else if (overlay == Overlay.NONE) {
            updateActionBarForPage(targetIndex)
        }
    }

    private fun updateActionBarForPage(position: Int) {
        val page = pages.getOrNull(position)
        val title = if (page != null) getString(page.stringId) else getString(R.string.app_name)
        setActionBarMode(showUp = false, title = title)
        if (page != null) {
            binding.navView.setCheckedItem(page.navId)
        }
    }

    private fun setActionBarMode(showUp: Boolean, title: String) {
        supportActionBar?.title = title
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        drawerToggle.isDrawerIndicatorEnabled = !showUp
        drawerToggle.syncState()
    }

    private fun setStartFragment() {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(applicationContext)
        val defaultViewId = sharedPreferences
            .getString("settings_default_view_listpreference", (R.id.nav_knmi_rain_m1).toString())
            ?.toIntOrNull() ?: R.id.nav_knmi_rain_m1

        if (pages.isEmpty()) {
            return
        }

        if (defaultViewId == R.id.nav_empty) {
            showEmptyOverlay()
            return
        }

        val index = pages.indexOfFirst { page -> page.navId == defaultViewId }
        pager.setCurrentItem(if (index >= 0) index else 0, false)
        updateActionBarForPage(pager.currentItem)
    }

    private fun currentContentFragment(): Fragment? {
        if (!pager.isVisible) {
            return supportFragmentManager.findFragmentById(R.id.fragment_overlay)
        }
        return supportFragmentManager.findFragmentByTag("f${pager.currentItem}")
    }

    private fun showPagerBeforeNavigation() {
        when (overlay) {
            Overlay.EMPTY -> hideEmptyOverlay()
            Overlay.SETTINGS, Overlay.ABOUT -> {
                if (supportFragmentManager.backStackEntryCount > 0) {
                    supportFragmentManager.popBackStackImmediate()
                }
            }
            Overlay.NONE -> { }
        }
    }

    private fun showEmptyOverlay() {
        overlay = Overlay.EMPTY
        showOverlayFragment(EmptyFragment(), addToBackStack = false)
        setActionBarMode(showUp = false, title = getString(R.string.menu_empty))
    }

    private fun hideEmptyOverlay() {
        if (overlay != Overlay.EMPTY) {
            return
        }
        val fragment = supportFragmentManager.findFragmentById(R.id.fragment_overlay)
        if (fragment != null) {
            supportFragmentManager.beginTransaction().remove(fragment).commitNow()
        }
        overlay = Overlay.NONE
        fragmentOverlay.isVisible = false
        pager.isVisible = true
        updateActionBarForPage(pager.currentItem)
    }

    private fun showSettings() {
        if (supportFragmentManager.backStackEntryCount > 0) {
            supportFragmentManager.popBackStackImmediate()
        }
        overlay = Overlay.SETTINGS
        showOverlayFragment(SettingsFragment(), addToBackStack = true)
        setActionBarMode(showUp = true, title = getString(R.string.menu_settings))
    }

    private fun showAbout() {
        if (supportFragmentManager.backStackEntryCount > 0) {
            supportFragmentManager.popBackStackImmediate()
        }
        overlay = Overlay.ABOUT
        showOverlayFragment(AboutFragment(), addToBackStack = true)
        setActionBarMode(showUp = true, title = getString(R.string.menu_about))
    }

    private fun showOverlayFragment(fragment: Fragment, addToBackStack: Boolean) {
        pager.isVisible = false
        fragmentOverlay.isVisible = true
        val transaction = supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_overlay, fragment)
        if (addToBackStack) {
            transaction.addToBackStack(fragment.javaClass.simpleName)
        }
        transaction.commit()
    }

    private fun onOverlayBackStackChanged() {
        if (supportFragmentManager.backStackEntryCount == 0 && overlay != Overlay.EMPTY) {
            overlay = Overlay.NONE
            if (pages.isEmpty()) {
                showEmptyOverlay()
            } else {
                fragmentOverlay.isVisible = false
                pager.isVisible = true
                updateActionBarForPage(pager.currentItem)
            }
        }
    }

    private fun restoreOverlayState() {
        val fragment = supportFragmentManager.findFragmentById(R.id.fragment_overlay)
        if (fragment == null) {
            overlay = Overlay.NONE
            if (pages.isEmpty()) {
                showEmptyOverlay()
            } else {
                fragmentOverlay.isVisible = false
                pager.isVisible = true
                updateActionBarForPage(pager.currentItem)
            }
            return
        }

        pager.isVisible = false
        fragmentOverlay.isVisible = true
        when (fragment) {
            is SettingsFragment -> {
                overlay = Overlay.SETTINGS
                setActionBarMode(showUp = true, title = getString(R.string.menu_settings))
            }
            is AboutFragment -> {
                overlay = Overlay.ABOUT
                setActionBarMode(showUp = true, title = getString(R.string.menu_about))
            }
            else -> {
                overlay = Overlay.EMPTY
                setActionBarMode(showUp = false, title = getString(R.string.menu_empty))
            }
        }
    }

    // From https://stackoverflow.com/questions/55265834/change-locale-not-work-after-migrate-to-androidx
    override fun getDelegate() = baseContextWrappingDelegate ?: BaseContextWrappingDelegate(super.getDelegate()).apply {
        baseContextWrappingDelegate = this
    }

    override fun createConfigurationContext(overrideConfiguration: Configuration) : Context {
        val context = super.createConfigurationContext(overrideConfiguration)
        super.attachBaseContext(ApplicationLanguageHelper.wrap(context!!))
        return context
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        if (supportFragmentManager.backStackEntryCount > 0) {
            supportFragmentManager.popBackStack()
            return true
        }
        return super.onSupportNavigateUp()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true
        }
        if (item.itemId == android.R.id.home && supportFragmentManager.backStackEntryCount > 0) {
            supportFragmentManager.popBackStack()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    fun setMenuItemsVisibility() {
        val menu = binding.navView.menu
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(applicationContext)

        val sourceEnableKNMI = sharedPreferences.getBoolean("knmi_enable", true)
        val menuKnmi = menu.findItem(R.id.knmi_menu)
        menuKnmi.isVisible = sourceEnableKNMI

        val sourceEnableBuienradar = sharedPreferences.getBoolean("buienradar_enable", false)
        val menuBuienradar = menu.findItem(R.id.buienradar_menu)
        menuBuienradar.isVisible = sourceEnableBuienradar

        val navItemsKNMI = sharedPreferences.getStringSet("settings_nav_view_knmi", setOf("not_yet_set"))
        if (navItemsKNMI != null) {
            for (item in KNMI_ITEMS) {
                val menuItem = menu.findItem(item.navId)
                if (navItemsKNMI == setOf("not_yet_set")) {  menuItem.isVisible = (item.defaultVisible == 1) }
                else { menuItem.isVisible = (item.navId.toString() in navItemsKNMI) }
            }
        }

        val navItemsBuienradar = sharedPreferences.getStringSet("settings_nav_view_buienradar", setOf("not_yet_set"))
        if (navItemsBuienradar != null) {
            for (item in BUIENRADAR_ITEMS) {
                val menuItem = menu.findItem(item.navId)
                if (navItemsBuienradar == setOf("not_yet_set")) {  menuItem.isVisible = (item.defaultVisible == 1) }
                else { menuItem.isVisible = (item.navId.toString() in navItemsBuienradar) }
            }
        }

        if (pagerInitialized) {
            rebuildPagerPages()
        }
    }

    // Dark mode
    fun setAppTheme() {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(applicationContext)
        val darkModeSetting = sharedPreferences.getString("dark_mode", "dark_mode_system")
        if (darkModeSetting == "dark_mode_yes") {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            appIsInDarkMode = true
        }
        else if (darkModeSetting == "dark_mode_system") {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
            val darkModeFlags = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
            appIsInDarkMode = darkModeFlags == Configuration.UI_MODE_NIGHT_YES
        }
        else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            appIsInDarkMode = false
        }
    }


    // Menu button: refresh
    fun onClickRefresh(@Suppress("UNUSED_PARAMETER") v: MenuItem) {
        val fragment = currentContentFragment()
        if (fragment is MapFragment) {
            fragment.refreshPage()
        }
        if (fragment is KnmiTextFragment) {
            fragment.refreshPage()
        }
    }

    // Menu button: share
    fun onClickShare(@Suppress("UNUSED_PARAMETER") v: MenuItem) {
        val fragment = currentContentFragment()
        val url: String
        if (fragment is MapFragment) {
            url = fragment.mapUrl()
        } else {

            // Unsupported
            val alertDialog: AlertDialog? = let {
                val builder = AlertDialog.Builder(it)
                builder.setMessage(R.string.share_disabled_alert_message)
                builder.setTitle(R.string.share_disabled_alert_title)
                builder.apply {
                    setPositiveButton(R.string.settings_buienradar_enable_accept
                    ) { _, _ -> }
                }
                builder.create()
            }
            alertDialog?.show()
            return
        }

        // Share it
        val sharing = Intent(Intent.ACTION_SEND)
        sharing.type = "text/plain"
        sharing.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.share_subject))
        sharing.putExtra(Intent.EXTRA_TEXT, url)
        startActivity(Intent.createChooser(sharing, getString(R.string.share_choose)))
    }

    // Menu button: settings
    fun onClickSettings(@Suppress("UNUSED_PARAMETER") v: MenuItem) {
        showSettings()
    }

    // Menu button: about
    fun onClickAbout(@Suppress("UNUSED_PARAMETER") v: MenuItem) {
        showAbout()
    }

    // Location for GPS (if enabled)
    fun setLocationManager() {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(applicationContext)
        val gpsEnable = sharedPreferences.getBoolean("gps_enable", false)
        val locationProvider = sharedPreferences.getString("location_provider", "network")
        if (gpsEnable) {
            try {
                try {
                    locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager?
                    if (locationProvider == "network") {
                        val networkProvidedAvailable = locationManager?.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
                        if (networkProvidedAvailable != null && networkProvidedAvailable) {
                            locationManager?.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 0f, locationListener)
                        }
                    }
                    else if (locationProvider == "gps") {
                        locationManager?.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 0f, locationListener)
                    }
                } catch (_: SecurityException) { }
            }
            catch (_: Exception) { }
        }
        else {
            locationManager?.removeUpdates(locationListener)
            locationManager = null
        }
    }

    override fun onResume() {
        super.onResume()
        setLocationManager()
    }

    override fun onPause() {
        super.onPause()
        locationManager?.removeUpdates(locationListener)
    }

    // Try again the above function when the request was accepted
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            1 -> {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    setLocationManager() // Try again to make a location-manager
                }
            }
        }
    }

    // Listens to location updates
    private val locationListener: LocationListener = object : LocationListener {
        override fun onLocationChanged(location: Location) {

            // Retrieves the location
            val gpsLatCurrent = location.latitude.toFloat()
            val gpsLonCurrent = location.longitude.toFloat()
            gpsLat = gpsLatCurrent
            gpsLon = gpsLonCurrent

            // Also updates the map with the latest values
            val fragment = currentContentFragment()
            if (fragment is MapFragment) {
                fragment.setLocation(gpsLatCurrent, gpsLonCurrent)
            }
            if (fragment is BuienradarChartFragment) {
                fragment.setLocation(gpsLatCurrent, gpsLonCurrent)
            }
        }
        @Deprecated("Deprecated in Java")
        override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}
        override fun onProviderEnabled(provider: String) {}
        override fun onProviderDisabled(provider: String) {}
    }
}
