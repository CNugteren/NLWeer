package foss.cnugteren.nlweer

import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
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
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_knmi_rain_m1,
                R.id.nav_knmi_today,
                R.id.nav_knmi_tomorrow,
                R.id.nav_knmi_tonight,
                R.id.nav_knmi_temperature,
                R.id.nav_knmi_wind,
                R.id.nav_knmi_text,
                R.id.nav_buienradar_rain_m1,
                R.id.nav_buienradar_chart
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
        setLocationManager()
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

        val sourceEnableKNMI = true
        val menuKnmi = menu.findItem(R.id.knmi_menu)
        menuKnmi.isVisible = sourceEnableKNMI

        val sourceEnableBuienradar = sharedPreferences.getBoolean("buienradar_enable", false)
        val menuBuienradar = menu.findItem(R.id.buienradar_menu)
        menuBuienradar.isVisible = sourceEnableBuienradar
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
}
