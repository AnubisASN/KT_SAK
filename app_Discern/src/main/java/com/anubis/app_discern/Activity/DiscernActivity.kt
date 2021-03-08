package com.anubis.app_discern.Activity

import android.annotation.TargetApi
import android.content.Intent
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Size
import android.view.View
import com.anubis.app_discern.R
import com.anubis.app_discern.testCameraGUI
import com.anubis.kt_extends.eAssets
import com.anubis.kt_extends.eBitmap
import com.anubis.kt_extends.eLog
import com.anubis.kt_extends.eShowTip
import com.anubis.module_camera.Camera.eCameraActivity
import com.anubis.module_camera.Camera.eMultiBoxTracker
import com.anubis.module_detection.face_mnn.eFaceSDK
import com.anubis.module_detection.face_net.FaceFeature
import com.anubis.module_detection.face_net.Facenet
import kotlinx.android.synthetic.main.activity_discern.*

@TargetApi(Build.VERSION_CODES.LOLLIPOP)
class DiscernActivity : eCameraActivity() {
    override val eScreenOrientation: Int = 270
    override var eUseCamera2API: Boolean = true
    override val eDesiredPreviewFrameSize: Size = Size(800, 600)
    override val eActivityLayout: Int = R.layout.activity_discern
    override val eFragmentLayout: Int = R.layout.fragment_camera
    override val eFrameLayoutId: Int = R.id.camera_container
    private val minWidth = 300
    private val minHeight = 350
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        eAssets.eInit.eAssetsToFile(this,"20180402-114759.pb","/sdcard/20180402-114759.pb")
    }

    private  var facenet:Facenet?=null
    private  var feature : FaceFeature?=null
    override fun onResume() {
        super.onResume()
//        人脸检测初始化
        eFaceSDK.eInit(this)
 //        追踪器初始化
        eMultiBoxTracker.einit(this)
//比例计算
        eMultiBoxTracker.setFrameConfiguration(minWidth,minHeight)
        Handler().postDelayed({ eReadyForNextImage(bitmapRotation = 90f, isFlip = true) }, 1000)
        facenet= Facenet.getInstance()
    }

    private var tBitmap: Bitmap? = null
    override fun eProcessImage(bitmap: Bitmap?) {
        if (bitmap != null) {
            //            预览
            tBitmap = eBitmap.eInit.eBitmapToZoom(bitmap, minWidth, minHeight)
            val re = eFaceSDK.eInit(this).eFaceDetect(tBitmap!!)
            eMultiBoxTracker.eTrackResults(re)
            if (feature!=null && re.size>0){
                eLog("开始识别")
                val s=facenet?.recognizeImage(tBitmap)?.compare(feature).eLog("识别结果差异")
                if (s!!<0.7){
                    imageView.post { eShowTip("验证成功：$s") }
                }
            }
            if (isRegister) {
                feature= facenet?.recognizeImage(tBitmap)
                eLog("注册成功${feature==null}")
                isRegister=false
            }
        }

        eReadyForNextImage(bitmapRotation = 90f, isFlip = true)
    }

    private  var isRegister=false
    fun onClick(v: View) {
        when (v.id) {
            button.id -> eReadyForNextImage(bitmapRotation = 90f, isFlip = true)
            qh.id -> startActivity(Intent(this, testCameraGUI::class.java))
            register.id->isRegister=true
        }
    }
}
