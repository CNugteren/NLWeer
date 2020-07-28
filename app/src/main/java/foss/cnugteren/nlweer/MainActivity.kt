package foss.cnugteren.nlweer

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.isVisible
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.preference.PreferenceManager
import com.google.android.material.navigation.NavigationView
import foss.cnugteren.nlweer.ui.fragments.BaseFragment
import foss.cnugteren.nlweer.ui.fragments.BuienradarChartFragment
import foss.cnugteren.nlweer.ui.fragments.KnmiTextFragment

// Each item consists of: string name, nav ID, default shown (1) or not (0)
val KNMI_ITEMS = arrayOf(
    arrayOf(R.string.menu_knmi_rain_m1, R.id.nav_knmi_rain_m1, 1),
    arrayOf(R.string.menu_knmi_today, R.id.nav_knmi_today, 1),
    arrayOf(R.string.menu_knmi_tonight, R.id.nav_knmi_tonight, 1),
    arrayOf(R.string.menu_knmi_tomorrow, R.id.nav_knmi_tomorrow, 1),
    arrayOf(R.string.menu_knmi_temperature, R.id.nav_knmi_temperature, 1),
    arrayOf(R.string.menu_knmi_wind, R.id.nav_knmi_wind, 1),
    arrayOf(R.string.menu_knmi_visibility, R.id.nav_knmi_visibility, 0),
    arrayOf(R.string.menu_knmi_humidity, R.id.nav_knmi_humidity, 0),
    arrayOf(R.string.menu_knmi_warnings, R.id.nav_knmi_warnings, 0),
    arrayOf(R.string.menu_knmi_sun_today, R.id.nav_knmi_sun_tod, 0),
    arrayOf(R.string.menu_knmi_sun_tomorrow, R.id.nav_knmi_sun_tom, 0),
    arrayOf(R.string.menu_knmi_text, R.id.nav_knmi_text, 1)
)
val BUIENRADAR_ITEMS = arrayOf(
    arrayOf(R.string.menu_buienradar_rain_m1, R.id.nav_buienradar_rain_m1, 1),
    arrayOf(R.string.menu_buienradar_sun, R.id.nav_buienradar_sun, 1),
    arrayOf(R.string.menu_buienradar_cloud, R.id.nav_buienradar_cloud, 1),
    arrayOf(R.string.menu_buienradar_drizzle, R.id.nav_buienradar_drizzle, 0),
    arrayOf(R.string.menu_buienradar_hail, R.id.nav_buienradar_hail, 0),
    arrayOf(R.string.menu_buienradar_chart, R.id.nav_buienradar_chart, 1)
)
val ALL_ITEMS = KNMI_ITEMS + BUIENRADAR_ITEMS

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration

    private var locationManager : LocationManager? = null
    var gpsLat: Float? = null
    var gpsLon: Float? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)
        setMenuItemsVisibility()
        val navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val destinations = mutableSetOf(R.id.nav_empty)
        for (item in KNMI_ITEMS) { destinations.add(item[1]) }
        for (item in BUIENRADAR_ITEMS) { destinations.add(item[1]) }
        appBarConfiguration = AppBarConfiguration(destinations, drawerLayout)

        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
        setLocationManager()
        if (savedInstanceState == null) { // Only when the app starts for the first time
            setStartFragment()
        }

        // The floating buttons (FAB) to go to the previous/next view
        val fabPrevious: View = findViewById(R.id.fab_previous)
        fabPrevious.setOnClickListener {
            val currentNavId = navController.currentDestination?.id
            val currentIndex = ALL_ITEMS.indexOfFirst { item -> item[1] == currentNavId }
            for (prevIndex in currentIndex - 1 + ALL_ITEMS.size downTo 0) { // double array instead of modulo
                val item = (ALL_ITEMS + ALL_ITEMS)[prevIndex]
                val menuItem = navView.menu.findItem(item[1])
                if (menuItem.isVisible) {
                    navController.navigate(item[1])
                    break
                }
            }
        }
        val fabNext: View = findViewById(R.id.fab_next)
        fabNext.setOnClickListener {
            val currentNavId = navController.currentDestination?.id
            val currentIndex = ALL_ITEMS.indexOfFirst { item -> item[1] == currentNavId }
            for (nextIndex in currentIndex + 1 until ALL_ITEMS.size * 2) { // double array instead of modulo
                val item = (ALL_ITEMS + ALL_ITEMS)[nextIndex]
                val menuItem = navView.menu.findItem(item[1])
                if (menuItem.isVisible) {
                    navController.navigate(item[1])
                    break
                }
            }
        }

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    fun setMenuItemsVisibility() {
        val navView: NavigationView = findViewById(R.id.nav_view)
        val menu = navView.menu
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
                val menuItem = menu.findItem(item[1])
                if (navItemsKNMI == setOf("not_yet_set")) {  menuItem.isVisible = (item[2] == 1) }
                else { menuItem.isVisible = (item[1].toString() in navItemsKNMI) }
            }
        }

        val navItemsBuienradar = sharedPreferences.getStringSet("settings_nav_view_buienradar", setOf("not_yet_set"))
        if (navItemsBuienradar != null) {
            for (item in BUIENRADAR_ITEMS) {
                val menuItem = menu.findItem(item[1])
                if (navItemsBuienradar == setOf("not_yet_set")) {  menuItem.isVisible = (item[2] == 1) }
                else { menuItem.isVisible = (item[1].toString() in navItemsBuienradar) }
            }
        }
    }

    fun setStartFragment() {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(applicationContext)
        val defaultViewId = sharedPreferences.getString("settings_default_view_listpreference", (R.id.nav_knmi_rain_m1).toString())?.toInt()
        if (defaultViewId != null) {
            val navController = findNavController(R.id.nav_host_fragment)
            val navGraph = navController.graph
            navGraph.startDestination = defaultViewId
            navController.graph = navGraph
        }
    }

    // Menu button: refresh
    fun onClickRefresh(@Suppress("UNUSED_PARAMETER") v: MenuItem) {
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment)
        if (navHostFragment != null) {
            val fragment: Fragment = navHostFragment.childFragmentManager.fragments[0]
            if (fragment is BaseFragment) {
                fragment.refreshPage()
            }
            if (fragment is KnmiTextFragment) {
                fragment.refreshPage()
            }
        }
    }

    // Menu button: share
    fun onClickShare(@Suppress("UNUSED_PARAMETER") v: MenuItem) {
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment)
        if (navHostFragment != null) {

            // Retrieve the URL to share
            val fragment: Fragment = navHostFragment.childFragmentManager.fragments[0]
            val url: String
            if (fragment is BaseFragment) {
                url = fragment.getURL()
            } else {

                // Unsupported
                val alertDialog: AlertDialog? = let {
                    val builder = AlertDialog.Builder(it)
                    builder.setMessage(R.string.share_disabled_alert_message)
                    builder.setTitle(R.string.share_disabled_alert_title)
                    builder.apply {
                        setPositiveButton(R.string.settings_buienradar_enable_accept,
                            DialogInterface.OnClickListener { _, _ -> })
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
    }

    // Menu button: settings
    fun onClickSettings(@Suppress("UNUSED_PARAMETER") v: MenuItem) {
        val navController = findNavController(R.id.nav_host_fragment)
        if (navController.currentDestination?.id != R.id.nav_settings) {
            navController.navigate(R.id.nav_settings)
        }
    }

    // Menu button: about
    fun onClickAbout(@Suppress("UNUSED_PARAMETER") v: MenuItem) {
        val navController = findNavController(R.id.nav_host_fragment)
        if (navController.currentDestination?.id != R.id.nav_about) {
            navController.navigate(R.id.nav_about)
        }
    }

    // Location for GPS (if enabled)
    fun setLocationManager() {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(applicationContext)
        val gpsEnable = sharedPreferences.getBoolean("gps_enable", false)
        val locationProvider = sharedPreferences.getString("location_provider", "Network")
        if (gpsEnable) {
            try {
                try {
                    locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager?
                    if (locationProvider == "Network") {
                        val networkProvidedAvailable = locationManager?.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
                        if (networkProvidedAvailable != null && networkProvidedAvailable) {
                            locationManager?.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 1f, locationListener)
                        }
                    }
                    else if (locationProvider == "GPS") {
                        locationManager?.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1f, locationListener)
                    }
                } catch (ex: SecurityException) { }
            }
            catch (ex: Exception) { }
        }
        else {
            locationManager?.removeUpdates(locationListener)
            locationManager = null
        }
    }

    override fun onPause() {
        super.onPause()
        locationManager?.removeUpdates(locationListener)
    }

    // Try again the above function when the request was accepted
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
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
            val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment)
            if (navHostFragment != null) {
                val fragment: Fragment = navHostFragment.childFragmentManager.fragments[0]
                if (fragment is BaseFragment) {
                    fragment.setLocation(gpsLatCurrent, gpsLonCurrent)
                }
                if (fragment is BuienradarChartFragment) {
                    fragment.setLocation(gpsLatCurrent, gpsLonCurrent)
                }
            }
        }
        override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}
        override fun onProviderEnabled(provider: String) {}
        override fun onProviderDisabled(provider: String) {}
    }

    fun toggleNavigationButtons(enable: Boolean) {
        val fabPrevious = findViewById<View>(R.id.fab_previous)
        fabPrevious.isVisible = enable
        val fabNext = findViewById<View>(R.id.fab_next)
        fabNext.isVisible = enable
    }
}
