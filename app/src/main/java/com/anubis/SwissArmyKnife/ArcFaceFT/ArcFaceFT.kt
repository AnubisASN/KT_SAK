package com.anubis.SwissArmyKnife

import android.app.Activity
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import com.alibaba.android.arouter.facade.annotation.Route
import com.anubis.SwissArmyKnife.R.id.tvHint
import com.anubis.kt_extends.eImage
import com.anubis.kt_extends.eLog
import com.anubis.kt_extends.eLogE
import com.anubis.module_arcfaceft.eArcFaceFT
import kotlinx.android.synthetic.main.activity_camera.*
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.textColor


/**
 * Author  ： AnubisASN   on 2018-07-26 14:27.
 * E-mail  ： anubisasn@gmail.com ( anubisasn@qq.com )
 *  Q Q： 773506352
 *命名规则定义：
 *Module :  module_'ModuleName'
 *Library :  lib_'LibraryName'
 *Package :  'PackageName'_'Module'
 *Class :  'Mark'_'Function'_'Tier'
 *Layout :  'Module'_'Function'
 *Resource :  'Module'_'ResourceName'_'Mark'
 * /+Id :  'LoayoutName'_'Widget'+FunctionName
 *Router :  /'Module'/'Function'
 *说明： 人脸跟踪
 */
@Route(path = "/face/ArcFaceFT")
class ArcFaceFT : Activity() {
    private var mRunnable: Runnable? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera)
        var camera: eArcFaceFT? = null
        try {
            camera = eArcFaceFT.init(findViewById(R.id.glsurfaceView), findViewById(R.id.surfaceView), false, Color.GREEN, 2, false, 100, 1, 0, 270f, imageView)
        } catch (e: Exception) {
            e.eLogE("ArcFace")
        }
        var bitmap: Bitmap?
        mRunnable = Runnable {
            eLog("${camera!!.mFaceNum}--$")
            camera.mIsState = true
            if (camera.mFaceNum != 0) {
                eLog("camear:${camera.mBitmap == null}")
                bitmap = camera.mBitmap
                imageView.setImageBitmap(bitmap)
                val `is` = keepOutDetection(bitmap)
                eLog("口罩检测:$`is`")
                tvHint.post {
                    tvHint.text = if (`is` == true) {
                        tvHint.textColor = Color.GREEN
                        "带了口罩"
                    } else {
                        tvHint.textColor = Color.RED
                        "没带口罩"
                    }
                }
            }
            Handler().postDelayed(mRunnable, 500)
        }
        Handler().postDelayed(mRunnable, 500)
    }

    fun keepOutDetection(b: Bitmap?): Boolean? {
        b ?: return null
        val bitmap = eImage.eGetHandleImageNegative(b)
        val width = bitmap.width
        val height = bitmap.height
        val bmp: Bitmap?
        bmp = Bitmap.createBitmap(bitmap, width / 2, 0, 1, height, null, false)
        var color0: Int
        var color1: Int
        val px = IntArray(bmp.width * bmp.height)
        bmp.getPixels(px, 0, bmp.width, 0, 0, bmp.width, bmp.height)
        var r0: Int
        var g0: Int
        var b0: Int
        var r1: Int
        var g1: Int
        var b1: Int
        val size = px.size
        val scope = 50
        var Y = 0
        var N = 0
        for (i in 0 until size / 2) {
            color0 = px[i]
            r0 = Color.red(color0)
            g0 = Color.green(color0)
            b0 = Color.blue(color0)
            color1 = px[size - i - 1]
            r1 = Color.red(color1)
            g1 = Color.green(color1)
            b1 = Color.blue(color1)
            if (r0 in r1 - scope..r1 + scope && g0 in g1 - scope..g1 + scope && b0 in b1 - scope..b1 + scope) {
                Y++
            } else {
                N++
            }
        }
        eLog("Y:$Y--N:$N")
        return Y*2 < N
    }
}
