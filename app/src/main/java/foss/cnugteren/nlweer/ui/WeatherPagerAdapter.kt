package foss.cnugteren.nlweer.ui

import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import foss.cnugteren.nlweer.MapData
import foss.cnugteren.nlweer.R
import foss.cnugteren.nlweer.ui.fragments.BuienradarChartFragment
import foss.cnugteren.nlweer.ui.fragments.BuienradarPluimFragment
import foss.cnugteren.nlweer.ui.fragments.KnmiPluimFragment
import foss.cnugteren.nlweer.ui.fragments.KnmiSixDayForecastFragment
import foss.cnugteren.nlweer.ui.fragments.KnmiTextFragment
import foss.cnugteren.nlweer.ui.fragments.MapFragment

class WeatherPagerAdapter(
    activity: FragmentActivity,
    private val pages: List<MapData>
) : FragmentStateAdapter(activity) {

    override fun getItemCount(): Int = pages.size

    override fun createFragment(position: Int): Fragment {
        val item = pages[position]
        return when (item.navId) {
            R.id.nav_knmi_text -> KnmiTextFragment()
            R.id.nav_knmi_sixdayforecast -> KnmiSixDayForecastFragment()
            R.id.nav_knmi_pluim -> KnmiPluimFragment()
            R.id.nav_buienradar_chart -> BuienradarChartFragment()
            R.id.nav_buienradar_pluim -> BuienradarPluimFragment()
            else -> MapFragment().apply {
                arguments = bundleOf(MapFragment.ARG_NAV_ID to item.navId)
            }
        }
    }
}
