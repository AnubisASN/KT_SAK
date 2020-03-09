package com.anubis.SwissArmyKnife

import android.app.Activity
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import com.alibaba.android.arouter.facade.annotation.Route
import com.anubis.kt_extends.eImage
import com.anubis.kt_extends.eLog
import com.anubis.kt_extends.eLogE
import com.anubis.module_arcface.eArcFace
import kotlinx.android.synthetic.main.main_preview.*
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
 *说明：  活体检测+人脸跟踪
 */
@Route(path = "/face/ArcFace")
class ArcFace : Activity() {
    private var mRunnable: Runnable? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_preview)
        var camera: eArcFace? = null
        try {
            camera = eArcFace.init(this, findViewById(R.id.texture_preview), findViewById(R.id.face_rect_view))
        } catch (e: Exception) {
            eLogE("ArcFace:$e")
            eLog("ArcFace$e")
        }
        mRunnable = Runnable {
            camera!!.mIsState = true
            if (camera.mBitmap != null) {
                imageView0.setImageBitmap(camera.mBitmap!!)
            }
            Handler().postDelayed(mRunnable, 1000)
        }
        Handler().postDelayed(mRunnable, 1000)
    }


}
