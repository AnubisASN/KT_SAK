package com.anubis.SwissArmyKnife

import android.app.Activity
import android.graphics.*
import android.os.Bundle
import android.os.Handler
import android.view.View
import com.alibaba.android.arouter.facade.annotation.Route
import com.anubis.kt_extends.eLog
import com.anubis.kt_extends.eLogE
import com.anubis.module_arcfaceft.eArcFaceFT
import com.anubis.module_detection.facemask.eFaceMask
import kotlinx.android.synthetic.main.activity_camera.*
import org.jetbrains.anko.custom.async
import org.jetbrains.anko.textColor
import java.io.IOException


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
    private var mFacemask: eFaceMask? = null
    private var frameToCropTransform: Matrix? = null
    private var croppedBitmap: Bitmap? = null
    private  var mRotate=270f
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera)
        var camera: eArcFaceFT? = null
        try {
            camera = eArcFaceFT.init(findViewById(R.id.glsurfaceView), findViewById(R.id.surfaceView), false, Color.GREEN, 2, false, 100, 1, 0,mRotate , glsurfaceView)
        } catch (e: Exception) {
            e.eLogE("ArcFace")
        }
        try {
            this.resources.assets.open("face_mask_detection.tflite").available().eLog("是否存在")
            assets.locales.forEach {
                it.eLog("assets")
            }
            mFacemask = eFaceMask(this.resources.assets)
        } catch (e: IOException) {
            e.eLogE("口罩检测初始化失败")
        }


        var bitmap: Bitmap?
        val map = BitmapFactory.decodeResource(resources, R.drawable.a)
        mRunnable = Runnable {
            eLog("${camera!!.mFaceNum}--$")
            camera.mIsState = true
            if (camera.mFaceNum != 0) {
                eLog("camear:${camera.mBitmap == null}")
                bitmap = camera.mBitmap
                imageView.setImageBitmap(bitmap)
//                val `is` = keepOutDetection(bitmap)
                async {
                    try {
                        val facemask_boxes = mFacemask?.detectFaceMasks(bitmap!!)?:return@async
                      val isMask=   mFacemask?.MasksDispose(facemask_boxes)?:return@async
                        tvHint.post {
                            tvHint.text = if (isMask) {
                                tvHint.textColor = Color.GREEN
                                "带了口罩"
                            } else {
                                tvHint.textColor = Color.RED
                                "没带口罩"
                            }
                        }
                    } catch (e: Exception) {
                        e.eLogE("口罩检测异常")
                    }
                }
            }
            Handler().postDelayed(mRunnable, 2000)
        }
        Handler().postDelayed(mRunnable, 2000)
    }
    fun onArcFtClick(v: View){
        when(v.id){
            glsurfaceView.id->{
                if (mRotate==90f)
                eArcFaceFT.setParameters(getBitmapRotate =270f )
                else
                    eArcFaceFT.setParameters(getBitmapRotate =90f )
            }
        }

    }

    private fun fillCroppedBitmap(image: Bitmap) {
        Canvas(croppedBitmap).drawBitmap(image.copy(Bitmap.Config.ARGB_8888, false), frameToCropTransform, null)
    }
}
