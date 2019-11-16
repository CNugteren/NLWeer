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
    lateinit var imageCoordinates: Array<Float> // minLat, minLon, maxLat, maxLon, width-offset-based-on-height, height-offset-based-on-width
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
        drawCircle(canvas, 51.3694f,3.3659f) // SW border with Belgium at Cadzand
        drawCircle(canvas, 50.7543f,6.0209f) // SE border with Belgium & Germany at Vaalserberg
        drawCircle(canvas, 51.8236f,5.9449f) // E border with Germany at Nijmegen
        drawCircle(canvas, 53.2360f,7.2098f) // NE border with Germany at the bottom of the Dollart
        drawCircle(canvas, 53.4390f,5.5466f) // N border with the sea at the eastern most point of Terschelling
        drawCircle(canvas, 53.1851f,4.8522f) // NW border with the sea at the northern most point of Texel
        drawCircle(canvas, 51.9841f,4.0964f) // S border with the sea at the extreme point at Hoek van Holland
    }

    private fun drawCircle(canvas: Canvas, lat: Float, lon: Float) {
        if (imageCoordinates.size < 6) { return } // invalid data provided

        val minLat = imageCoordinates[0]; val minLon = imageCoordinates[1]
        val maxLat = imageCoordinates[2]; val maxLon = imageCoordinates[3]

        // Percentage of the image computation
        var percentWidth = (lon - minLon) / (maxLon - minLon)
        var percentHeight = (lat - minLat) / (maxLat - minLat)
        percentWidth += percentHeight * imageCoordinates[4]
        percentHeight += percentWidth * imageCoordinates[5]
        if (percentHeight < 0 || percentWidth < 0 || percentHeight > 1 || percentWidth > 1) {
            return // skip drawing - out of bounds
        }

        // Pixel space
        val xPos = imageWidth * minScale * percentWidth
        val yPos = imageHeight * minScale * (1f - percentHeight)
        val offsetWidth = (fullWidth - imageWidth * minScale) / 2f
        val offsetHeight = (fullHeight - imageHeight * minScale) / 2f
        canvas.drawCircle(xPos + offsetWidth, yPos + offsetHeight,25f, painter)
        canvas.drawCircle(xPos + offsetWidth, yPos + offsetHeight,3f, painter)
    }

}
