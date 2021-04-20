package com.anubis.module_camera.Camera

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.graphics.ImageFormat
import android.graphics.SurfaceTexture
import android.hardware.Camera
import android.hardware.camera2.*
import android.hardware.camera2.CameraCaptureSession.CaptureCallback
import android.media.Image
import android.media.ImageReader
import android.util.Log
import android.view.Surface
import android.view.TextureView
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.core.app.ActivityCompat
import com.anubis.kt_extends.*
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.nio.ByteBuffer


/**
 * Author  ： AnubisASN   on 21-4-14 下午2:09.
 * E-mail  ： anubisasn@gmail.com ( anubisasn@qq.com )
 *  Q Q： 773506352
 *命名规则定义：
 *Module :  module_'ModuleName'
 *Library :  lib_'LibraryName'
 *Package :  'PackageName'_'Module'
 *Class :  'Mark'_'Function'_'Tier'
 *Layout :  'Module'_'Function'
 *Resource :  'Module'_'ResourceName'_'Mark'
 *Layout Id :  'LoayoutName'_'Widget'_'FunctionName'
 *Class Id :  'LoayoutName'_'Widget'+'FunctionName'
 *Router :  /'Module'/'Function'
 *说明：
 */
class eCamera private constructor() {
    private var saveFile: File? = null
    private var mImageReader: ImageReader? = null
    private var mCameraCaptureSession: CameraCaptureSession? = null
    private var mCameraDevice: CameraDevice? = null

    companion object {
        val eInit by lazy(LazyThreadSafetyMode.SYNCHRONIZED) { eCamera() }
    }
    /*开始相机预览*/
    fun eInitCamera(context: Context, textureView: TextureView, cameraId: Int? = null, rotation: Float = 0f, isFlip: Boolean = false, takePicBlock: ((ImageReader) -> Unit)? = null) {
        var mCameraId = 0
        try {
            mCameraId = cameraId ?: eGetCameraIds()[0].first
        } catch (e: Exception) {
        }
        textureView.rotation = rotation
        if (isFlip)
            textureView.rotationY = 180f
        //  预览用的surface
        textureView.surfaceTextureListener = object : TextureView.SurfaceTextureListener {
            @SuppressLint("MissingPermission")
            override fun onSurfaceTextureAvailable(surfaceTexture: SurfaceTexture, arg1: Int, arg2: Int) {
                val mPreviewSurface = Surface(surfaceTexture)
                val manager: CameraManager = context.getSystemService(Context.CAMERA_SERVICE) as CameraManager
                try {
                    //  权限检查
                    if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CAMERA) !== PackageManager.PERMISSION_GRANTED) {
                        return context.eShowTip("请开启相机权限")
                    }
                    //  开启相机
                    manager.openCamera(mCameraId.toString(), object : CameraDevice.StateCallback() {
                        override fun onOpened(@NonNull cameraDevice: CameraDevice) {
                            mCameraDevice = cameraDevice
                            //  相机已经打开回调
                            //  ImageReader可以直接获取屏幕渲染数据
                            mImageReader = ImageReader.newInstance(textureView.width, textureView.height, ImageFormat.JPEG, 2)
                            //  设置图片拍摄回调
                            takePicBlock?.let {
                                mImageReader?.setOnImageAvailableListener({ reader ->
                                    it(reader)
                                }, null)
                            }
                            try {
                                //  创建一个 创建捕获会话
                                cameraDevice.createCaptureSession(listOf(mPreviewSurface, mImageReader?.surface), object : CameraCaptureSession.StateCallback() {
                                    override fun onConfigured(@NonNull cameraCaptureSession: CameraCaptureSession) {
                                        mCameraCaptureSession = cameraCaptureSession
                                        try {
                                            val builder: CaptureRequest.Builder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW)
                                            builder.addTarget(mPreviewSurface)
                                            cameraCaptureSession.setRepeatingRequest(builder.build(), null, null)

                                        } catch (e1: CameraAccessException) {
                                            e1.printStackTrace()
                                            context.eShowTip("CameraAccessException 异常：" + e1)
                                        }
                                    }

                                    override fun onConfigureFailed(@NonNull cameraDevice: CameraCaptureSession?) {
                                        eLog("onConfigureFailed 异常")
                                    }
                                }, null)
                            } catch (e: CameraAccessException) {
                                e.printStackTrace()
                                context.eShowTip("CameraAccessException 异常：" + e)
                            }
                        }

                        override fun onError(@NonNull cameraDevice: CameraDevice?, arg1: Int) {
                            context.eShowTip("相机错误")
                        }

                        override fun onDisconnected(@NonNull cameraDevice: CameraDevice?) {
                            context.eShowTip("相机断开连接")
                        }
                    }, null)
                } catch (e: CameraAccessException) {
                    e.printStackTrace()
                    context.eShowTip("CameraAccessException 异常：$e")
                }
            }

            override fun onSurfaceTextureDestroyed(cameraDevice: SurfaceTexture?): Boolean {
                eLog("onSurfaceTextureDestroyed")
                return false
            }

            override fun onSurfaceTextureSizeChanged(cameraDevice: SurfaceTexture?, arg1: Int, arg2: Int) {
                eLog("onSurfaceTextureSizeChanged")
            }

            override fun onSurfaceTextureUpdated(cameraDevice: SurfaceTexture?) {
            }
        }
    }
    /*拍照*/
    fun eTakePicture(file: File, success: ((String) -> Unit)? = null) {
        saveFile = file
        try {
            mCameraDevice ?: return
            val captureBuilder: CaptureRequest.Builder = mCameraDevice!!.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE)
            //captureBuilder.addTarget(mPreviewSurface);
            captureBuilder.addTarget(mImageReader!!.surface)
            captureBuilder.set(CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE)
            mCameraCaptureSession?.capture(captureBuilder.build(), object : CaptureCallback() {
                override fun onCaptureCompleted(session: CameraCaptureSession, request: CaptureRequest, result: TotalCaptureResult) {
                    super.onCaptureCompleted(session, request, result)
                    success?.let { it(file.path) }
                }
            }, null)
        } catch (e: java.lang.Exception) {
            eLogE("发生错误：" + e.message)
        }
    }

    /* Image 转文件 与拍照关联*/
    fun eImageToFile(image: Image, file: File?=saveFile): String? {
        file?:return null.apply { eLogE("file==null") }
        eImage.eInit.eImageToFile(image,file)
        saveFile=null
        return null
    }

    /*获取相机数量，ID*/
    fun eGetCameraIds(): List<Pair<Int, String>> {
        val cameras = arrayListOf<Pair<Int, String>>()
        val numberOfCameras: Int = Camera.getNumberOfCameras().eLog("getNumberOfCameras")
        for (i in 0..numberOfCameras) {
            val info: Camera.CameraInfo = Camera.CameraInfo()
            try {
                Camera.getCameraInfo(i, info)
                cameras.add(Pair(i, info.facing.toString()))
            } catch (e: Exception) {
                eLogE("eGetCameraIds：$e")
            }
        }
        return cameras
    }

    /*相机关闭*/
    fun eClose(){
        mCameraCaptureSession?.close()
        mImageReader?.close()
        mCameraDevice?.close()
    }
}
