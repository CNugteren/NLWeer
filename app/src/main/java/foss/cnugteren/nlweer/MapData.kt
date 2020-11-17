package foss.cnugteren.nlweer

data class MapData(
    val stringId: Int,
    val navId: Int,
    val defaultVisible: Int,
    val parentMenuId: Int
)

val KNMI_ITEMS = arrayOf(
    MapData(R.string.menu_knmi_rain_m1, R.id.nav_knmi_rain_m1, 1, R.id.knmi_menu),
    MapData(R.string.menu_knmi_today, R.id.nav_knmi_today, 1, R.id.knmi_menu),
    MapData(R.string.menu_knmi_tonight, R.id.nav_knmi_tonight, 1, R.id.knmi_menu),
    MapData(R.string.menu_knmi_tomorrow, R.id.nav_knmi_tomorrow, 1, R.id.knmi_menu),
    MapData(R.string.menu_knmi_temperature, R.id.nav_knmi_temperature, 1, R.id.knmi_menu),
    MapData(R.string.menu_knmi_wind, R.id.nav_knmi_wind, 1, R.id.knmi_menu),
    MapData(R.string.menu_knmi_visibility, R.id.nav_knmi_visibility, 0, R.id.knmi_menu),
    MapData(R.string.menu_knmi_humidity, R.id.nav_knmi_humidity, 0, R.id.knmi_menu),
    MapData(R.string.menu_knmi_warnings, R.id.nav_knmi_warnings, 0, R.id.knmi_menu),
    MapData(R.string.menu_knmi_sun_today, R.id.nav_knmi_sun_tod, 0, R.id.knmi_menu),
    MapData(R.string.menu_knmi_sun_tomorrow, R.id.nav_knmi_sun_tom, 0, R.id.knmi_menu),
    MapData(R.string.menu_knmi_text, R.id.nav_knmi_text, 1, R.id.knmi_menu)
)
val BUIENRADAR_ITEMS = arrayOf(
    MapData(R.string.menu_buienradar_rain_m1, R.id.nav_buienradar_rain_m1, 1, R.id.buienradar_menu),
    MapData(R.string.menu_buienradar_sun, R.id.nav_buienradar_sun, 1, R.id.buienradar_menu),
    MapData(R.string.menu_buienradar_cloud, R.id.nav_buienradar_cloud, 1, R.id.buienradar_menu),
    MapData(R.string.menu_buienradar_drizzle, R.id.nav_buienradar_drizzle, 0, R.id.buienradar_menu),
    MapData(R.string.menu_buienradar_hail, R.id.nav_buienradar_hail, 0, R.id.buienradar_menu),
    MapData(R.string.menu_buienradar_chart, R.id.nav_buienradar_chart, 1, R.id.buienradar_menu)
)
val ALL_ITEMS = KNMI_ITEMS + BUIENRADAR_ITEMS
