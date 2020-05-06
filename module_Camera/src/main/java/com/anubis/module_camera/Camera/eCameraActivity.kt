/*
 * Copyright 2019 The TensorFlow Authors. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.anubis.module_camera.Camera

import android.app.Fragment
import android.content.Context
import android.graphics.Bitmap
import android.hardware.Camera
import android.hardware.camera2.CameraAccessException
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import android.media.Image.Plane
import android.media.ImageReader
import android.media.ImageReader.OnImageAvailableListener
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.support.annotation.RequiresApi
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.SwitchCompat
import android.util.Size
import android.view.Surface
import android.view.WindowManager
import android.widget.CompoundButton
import android.widget.TextView
import com.anubis.kt_extends.eBitmap
import com.anubis.kt_extends.eBitmap.eYUV420ToARGB8888
import com.anubis.kt_extends.eLog
import com.anubis.kt_extends.eLogE
import com.anubis.module_camera.R


@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
open class eCameraActivity : AppCompatActivity(), OnImageAvailableListener, Camera.PreviewCallback, CompoundButton.OnCheckedChangeListener {
    protected var ePreviewWidth = 0
    protected var ePreviewHeight = 0
    private var handler: Handler? = null
    private var handlerThread: HandlerThread? = null
    open var useCamera2API: Boolean = false
    private var isProcess = false
    private val yuvBytes = arrayOfNulls<ByteArray>(3)
    //FrameLayout
    open val eFrameLayoutId = R.id.fl_camera_ontainer
    //相机预览控件
    open val eFragmentLayout: Int = R.layout.fragment_camera
    //activity界面
    open val eActivityLayout: Int = R.layout.activity_camera
    open val eDesiredPreviewFrameSize: Size = Size(640, 480)

    private var postInferenceCallback: Runnable? = null
    protected var frameValueTextView: TextView? = null
    protected var cropValueTextView: TextView? = null
    protected var inferenceTimeTextView: TextView? = null
    private val apiSwitchCompat: SwitchCompat? = null

    //camera1  预览旋转
    open val screenOrientation: Int
        get() {
            when (windowManager.defaultDisplay.rotation) {
                Surface.ROTATION_270 -> return 270
                Surface.ROTATION_180 -> return 180
                Surface.ROTATION_90 -> return 90
                else -> return 0
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(null)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        setContentView(eActivityLayout)
        setFragment()
    }


    /** android.hardware.Camera API 的预览回调 */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    override fun onPreviewFrame(bytes: ByteArray, camera: Camera) {
        eLog("eOnPreviewFrame")
        try {
            val previewSize = camera.parameters.previewSize
            ePreviewHeight = previewSize.height
            ePreviewWidth = previewSize.width
            camera.setDisplayOrientation(screenOrientation)
            onPreviewSizeChosen(Size(ePreviewWidth, ePreviewHeight), screenOrientation)
        } catch (e: Exception) {
            e.eLogE("Exception!")
            return
        }
        postInferenceCallback = Runnable {
            camera.addCallbackBuffer(bytes)
            isProcess = true
        }
        if (!isProcess) {
            eLog("等待图片获取指令")
            return
        }
        yuvBytes[0] = bytes
        when (returnType) {

            TYPE.ByteArray -> {
                processImage(bytes)
            }
            else -> {
                processImage(eBitmap.eByteArrayToBitmp(bytes, ePreviewWidth, ePreviewHeight, rotate = bitmapRotation, isFlip = isFlip))
            }
        }
    }

    /**  Camera2 API 的预览回调 */
    private var inArrays: IntArray? = null

    override fun onImageAvailable(reader: ImageReader) {
        if (ePreviewWidth == 0 || ePreviewHeight == 0) {
            return
        }
        try {
            val image = reader.acquireLatestImage() ?: return
            postInferenceCallback = Runnable {
                isProcess = true
            }
            if (!isProcess) {
                image.close()
                return
            }
            isProcess = false
            if (inArrays == null) {
                inArrays = IntArray(ePreviewWidth * ePreviewHeight)
            }
            val planes = image.planes
            fillBytes(planes, yuvBytes)
            val yRowStride = planes[0].rowStride
            val uvRowStride = planes[1].rowStride
            val uvPixelStride = planes[1].pixelStride
            eYUV420ToARGB8888(
                    yuvBytes[0]!!,
                    yuvBytes[1]!!,
                    yuvBytes[2]!!,
                    ePreviewWidth,
                    ePreviewHeight,
                    yRowStride,
                    uvRowStride,
                    uvPixelStride,
                    inArrays!!)
            image.close()
            val bitmap = Bitmap.createBitmap(ePreviewWidth, ePreviewHeight, Bitmap.Config.ARGB_8888)
            bitmap!!.setPixels(inArrays, 0, ePreviewWidth, 0, 0, ePreviewWidth, ePreviewHeight)
            when (returnType) {
                TYPE.ByteArray -> {
                    processImage(eBitmap.eBitmapToByteArray(bitmap))
                }
                else -> {
                    processImage(eBitmap.eBitmapRotateFlip(bitmap, bitmapRotation, isFlip))
                }
            }

        } catch (e: Exception) {
            return
        }

    }

    @Synchronized
    public override fun onStart() {
        eLog("onStart $this")
        super.onStart()
    }

    @Synchronized
    public override fun onResume() {
        eLog("onResume $this")
        super.onResume()

        handlerThread = HandlerThread("inference")
        handlerThread!!.start()
        handler = Handler(handlerThread!!.looper)
    }

    @Synchronized
    public override fun onPause() {
        eLog("onPause $this")
        handlerThread!!.quitSafely()
        try {
            handlerThread!!.join()
            handlerThread = null
            handler = null
        } catch (e: InterruptedException) {
            e.eLogE("Exception!")
        }

        super.onPause()
    }

    @Synchronized
    public override fun onStop() {
        eLog("onStop $this")
        super.onStop()
    }

    @Synchronized
    public override fun onDestroy() {
        eLog("onDestroy $this")
        super.onDestroy()
    }


    // 如果设备支持所需的硬件级别或更高级别，则返回true。
    private fun isHardwareLevelSupported(
            characteristics: CameraCharacteristics, requiredLevel: Int): Boolean {
        val deviceLevel = characteristics.get(CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL)!!
        return if (deviceLevel == CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_LEGACY) {
            requiredLevel == deviceLevel
        } else requiredLevel <= deviceLevel
        // deviceLevel is not LEGACY, can use numerical sort
    }


    private fun chooseCamera(): String? {
        val manager = getSystemService(Context.CAMERA_SERVICE) as CameraManager
        try {
            for (cameraId in manager.cameraIdList) {
                val characteristics = manager.getCameraCharacteristics(cameraId)

                // We don't use a front facing camera in this sample.
                val facing = characteristics.get(CameraCharacteristics.LENS_FACING)
                if (facing != null && facing == CameraCharacteristics.LENS_FACING_FRONT) {
                    continue
                }

                val map = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP)
                        ?: continue

// 不支持所有内部摄像机的camera1 API的后备。
//                //这应有助于解决使用camera2 API造成的遗留情况
//                //预览扭曲或破坏。
                useCamera2API = facing == CameraCharacteristics.LENS_FACING_EXTERNAL || isHardwareLevelSupported(
                        characteristics, CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_FULL)
                eLog("Camera API lv2?: ${useCamera2API}")
                return cameraId
            }
        } catch (e: CameraAccessException) {
            e.eLogE("Not allowed to access camera")
        }

        return null
    }

    /*设置相机预览*/
    open val cameraId = "0"// chooseCamera()

    protected fun setFragment() {
        var fragment: Fragment? = null
        if (useCamera2API) {
            val camera2Fragment = CameraConnectionFragment.newInstance(
                    object : CameraConnectionFragment.ConnectionCallback {
                        override fun onPreviewSizeChosen(size: Size, cameraRotation: Int) {
                            ePreviewHeight = size.height
                            ePreviewWidth = size.width
                            this@eCameraActivity.onPreviewSizeChosen(size, cameraRotation)
                        }
                    },
                    this,
                    eFragmentLayout,
                    eDesiredPreviewFrameSize)

            camera2Fragment.setCamera(cameraId)
            fragment = camera2Fragment
        } else {
            fragment = LegacyCameraConnectionFragment(this, eFragmentLayout, eDesiredPreviewFrameSize)
        }
        fragmentManager.beginTransaction().replace(eFrameLayoutId, fragment).commit()
    }

    protected fun fillBytes(planes: Array<Plane>, yuvBytes: Array<ByteArray?>) {
        // Because of the variable row stride it's not possible to know in
        // advance the actual necessary dimensions of the yuv planes.
        for (i in planes.indices) {
            val buffer = planes[i].buffer
            if (yuvBytes[i] == null) {
                eLog("Initializing buffer $i at size ${buffer.capacity()}")
                yuvBytes[i] = ByteArray(buffer.capacity())
            }
            buffer.get(yuvBytes[i])
        }
    }

    private var returnType: Any = TYPE.Bitmap
    private var bitmapRotation = 0f
    private var isFlip = false
    //camera1 bitmapRotation  拍照旋转
    protected fun eReadyForNextImage(type: TYPE = TYPE.Bitmap, bitmapRotation: Float = 0f, isFlip: Boolean = false) {
        returnType = type
        this.isFlip = isFlip
        this.bitmapRotation = bitmapRotation
        if (postInferenceCallback != null) {
            postInferenceCallback!!.run()
        }
    }

    protected open fun processImage(bitmap: Bitmap?) {}
    protected open fun processImage(byteArray: ByteArray?) {}
    protected open fun onPreviewSizeChosen(size: Size, rotation: Int) {}

    companion object {
        enum class TYPE { Bitmap, ByteArray }
    }


    /** ------------------------------------- */

    @Synchronized
    protected fun runInBackground(r: Runnable) {
        if (handler != null) {
            handler!!.post(r)
        }
    }

    override fun onCheckedChanged(buttonView: CompoundButton, isChecked: Boolean) {
        setUseNNAPI(isChecked)
        if (isChecked)
            apiSwitchCompat!!.text = "NNAPI"
        else
            apiSwitchCompat!!.text = "TFLITE"
    }

    protected fun showFrameInfo(frameInfo: String) {
        frameValueTextView!!.text = frameInfo
    }

    protected fun showCropInfo(cropInfo: String) {
        cropValueTextView!!.text = cropInfo
    }

    protected fun showInference(inferenceTime: String) {
        inferenceTimeTextView!!.text = inferenceTime
    }


    protected open fun setNumThreads(numThreads: Int) {}

    protected open fun setUseNNAPI(isChecked: Boolean) {}


}
