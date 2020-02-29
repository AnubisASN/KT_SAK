package com.anubis.module_Thermography

import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import kotlinx.android.synthetic.main.thermography.*
import android.opengl.ETC1.getHeight
import android.opengl.ETC1.getWidth
import android.support.v4.app.FragmentActivity
import android.system.Os.close
import android.util.Log
import com.anubis.kt_extends.eBitmap
import com.anubis.kt_extends.eLog
import com.anubis.module_Thermography.R.drawable.test
import com.anubis.module_Thermography.util.ImageHelper
import com.tencent.bugly.proguard.r


class MainActivity_Temp : AppCompatActivity() {
    private var bitmap: Bitmap? = null
    private var width: Int? = null
    private var height: Int? = null
    private var oldPx: IntArray? = null
    private var newPx: IntArray? = null
    private var color: Int? = null
    private var r: Int? = null
    private var g: Int? = null
    private var b: Int? = null
    private var a: Int? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.thermography)
    }



    fun temp_onClick(v: View) {
        when (v.id) {
//            底片效果
            button_0.id ->{}
//            浮雕效果
            button_1.id -> {
            }
//            老照片效果
            button_2.id -> {
            }
        }
    }

}
