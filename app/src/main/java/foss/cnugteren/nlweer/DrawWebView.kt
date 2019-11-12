package foss.cnugteren.nlweer

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.webkit.WebView
import androidx.core.content.ContextCompat
import kotlin.math.ceil
import kotlin.math.min


class DrawWebView : WebView {
    var fullWidth = 0
    var fullHeight = 0
    var imageWidth = 0
    var imageHeight = 0
    var lat = 0f
    var lon = 0f
    var drawCircles = false
    lateinit var imageCoordinates: Array<Float> // minLat, minLon, maxLat, maxLon
    private var scaleWidth = 1f
    private var scaleHeight = 1f
    private var minScale = 1f

    private val painter = Paint()

    constructor(context: Context) : super(context) { init() }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) { init() }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) { init() }

    private fun init() {
        painter.color = Color.parseColor("#"+Integer.toHexString(ContextCompat.getColor(context, R.color.colorHighlight)))
        painter.strokeWidth = 3f
        painter.style = Paint.Style.STROKE
        painter.isAntiAlias = true
        painter.isDither = true
        painter.textSize = 40f
    }

    fun setWidthFittingScale() {
        scaleWidth = fullWidth / imageWidth.toFloat()
        scaleHeight = fullHeight / imageHeight.toFloat()
        minScale = min(scaleWidth, scaleHeight)
        super.setInitialScale(ceil(100f * minScale).toInt())
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (drawCircles) {
            drawCircle(canvas, lat, lon)
        }
    }

    // Some test values to test the map imageCoordinates
    private fun drawTestCircles(canvas: Canvas) {
        drawCircle(canvas, 52.11f, 5.18f) // de Bilt
        drawCircle(canvas, 50.868f, 5.702f) // Maastricht
        drawCircle(canvas, 53.167f, 4.839f) // top of Texel
        drawCircle(canvas, 51.529f, 3.428f) // Bottom left: Westkapelle
        drawCircle(canvas, 53.229f, 7.191f) // Top right at the bottom of Dollart and border with DE
    }

    private fun drawCircle(canvas: Canvas, lat: Float, lon: Float) {
        val minLat = imageCoordinates[0]; val minLon = imageCoordinates[1]
        val maxLat = imageCoordinates[2]; val maxLon = imageCoordinates[3]
        if (lat < minLat || lon < minLon || lat > maxLat || lon > maxLon) {
            return // skip drawing - out of bounds
        }
        val offsetWidth = (fullWidth - imageWidth * minScale) / 2f
        val offsetHeight = (fullHeight - imageHeight * minScale) / 2f
        val percentWidth = (lon - minLon) / (maxLon - minLon)
        val percentHeight = (lat - minLat) / (maxLat - minLat)
        val xPos = imageWidth * minScale * percentWidth
        val yPos = imageHeight * minScale * (1f - percentHeight)
        canvas.drawCircle(xPos + offsetWidth, yPos + offsetHeight,40f, painter)
    }

}
