package com.anubis.module_view.animator

import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import java.util.concurrent.atomic.AtomicInteger

/**
 * 路径动画
 * @param config:  Config ,信息设定
 */
class ePathAnimator(config:  Config) : eAbstractPathAnimator(config) {
    private val mCounter = AtomicInteger(0)
    private val mHandler: Handler = Handler(Looper.getMainLooper())
    override fun start(child: View, parent: ViewGroup?) {
        parent!!.addView(child, ViewGroup.LayoutParams(mConfig.heartWidth, mConfig.heartHeight))
        val anim = eFloatAnimation(eCreatePath(mCounter, parent, 2), randomRotation(), parent, child)
        anim.duration = mConfig.animDuration.toLong()
        anim.interpolator = LinearInterpolator()
        anim.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationEnd(animation: Animation) {
                mHandler.post { parent.removeView(child) }
                mCounter.decrementAndGet()
            }

            override fun onAnimationRepeat(animation: Animation) {}
            override fun onAnimationStart(animation: Animation) {
                mCounter.incrementAndGet()
            }
        })
        anim.interpolator = LinearInterpolator()
        child.startAnimation(anim)
    }

}
