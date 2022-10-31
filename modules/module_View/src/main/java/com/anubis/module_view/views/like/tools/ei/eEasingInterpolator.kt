package com.anubis.module_view.views.like.tools.ei

import android.animation.TimeInterpolator

/**
 * @author tamsiree
 * The Easing class provides a collection of ease functions. It does not use the standard 4 param
 * ease signature. Instead it uses a single param which indicates the current linear ratio (0 to 1) of the tween.
 */
class eEasingInterpolator(val ease: eEase) : TimeInterpolator {
    override fun getInterpolation(input: Float): Float {
        return eEasingProvider.get(ease, input)
    }

}
