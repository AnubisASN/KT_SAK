package com.anubis.SwissArmyKnife

import android.app.Activity
import android.os.Bundle
import android.os.Handler
import com.alibaba.android.arouter.facade.annotation.Route
import com.anubis.SwissArmyKnife.R.id.imageView
import com.anubis.kt_extends.eLog
import com.anubis.module_arcfaceft.eArcFaceFTActivity
import kotlinx.android.synthetic.main.activity_camera.*
import org.jetbrains.anko.displayMetrics


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
class  Face: Activity(){
    private  var mRunnable:Runnable?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera)
      val camera=  eArcFaceFTActivity.init(findViewById(R.id.glsurfaceView),findViewById(R.id.surfaceView),true)
        mRunnable= Runnable {
            eLog("${camera.mFaceNum}--$")
            if (camera.mFaceNum!=0 ){
                imageView.setImageBitmap(camera.mBitmap)
            }
            Handler().postDelayed(mRunnable,1000)
            eLog("屏幕-width:"+displayMetrics.widthPixels+"height:"+displayMetrics.heightPixels+"------"+camera.mAFT_FSDKFace?.rect.toString(),"FACE")
        }
        Handler().postDelayed(mRunnable,1000)
    }
}
