package com.anubis.app_discern.Activity

import android.annotation.TargetApi
import android.graphics.Bitmap
import android.hardware.Camera
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Size
import android.view.View
import com.anubis.app_discern.R
import com.anubis.kt_extends.eBitmap
import com.anubis.kt_extends.eLog
import com.anubis.module_camera.Camera.eCameraActivity
import com.anubis.module_camera.Camera.tracking.eMultiBoxTracker
import com.anubis.module_detection.face_mnn.eFaceSDK
import kotlinx.android.synthetic.main.activity_discern.*
import kotlinx.coroutines.delay
import org.jetbrains.anko.imageBitmap

@TargetApi(Build.VERSION_CODES.LOLLIPOP)
class DiscernActivity : eCameraActivity() {
    override val eScreenOrientation: Int = 90
    override var eUseCamera2API: Boolean = true
    override val eDesiredPreviewFrameSize: Size = Size(800, 600)
    override val eActivityLayout: Int = R.layout.activity_discern
    override val eFrameLayoutId: Int = R.id.camera_container
    private var mTracker: eMultiBoxTracker? = null
    private val minWidth = 120
    private val minHeight = 120
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mTracker = eMultiBoxTracker(this)
        mTracker!!.setFrameConfiguration(minWidth, minHeight)
        Handler().postDelayed({ eReadyForNextImage(bitmapRotation = 90f, isFlip = true) }, 1000)
    }

    private var tBitmap: Bitmap? = null
    override fun eProcessImage(bitmap: Bitmap?) {
        if (bitmap != null) {
//            预览
            tBitmap = eBitmap.eBitmapToZoom(bitmap, minWidth, minHeight)
            val faces = eFaceSDK.eFaceDetect(tBitmap!!)
            imageView.post { imageView.imageBitmap = tBitmap }

        }
        eReadyForNextImage(bitmapRotation = 90f, isFlip = true)
    }

    fun onClick(v: View) {
    }
}
