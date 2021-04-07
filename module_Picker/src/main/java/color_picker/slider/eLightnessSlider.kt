package color_picker.slider
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.PorterDuff
import android.util.AttributeSet
import color_picker.eColorPickerView
import color_picker.builder.PaintBuilder
import com.anubis.kt_extends.eColor

/**
 * @author tamsiree
 * @date 2018/6/11 11:36:40 整合修改
 */
class eLightnessSlider : AbsCustomSlider {
    private var color = 0
    private val barPaint = PaintBuilder.newPaint().build()
    private val solid = PaintBuilder.newPaint().build()
    private val clearingStroke = PaintBuilder.newPaint().color(-0x1).xPerMode(PorterDuff.Mode.CLEAR).build()
    private var colorPicker: eColorPickerView? = null

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    override fun drawBar(barCanvas: Canvas) {
        val width = barCanvas.width
        val height = barCanvas.height
        val hsv = FloatArray(3)
        Color.colorToHSV(color, hsv)
        val l = Math.max(2, width / 256)
        var x = 0
        while (x <= width) {
            hsv[2] = x.toFloat() / (width - 1)
            barPaint.color = Color.HSVToColor(hsv)
            barCanvas.drawRect(x.toFloat(), 0f, x + l.toFloat(), height.toFloat(), barPaint)
            x += l
        }
    }

    override fun onValueChanged(value: Float) {
        if (colorPicker != null) {
            colorPicker!!.setLightness(value)
        }
    }

    override fun drawHandle(canvas: Canvas, x: Float, y: Float) {
        solid.color = eColor.eInit.eGetColorAtLightness(color, value)
        canvas.drawCircle(x, y, handleRadius.toFloat(), clearingStroke)
        canvas.drawCircle(x, y, handleRadius * 0.75f, solid)
    }

    fun setColorPicker(colorPicker: eColorPickerView?) {
        this.colorPicker = colorPicker
    }

    fun setColor(color: Int) {
        this.color = color
        value = eColor.eInit.eGetLightnessOfColor(color)
        if (bar != null) {
            updateBar()
            invalidate()
        }
    }
}
