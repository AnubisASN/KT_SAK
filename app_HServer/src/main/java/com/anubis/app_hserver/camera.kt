package com.anubis.app_hserver

import android.graphics.Rect
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.anubis.kt_extends.eBitmap
import com.anubis.kt_extends.eCrashReport
import com.anubis.module_camera.Camera.eCamera
import kotlinx.android.synthetic.main.activity_camera.*
import org.jetbrains.anko.onClick


class camera : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera)
        application.eCrashReport("8f61d4025a","987")
        val mCamera=  eCamera.eInit
        textureView.onClick {
            val bitmap=textureView.bitmap
          eBitmap.eInit.eBitmapRotateFlipRect(bitmap,0f,true,rect = Rect(0,0,bitmap.width,bitmap.height),scaleX = 2,scaleY = 2).apply {
              imageView2.setImageBitmap(this)
          }
        }
        mCamera.eInitCamera(this, textureView, 0, 0f, true)
        imageView2.onClick {
            "".toInt()
        }
    }

}
