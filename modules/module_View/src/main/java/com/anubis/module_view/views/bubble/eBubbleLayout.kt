package com.anubis.module_view.views.bubble

import android.content.Context
import android.util.AttributeSet
import android.widget.RelativeLayout
import com.anubis.module_view.R
import com.anubis.module_view.animator.eAbstractPathAnimator
import com.anubis.module_view.animator.eAbstractPathAnimator.Config.Companion.fromTypeArray
import com.anubis.module_view.animator.ePathAnimator

/**
 * 气泡上升控件
 */
class eBubbleLayout : RelativeLayout {
    private var mAnimator: eAbstractPathAnimator? = null

    constructor(context: Context?) : super(context) {
        init(null, 0)
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        init(attrs, 0)
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(attrs, defStyleAttr)
    }

    private fun init(attrs: AttributeSet?, defStyleAttr: Int) {
        val a = context.obtainStyledAttributes(
                attrs, R.styleable.eBubbleLayout, defStyleAttr, 0)
        mAnimator = ePathAnimator(fromTypeArray(a))
        a.recycle()
    }

    var animator: eAbstractPathAnimator?
        get() = mAnimator
        set(animator) {
            clearAnimation()
            mAnimator = animator
        }

    override fun clearAnimation() {
        for (i in 0 until childCount) {
            getChildAt(i).clearAnimation()
        }
        removeAllViews()
    }

    /**添加上升泡沫
    * @param color: Int ,颜色
    * */
    fun eAddBubble(color: Int) {
        val rxHeartView = eBubbleView(context)
        rxHeartView.setColor(color)
        mAnimator!!.start(rxHeartView, this)
    }
    /**添加上升泡沫
     * @param color: Int ,颜色
     * @param heartResId: Int,图片资源
     * @param heartBorderResId: Int,边图片资源
     * */
    fun eAddBubble(color: Int, heartResId: Int, heartBorderResId: Int) {
        val rxHeartView = eBubbleView(context)
        rxHeartView.setColorAndDrawables(color, heartResId, heartBorderResId)
        mAnimator!!.start(rxHeartView, this)
    }
}
