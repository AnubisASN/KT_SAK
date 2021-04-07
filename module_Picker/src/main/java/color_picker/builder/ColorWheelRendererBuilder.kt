package color_picker.builder

import color_picker.eColorPickerView
import color_picker.renderer.ColorWheelRenderer
import color_picker.renderer.FlowerColorWheelRenderer
import color_picker.renderer.SimpleColorWheelRenderer

/**
 * @author tamsiree
 * @date 2018/6/11 11:36:40 整合修改
 */
object ColorWheelRendererBuilder {
    @JvmStatic
    fun getRenderer(wheelType: eColorPickerView.WHEEL_TYPE?): ColorWheelRenderer {
        when (wheelType) {
            eColorPickerView.WHEEL_TYPE.CIRCLE -> return SimpleColorWheelRenderer()
            eColorPickerView.WHEEL_TYPE.FLOWER -> return FlowerColorWheelRenderer()
            else -> {
            }
        }
        throw IllegalArgumentException("wrong WHEEL_TYPE")
    }
}
