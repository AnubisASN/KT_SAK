package com.anubis.module_dialog

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.graphics.PixelFormat
import android.os.Build
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import com.anubis.kt_extends.eLog

/**
 * Author  ： AnubisASN   on 21-3-19 下午4:25.
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
object eFloatWindow {
    private var mRootView: View? = null
    private var eParams: WindowManager.LayoutParams? = null
    var eWindowManager: WindowManager? = null
    var eXY: Pair<Int, Int>? = null

    @SuppressLint("ClickableViewAccessibility")
    fun eShowView(application: Application, layoutId: Int, touchViewId: Int? = null, block: (View, WindowManager.LayoutParams) -> Unit) {
        val inflater = LayoutInflater.from(application)
        mRootView = inflater.inflate(layoutId, null)
        with(application.resources.displayMetrics){
            eXY?:apply {
                eXY=Pair(widthPixels,heightPixels/2)
            }
        }
        eParams = WindowManager.LayoutParams()
        eWindowManager = application.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        eParams!!.type = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        else
            WindowManager.LayoutParams.TYPE_PHONE
        eParams!!.format = PixelFormat.RGBA_8888
        eParams!!.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE  //不可用  FLAG_NOT_TOUCH_MODAL//悬浮窗可用
        eParams!!.gravity = Gravity.LEFT or Gravity.TOP
        eParams!!.x = eXY?.first?:350
        eParams!!.y = eXY?.second?:350
        eParams!!.width = WindowManager.LayoutParams.WRAP_CONTENT
        eParams!!.height = WindowManager.LayoutParams.WRAP_CONTENT

        eWindowManager?.addView(mRootView, eParams)
        touchViewId?.let {
            (mRootView!!.findViewById(it) as View).setOnTouchListener { v, event ->
                eParams!!.x = event.rawX.toInt()
                eParams!!.y = event.rawY.toInt() - v.height
                eXY = Pair(eParams!!.x, eParams!!.y)
                eUpdateView()
                false
            }
        }
        block(mRootView!!, eParams!!)
    }

    fun eSetFlaView(boolean: Boolean) {
        eParams!!.flags = if (boolean)
            WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL //悬浮窗使用
        else
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE  //悬浮窗不可用
        eUpdateView()
    }

    fun eRemoveView() {
        eWindowManager?.removeView(mRootView)
    }

    fun eUpdateView() {
        eWindowManager?.updateViewLayout(mRootView, eParams)
    }
}
