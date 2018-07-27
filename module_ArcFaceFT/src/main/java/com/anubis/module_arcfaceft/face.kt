package com.anubis.module_arcfaceft

import android.app.Activity
import android.hardware.Camera
import android.os.Bundle
import android.os.Handler
import com.alibaba.android.arouter.facade.annotation.Route
import com.anubis.kt_extends.eGcBitmap
import com.anubis.kt_extends.eGetPhoneBitmap
import com.anubis.kt_extends.eLog
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
 *说明：
 */
@Route(path = "/face/arcFace")
class  face: Activity(){
    private  var mRunnable:Runnable?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera)
      val camera=  eArcFaceFTActivity.init(findViewById(R.id.glsurfaceView),findViewById(R.id.surfaceView))
        mRunnable= Runnable {
            if (camera.mFaceNum!=0 ){
                imageView.setImageBitmap(camera.mBitmap)
            }
            Handler().postDelayed(mRunnable,1000)
        }
        Handler().postDelayed(mRunnable,1000)
    }
}
