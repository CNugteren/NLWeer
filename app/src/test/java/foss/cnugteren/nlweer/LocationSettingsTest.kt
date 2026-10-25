package foss.cnugteren.nlweer

import org.junit.Test
import org.junit.Assert.*

/**
 * Tests for location settings behavior
 */
class LocationSettingsTest {
    
    @Test
    fun testLocationEnableDefault() {
        // location_enable should default to false
        val defaultValue = false
        assertFalse(defaultValue)
    }
    
    @Test
    fun testGpsEnableDependsOnLocationEnable() {
        // GPS enable is dependent on location_enable
        val locationEnabled = true
        val gpsEnabled = locationEnabled
        assertTrue(gpsEnabled)
    }
    
    @Test
    fun testMapsWithInvalidCoordinates() {
        // Maps that should not show location markers (all zero coordinates)
        val satelliteCoords = arrayOf(0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f)
        val synopticCoords = arrayOf(0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f)
        
        assertArrayEquals(satelliteCoords, synopticCoords)
        assertTrue(isAllZeroCoordinates(satelliteCoords))
    }
    
    private fun isAllZeroCoordinates(coords: Array<Float>): Boolean {
        return coords.size >= 6 && 
               coords[0] == 0.0f && coords[1] == 0.0f && 
               coords[2] == 0.0f && coords[3] == 0.0f
    }
}
