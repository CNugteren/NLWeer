package foss.cnugteren.nlweer

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
import com.google.android.material.navigation.NavigationView
import foss.cnugteren.nlweer.ui.fragments.BaseFragment


class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)
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
                R.id.nav_knmi_wind
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
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

    // Menu button: refresh
    fun onClickRefresh(@Suppress("UNUSED_PARAMETER") v: MenuItem) {
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment)
        if (navHostFragment != null) {
            val fragment: Fragment = navHostFragment.childFragmentManager.fragments[0]
            if (fragment is BaseFragment) {
                fragment.refresh()
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
}
