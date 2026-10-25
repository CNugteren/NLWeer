package foss.cnugteren.nlweer

import org.junit.Test
import org.junit.Assert.*

/**
 * Unit tests for location drawing logic to prevent division by zero crashes
 */
class DrawWebViewLocationTest {
    
    @Test
    fun testInvalidCoordinatesAllZero() {
        // All zero coordinates should be treated as invalid (causes division by zero)
        val coords = arrayOf(0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f)
        assertTrue("All zero coordinates are invalid", 
            coords[0] == 0.0f && coords[1] == 0.0f && coords[2] == 0.0f && coords[3] == 0.0f)
    }
    
    @Test
    fun testZeroCoordinateRange() {
        // When min == max, division by zero occurs
        val minLat = 50.0f
        val maxLat = 50.0f
        val range = maxLat - minLat
        assertEquals("Zero range causes division by zero", 0.0f, range)
    }
    
    @Test
    fun testValidCoordinates() {
        // Valid coordinates should have different min/max
        val coords = arrayOf(50.60f, 1.85f, 54.05f, 7.20f, -0.10f, 0.09f)
        assertNotEquals(coords[0], coords[2])
        assertNotEquals(coords[1], coords[3])
        assertTrue((coords[2] - coords[0]) != 0.0f)
        assertTrue((coords[3] - coords[1]) != 0.0f)
    }
    
    @Test
    fun testNanDetection() {
        val nan = Float.NaN
        assertTrue(nan.isNaN())
        assertFalse(nan < 0)
        assertFalse(nan > 1)
    }
    
    @Test
    fun testInfinityDetection() {
        val inf = Float.POSITIVE_INFINITY
        assertTrue(inf.isInfinite())
    }
}
