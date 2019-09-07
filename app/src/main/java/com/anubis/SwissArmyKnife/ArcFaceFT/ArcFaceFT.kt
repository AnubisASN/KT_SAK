package com.anubis.SwissArmyKnife

import android.app.Activity
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import com.alibaba.android.arouter.facade.annotation.Route
import com.anubis.kt_extends.eLog
import com.anubis.kt_extends.eLogE
import com.anubis.module_arcfaceft.eArcFaceFT
import kotlinx.android.synthetic.main.activity_camera.*


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
            camera = eArcFaceFT.init(findViewById(R.id.glsurfaceView), findViewById(R.id.surfaceView),false, Color.GREEN, 2, false, 100, 1,0 ,270f, imageView)
        } catch (e: Exception) {
            eLogE("ArcFace:$e")
            eLog("ArcFace$e")
        }
        mRunnable = Runnable {
            eLog("${camera!!.mFaceNum}--$")
            camera.mIsState = true
            if (camera.mFaceNum != 0) {
              eLog (  "camear:${camera.mBitmap==null}")
             imageView.setImageBitmap(camera.mBitmap)
            }
            Handler().postDelayed(mRunnable, 500)
        }
        Handler().postDelayed(mRunnable, 500)
    }
}
