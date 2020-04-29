package com.anubis.module_deepLearning

import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.anubis.app_opencv.R
import com.anubis.kt_extends.eLogE
import com.anubis.module_arcfaceft.eArcFaceFT
import kotlinx.android.synthetic.main.activity_camera.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import org.jetbrains.anko.custom.onUiThread

class cameraActivity : AppCompatActivity() {
    var camera: eArcFaceFT? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera)
        camera()
    }

    fun camera() {
        try {
            camera = eArcFaceFT.init(findViewById(R.id.glsurfaceView), findViewById(R.id.surfaceView), false, Color.GREEN, 2, false, 100, 0, 0, 270F, glsurfaceView)
        } catch (e: Exception) {
            e.eLogE("ArcFace")
        }
        GlobalScope.launch {
            while (isActive){
                camera?.mIsState=true
                onUiThread {  imageView.setImageBitmap(camera?.mBitmap) }
                delay(500L)
            }
        }
    }
    fun onClick(v:View){
//        onUiThread {  imageView.setImageBitmap(camera?.photoImg()) }
    }
}
