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
import com.anubis.kt_extends.eBitmap
import com.anubis.module_camera.Camera.eCameraActivity
import com.anubis.module_camera.Camera.eMultiBoxTracker
import com.anubis.module_detection.face_mnn.eFaceSDK
import kotlinx.android.synthetic.main.activity_discern.*

@TargetApi(Build.VERSION_CODES.LOLLIPOP)
class DiscernActivity : eCameraActivity() {
    override val eScreenOrientation: Int = 270
    override var eUseCamera2API: Boolean = false
    override val eDesiredPreviewFrameSize: Size = Size(800, 600)
    override val eActivityLayout: Int = R.layout.activity_discern
    override val eFragmentLayout: Int = R.layout.fragment_camera
    override val eFrameLayoutId: Int = R.id.camera_container
    private val minWidth = 300
    private val minHeight = 350
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onResume() {
        super.onResume()
//        人脸检测初始化
        eFaceSDK.eInit(this)
 //        追踪器初始化
        eMultiBoxTracker.einit(this)
//比例计算
        eMultiBoxTracker.setFrameConfiguration(minWidth,minHeight)
        Handler().postDelayed({ eReadyForNextImage(bitmapRotation = 90f, isFlip = true) }, 1000)
    }

    private var tBitmap: Bitmap? = null
    override fun eProcessImage(bitmap: Bitmap?) {
        if (bitmap != null) {
            //            预览
            tBitmap = eBitmap.eBitmapToZoom(bitmap, minWidth, minHeight)
            val re = eFaceSDK.eFaceDetect(tBitmap!!)
            eMultiBoxTracker.eTrackResults(re)
        }
        eReadyForNextImage(bitmapRotation = 90f, isFlip = true)
    }

    fun onClick(v: View) {
        when (v.id) {
            button.id -> eReadyForNextImage(bitmapRotation = 90f, isFlip = true)
            qh.id -> {
                startActivity(Intent(this, testCameraGUI::class.java))
            }
        }
    }
}
