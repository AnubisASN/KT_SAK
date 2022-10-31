package com.anubis.selfServicePayment.Utils

import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator

/**
 * Author  ： AnubisASN   on 21-1-19 下午2:52.
 * E-mail  ： anubisasn@gmail.com ( anubisasn@qq.com )
 *  Q Q： 773506352
 *命名规则定义：
 *Module :  module_'ModuleName'
 *Library :  lib_'LibraryName'
 *Package :  'PackageName'_'Module'
 *Class :  'Mark'_'Function'_'Tier'
 *Layout :  'Module'_'Function'
 *Resource :  'Module'_'ResourceName'_'Mark'
 *Layout Id :  'LoayoutName'_'Widget'_'FunctionName'
 *Class Id :  'LoayoutName'_'Widget'+'FunctionName'
 *Router :  /'Module'/'Function'
 *说明：
 */
open class eDefaultItemAnimator : SimpleItemAnimator() {
    open val time = 500L
    override fun animateAdd(holder: RecyclerView.ViewHolder?): Boolean {
//        eLogI("animateAdd")
        holder?.itemView?.let {
            it.translationX = it.width.toFloat()
            ViewCompat.animate(it)
                    .setDuration(time)
                    .alpha(1f)
                    .translationX(0f)
                    .start()
        }
        return false
    }

    override fun runPendingAnimations() {
//        eLogI("runPendingAnimations")
    }

    private var filstY: Int?=null
    override fun animateMove(
            holder: RecyclerView.ViewHolder?,
            fromX: Int,
            fromY: Int,
            toX: Int,
            toY: Int
    ): Boolean {
//        eLogI("animateMove-fromX:$fromX--fromY:$fromY--toX:$toX--toY:$toY")
        return false
    }

    override fun animateChange(
            oldHolder: RecyclerView.ViewHolder?,
            newHolder: RecyclerView.ViewHolder?,
            fromLeft: Int,
            fromTop: Int,
            toLeft: Int,
            toTop: Int
    ): Boolean {
//        eLogI("animateChange")
        return false
    }

    override fun isRunning(): Boolean {
//        eLogI("isRunning")
        return false
    }

    override fun endAnimation(item: RecyclerView.ViewHolder) {
//        eLogI("endAnimation")
    }

    override fun animateRemove(holder: RecyclerView.ViewHolder?): Boolean {
//        eLog("animateRemove")
        holder?.itemView?.let {
            ViewCompat.animate(it)
                    .setDuration(time)
                    .alpha(1f)
                    .translationXBy(it.width.toFloat())
                    .start()

        }
        return true
    }

    override fun endAnimations() {
//        eLog("endAnimations")
    }

}
