package foss.cnugteren.nlweer

data class MapData(
    val stringId: Int,
    val navId: Int,
    val defaultVisible: Int,
    val parentMenuId: Int,
    val mapUrl: String,
    val imageWidth: Int,
    val imageHeight: Int,
    val coordinates: Array<Float>
)

val KNMI_ITEMS = arrayOf(
    // Shown by default
    MapData(R.string.menu_knmi_rain_m1, R.id.nav_knmi_rain_m1, 1, R.id.knmi_menu, "https://cdn.knmi.nl/knmi/map/general/weather-map.gif", 425, 445, arrayOf(50.60f, 1.85f, 54.05f, 7.20f, -0.10f, 0.09f)),
    MapData(R.string.menu_knmi_today, R.id.nav_knmi_today, 1, R.id.knmi_menu, "https://cdn.knmi.nl/knmi/map/current/weather/forecast/kaart_verwachtingen_Vandaag_dag.gif", 425, 467, arrayOf(50.70f, 3.10f, 53.65f, 7.55f, -0.01f, 0.00f)),
    MapData(R.string.menu_knmi_tonight, R.id.nav_knmi_tonight, 1, R.id.knmi_menu, "https://cdn.knmi.nl/knmi/map/current/weather/forecast/kaart_verwachtingen_Morgen_nacht.gif", 425, 467, arrayOf(50.70f, 3.10f, 53.65f, 7.55f, -0.01f, 0.00f)),
    MapData(R.string.menu_knmi_tomorrow, R.id.nav_knmi_tomorrow, 1, R.id.knmi_menu, "https://cdn.knmi.nl/knmi/map/current/weather/forecast/kaart_verwachtingen_Morgen_dag.gif", 425, 467, arrayOf(50.70f, 3.10f, 53.65f, 7.55f, -0.01f, 0.00f)),
    MapData(R.string.menu_knmi_temperature, R.id.nav_knmi_temperature, 1, R.id.knmi_menu, "https://cdn.knmi.nl/knmi/map/page/weer/actueel-weer/temperatuur.png", 569, 622, arrayOf(50.70f, 2.95f, 53.65f, 7.30f, -0.01f, 0.00f)),
    MapData(R.string.menu_knmi_wind, R.id.nav_knmi_wind, 1, R.id.knmi_menu, "https://cdn.knmi.nl/knmi/map/page/weer/actueel-weer/windkracht.png", 569, 622, arrayOf(50.70f, 2.95f, 53.65f, 7.30f, -0.01f, 0.00f)),
    // Optionally shown
    MapData(R.string.menu_knmi_visibility, R.id.nav_knmi_visibility, 0, R.id.knmi_menu, "https://cdn.knmi.nl/knmi/map/page/weer/actueel-weer/zicht.png", 569, 622, arrayOf(50.70f, 2.95f, 53.65f, 7.30f, -0.01f, 0.00f)),
    MapData(R.string.menu_knmi_humidity, R.id.nav_knmi_humidity, 0, R.id.knmi_menu, "https://cdn.knmi.nl/knmi/map/page/weer/actueel-weer/relvocht.png", 569, 622, arrayOf(50.70f, 2.95f, 53.65f, 7.30f, -0.01f, 0.00f)),
    MapData(R.string.menu_knmi_warnings, R.id.nav_knmi_warnings, 0, R.id.knmi_menu, "https://cdn.knmi.nl/knmi/map/current/weather/warning/waarschuwing_land_0_new.gif", 425, 457, arrayOf(50.70f, 3.10f, 53.65f, 7.55f, -0.01f, 0.00f)),
    MapData(R.string.menu_knmi_sun_today, R.id.nav_knmi_sun_tod, 0, R.id.knmi_menu, "https://cdn.knmi.nl/knmi/map/page/weer/waarschuwingen_verwachtingen/zonkracht/zonkracht_dag0.gif", 425, 457, arrayOf(50.70f, 3.10f, 53.65f, 7.55f, -0.01f, 0.00f)),
    MapData(R.string.menu_knmi_sun_tomorrow, R.id.nav_knmi_sun_tom, 0, R.id.knmi_menu, "https://cdn.knmi.nl/knmi/map/page/weer/waarschuwingen_verwachtingen/zonkracht/zonkracht_dag1.gif", 425, 457, arrayOf(50.70f, 3.10f, 53.65f, 7.55f, -0.01f, 0.00f)),
    MapData(R.string.menu_knmi_daily_max_temperature, R.id.nav_knmi_daily_max_temperature, 0, R.id.knmi_menu, "https://cdn.knmi.nl/knmi/map/page/klimatologie/geografische-overzichten/tx/tx.png", 570, 622, arrayOf(50.70f, 2.95f, 53.65f, 7.30f, -0.01f, 0.00f)),
    MapData(R.string.menu_knmi_daily_min_temperature, R.id.nav_knmi_daily_min_temperature, 0, R.id.knmi_menu, "https://cdn.knmi.nl/knmi/map/page/klimatologie/geografische-overzichten/tn/tn.png", 570, 622, arrayOf(50.70f, 2.95f, 53.65f, 7.30f, -0.01f, 0.00f)),
    MapData(R.string.menu_knmi_daily_max_wind_gust, R.id.nav_knmi_daily_max_wind_gust, 0, R.id.knmi_menu, "https://cdn.knmi.nl/knmi/map/page/klimatologie/geografische-overzichten/fxx/fxx.png", 570, 622, arrayOf(50.70f, 2.95f, 53.65f, 7.30f, -0.01f, 0.00f)),
    MapData(R.string.menu_knmi_daily_max_wind_speed, R.id.nav_knmi_daily_max_wind_speed, 0, R.id.knmi_menu, "https://cdn.knmi.nl/knmi/map/page/klimatologie/geografische-overzichten/fhx/fhx.png", 570, 622, arrayOf(50.70f, 2.95f, 53.65f, 7.30f, -0.01f, 0.00f)),
    MapData(R.string.menu_knmi_daily_rain, R.id.nav_knmi_daily_rain, 0, R.id.knmi_menu, "https://cdn.knmi.nl/knmi/map/page/klimatologie/geografische-overzichten/prec/prec.png", 570, 622, arrayOf(50.70f, 2.95f, 53.65f, 7.30f, -0.01f, 0.00f)),
    MapData(R.string.menu_knmi_daily_snow, R.id.nav_knmi_daily_snow, 0, R.id.knmi_menu, "https://cdn.knmi.nl/knmi/map/page/klimatologie/geografische-overzichten/sx/sx.png", 570, 622, arrayOf(50.70f, 2.95f, 53.65f, 7.30f, -0.01f, 0.00f)),
    MapData(R.string.menu_knmi_daily_sun, R.id.nav_knmi_daily_sun, 0, R.id.knmi_menu, "https://cdn.knmi.nl/knmi/map/page/klimatologie/geografische-overzichten/sq/sq.png", 570, 622, arrayOf(50.70f, 2.95f, 53.65f, 7.30f, -0.01f, 0.00f)),
    MapData(R.string.menu_knmi_europe_synoptic_now, R.id.nav_knmi_europe_synoptic_now, 0, R.id.knmi_menu, "https://cdn.knmi.nl/knmi/map/page/weer/waarschuwingen_verwachtingen/weerkaarten/AL<DDHH>_large.gif", 1083, 696, arrayOf(0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f)),
    MapData(R.string.menu_knmi_europe_synoptic_next_12h, R.id.nav_knmi_europe_synoptic_next_12h, 0, R.id.knmi_menu, "https://cdn.knmi.nl/knmi/map/page/weer/waarschuwingen_verwachtingen/weerkaarten/PL<DDHH>+12_large.gif", 1083, 696, arrayOf(0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f)),
    MapData(R.string.menu_knmi_europe_synoptic_next_24h, R.id.nav_knmi_europe_synoptic_next_24h, 0, R.id.knmi_menu, "https://cdn.knmi.nl/knmi/map/page/weer/waarschuwingen_verwachtingen/weerkaarten/PL<DDHH>+24_large.gif", 1083, 696, arrayOf(0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f)),
    MapData(R.string.menu_knmi_satellite, R.id.nav_knmi_satellite, 0, R.id.knmi_menu, "https://cdn.knmi.nl/knmi/map/page/weer/actueel-weer/satelliet/satloop.gif", 613, 394, arrayOf(0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f)),
    MapData(R.string.menu_knmi_chill_temperature, R.id.nav_knmi_chill_temperature, 0, R.id.knmi_menu, "https://cdn.knmi.nl/knmi/map/page/weer/actueel-weer/gevoelstemperatuur.png", 569, 622, arrayOf(50.70f, 2.95f, 53.65f, 7.30f, -0.01f, 0.00f)),
    MapData(R.string.menu_knmi_rain_m1_no_temp, R.id.nav_knmi_rain_m1_no_temp, 0, R.id.knmi_menu, "https://cdn.knmi.nl/knmi/map/page/weer/actueel-weer/neerslagradar/WWWRADAR_loop.gif", 425, 445, arrayOf(50.60f, 1.85f, 54.05f, 7.20f, -0.10f, 0.09f)),
    // Special maps
    MapData(R.string.menu_knmi_text, R.id.nav_knmi_text, 1, R.id.knmi_menu, "N/A", 0, 0, arrayOf(0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f)),
    MapData(R.string.menu_knmi_pluim, R.id.nav_knmi_pluim, 0, R.id.knmi_menu, "N/A", 0, 0, arrayOf(0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f))
    MapData(R.string.menu_knmi_forecast, R.id.nav_knmi_forecast, 1, R.id.knmi_menu, "N/A", 0, 0, arrayOf(0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f)),
)

