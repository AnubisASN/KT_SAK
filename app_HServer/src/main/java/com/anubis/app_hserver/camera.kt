package com.anubis.app_hserver

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.graphics.ImageFormat
import android.graphics.Rect
import android.graphics.SurfaceTexture
import android.hardware.Camera
import android.hardware.camera2.*
import android.media.ImageReader
import android.os.Bundle
import android.view.Surface
import android.view.TextureView
import androidx.annotation.NonNull
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.anubis.kt_extends.eBitmap
import com.anubis.kt_extends.eLog
import com.anubis.kt_extends.eShowTip
import com.anubis.module_camera.Camera.eCamera
import kotlinx.android.synthetic.main.activity_camera.*
import org.jetbrains.anko.onClick
import java.io.File

class camera : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera)
        val mCamera=  eCamera.eInit
        textureView.onClick {
            val bitmap=textureView.bitmap
          eBitmap.eInit.eBitmapRotateFlipRect(bitmap,0f,true,rect = Rect(0,0,bitmap.width,bitmap.height),scaleX = 2,scaleY = 2).apply {
              imageView2.setImageBitmap(this)
          }

        }
        mCamera.eInitCamera(this, textureView, 0, 0f, true)
    }
}
