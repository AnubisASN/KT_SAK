package com.anubis.module_view.animator

import android.animation.ValueAnimator
import android.graphics.Canvas
import com.anubis.module_view.views.like.tools.eShineView
import com.anubis.module_view.views.like.tools.ei.eEase
import com.anubis.module_view.views.like.tools.ei.eEasingInterpolator

/*闪烁动画*/
class eShineAnimator : ValueAnimator {
    var MAX_VALUE = 1.5f
    var ANIM_DURATION: Long = 1500
    var canvas: Canvas? = null

    constructor() {
        setFloatValues(1f, MAX_VALUE)
        duration = ANIM_DURATION
        startDelay = 200
        interpolator = eEasingInterpolator(eEase.QUART_OUT)
    }

    constructor(duration: Long, maxValue: Float, delay: Long) {
        setFloatValues(1f, maxValue)
        setDuration(duration)
        startDelay = delay
        interpolator = eEasingInterpolator(eEase.QUART_OUT)
    }

    /** 开始动画
     * @param eShineView: eShineView? ，闪烁控件
     * @param centerAnimX: Int, 闪烁X
     * @param  centerAnimY: Int, 闪烁Y
     * */
    fun eStartAnim(eShineView: eShineView?, centerAnimX: Int, centerAnimY: Int) {
        start()
    }

}