val BUIENRADAR_ITEMS = arrayOf(
    // Shown by default
    MapData(R.string.menu_buienradar_rain_m1, R.id.nav_buienradar_rain_m1, 1, R.id.buienradar_menu, "https://api.buienradar.nl/image/1.0/RadarMapNL?w=550&h=510", 550, 510, arrayOf(49.50f, 0.15f, 54.80f, 10.25f, 0.02f, -0.01f)),
    MapData(R.string.menu_buienradar_sun, R.id.nav_buienradar_sun, 1, R.id.buienradar_menu, "https://image.buienradar.nl/2.0/image/animation/RadarMapSunNL?w=550&h=510", 550, 510, arrayOf(49.50f, 0.15f, 54.80f, 10.25f, 0.02f, -0.01f)),
    MapData(R.string.menu_buienradar_cloud, R.id.nav_buienradar_cloud, 1, R.id.buienradar_menu, "https://image.buienradar.nl/2.0/image/animation/RadarMapCloudNL?w=550&h=510", 550, 510, arrayOf(49.50f, 0.15f, 54.80f, 10.25f, 0.02f, -0.01f)),
    // Optionally shown
    MapData(R.string.menu_buienradar_drizzle, R.id.nav_buienradar_drizzle, 0, R.id.buienradar_menu, "https://image.buienradar.nl/2.0/image/animation/RadarMapDrizzleNL?w=550&h=510", 550, 510, arrayOf(49.50f, 0.15f, 54.80f, 10.25f, 0.02f, -0.01f)),
    MapData(R.string.menu_buienradar_hail, R.id.nav_buienradar_hail, 0, R.id.buienradar_menu, "https://image.buienradar.nl/2.0/image/animation/RadarMapHailNL?w=550&h=510", 550, 510, arrayOf(49.50f, 0.15f, 54.80f, 10.25f, 0.02f, -0.01f)),
    // Special maps
    MapData(R.string.menu_buienradar_chart, R.id.nav_buienradar_chart, 1, R.id.buienradar_menu, "N/A", 0, 0, arrayOf(0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f)),
    MapData(R.string.menu_buienradar_pluim, R.id.nav_buienradar_pluim, 0, R.id.buienradar_menu, "N/A", 0, 0, arrayOf(0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f))
)

val ALL_ITEMS = KNMI_ITEMS + BUIENRADAR_ITEMS
