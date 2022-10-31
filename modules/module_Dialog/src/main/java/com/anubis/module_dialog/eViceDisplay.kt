package com.example.module_adcore

import android.app.Presentation
import android.content.Context
import android.hardware.display.DisplayManager
import android.os.Build
import android.os.Bundle
import android.view.Display
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import kotlin.properties.Delegates

class eViceDisplay internal constructor(){
    private var mViceDisplay:ViceDisplay?=null
    companion object{
          val eInit by lazy(LazyThreadSafetyMode.SYNCHRONIZED) { eViceDisplay() }
    }
    fun eViceShow(context: Context,  layoutId:Int,BCreate:((View)->Unit)?=null):ViceDisplay {
        eClose()
        val manager = context.getSystemService(Context.DISPLAY_SERVICE) as DisplayManager
        val displays = manager.displays
          mViceDisplay = ViceDisplay(context, displays[1],layoutId,BCreate)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mViceDisplay!!.window.setType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY)
        } else {
            mViceDisplay!!.window.setType(WindowManager.LayoutParams.TYPE_PHONE)
        }
        mViceDisplay!!.show()
        return  mViceDisplay!!
    }
    fun eClose(){
        mViceDisplay?.dismiss()
    }

    class ViceDisplay: Presentation {
        private lateinit var mContext: Context
        private var mLayoutId by Delegates.notNull<Int>()
        private var mBCreate:((View)->Unit)?=null
        constructor(outerContext: Context, display: Display, layoutId:Int=R.layout.vice_display, BCreate:((View)->Unit)?=null) : super(outerContext, display){
            mContext = outerContext
            mLayoutId=layoutId
            mBCreate=BCreate
        }

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContentView(mLayoutId)
            mBCreate?.invoke(( findViewById<ViewGroup>(android.R.id.content)).getChildAt(0))
        }
    }
}