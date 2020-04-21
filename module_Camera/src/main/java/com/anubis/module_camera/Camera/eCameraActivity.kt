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

import android.Manifest
import android.app.Fragment
import android.content.Context
import android.content.pm.PackageManager
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
import android.os.Trace
import android.support.annotation.RequiresApi
import android.support.design.widget.BottomSheetBehavior
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.SwitchCompat
import android.support.v7.widget.Toolbar
import android.util.Size
import android.view.Surface
import android.view.View
import android.view.ViewTreeObserver
import android.view.WindowManager
import android.widget.CompoundButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.anubis.kt_extends.eBitmap.eYUV420SPToARGB8888
import com.anubis.kt_extends.eBitmap.eYUV420ToARGB8888
import com.anubis.kt_extends.eLog
import com.anubis.kt_extends.eLogE
import com.anubis.module_camera.R


@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
open class eCameraActivity : AppCompatActivity(), OnImageAvailableListener, Camera.PreviewCallback, CompoundButton.OnCheckedChangeListener {
    protected var previewWidth = 0
    protected var previewHeight = 0
    val isDebug = false
    private var handler: Handler? = null
    private var handlerThread: HandlerThread? = null
    private var useCamera2API: Boolean = false
    private var isProcessingFrame = false
    private val yuvBytes = arrayOfNulls<ByteArray>(3)
    private var rgbBytes: IntArray? = null
    protected var luminanceStride: Int = 0
    private var postInferenceCallback: Runnable? = null
    private var imageConverter: Runnable? = null


    protected var frameValueTextView: TextView? = null
    protected var cropValueTextView: TextView? = null
    protected var inferenceTimeTextView: TextView? = null
    private val apiSwitchCompat: SwitchCompat? = null

    protected val luminance: ByteArray
        get() = yuvBytes[0]!!

    protected val screenOrientation: Int
        get() {
            when (windowManager.defaultDisplay.rotation) {
                Surface.ROTATION_270 -> return 270
                Surface.ROTATION_180 -> return 180
                Surface.ROTATION_90 -> return 90
                else -> return 0
            }
        }

    open val layoutId: Int? = R.layout.fragment_camera
    open val desiredPreviewFrameSize: Size? = Size(640, 480)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(null)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        setContentView(R.layout.activity_camera)
            setFragment()
    }

    protected fun getRgbBytes(): IntArray? {
        imageConverter!!.run()
        return rgbBytes
    }

    /** Callback for android.hardware.Camera API  */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    override fun onPreviewFrame(bytes: ByteArray, camera: Camera) {
        if (isProcessingFrame) {
            eLog("Dropping frame!")
            return
        }

        try {
            // Initialize the storage bitmaps once when the resolution is known.
            if (rgbBytes == null) {
                val previewSize = camera.parameters.previewSize
                previewHeight = previewSize.height
                previewWidth = previewSize.width
                rgbBytes = IntArray(previewWidth * previewHeight)
                onPreviewSizeChosen(Size(previewSize.width, previewSize.height), 90)
            }
        } catch (e: Exception) {
            e.eLogE("Exception!")
            return
        }

        isProcessingFrame = true
        yuvBytes[0] = bytes
        luminanceStride = previewWidth

        imageConverter = Runnable { eYUV420SPToARGB8888(bytes, previewWidth, previewHeight, rgbBytes!!) }

        postInferenceCallback = Runnable {
            camera.addCallbackBuffer(bytes)
            isProcessingFrame = false
        }
        processImage()
    }

    /** Callback for Camera2 API  */
    override fun onImageAvailable(reader: ImageReader) {
        // We need wait until we have some size from onPreviewSizeChosen
        if (previewWidth == 0 || previewHeight == 0) {
            return
        }
        if (rgbBytes == null) {
            rgbBytes = IntArray(previewWidth * previewHeight)
        }
        try {
            val image = reader.acquireLatestImage() ?: return

            if (isProcessingFrame) {
                image.close()
                return
            }
            isProcessingFrame = true
            Trace.beginSection("imageAvailable")
            val planes = image.planes
            fillBytes(planes, yuvBytes)
            luminanceStride = planes[0].rowStride
            val uvRowStride = planes[1].rowStride
            val uvPixelStride = planes[1].pixelStride
            imageConverter = Runnable {
                eYUV420ToARGB8888(
                        yuvBytes[0]!!,
                        yuvBytes[1]!!,
                        yuvBytes[2]!!,
                        previewWidth,
                        previewHeight,
                        luminanceStride,
                        uvRowStride,
                        uvPixelStride,
                        rgbBytes!!)
            }

            postInferenceCallback = Runnable {
                image.close()
                isProcessingFrame = false
            }
            processImage()
        } catch (e: Exception) {
            e.eLogE("Exception!")
            Trace.endSection()
            return
        }

        Trace.endSection()
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

    @Synchronized
    protected fun runInBackground(r: Runnable) {
        if (handler != null) {
            handler!!.post(r)
        }
    }




    // Returns true if the device supports the required hardware level, or better.
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

// Fallback to camera1 API for internal cameras that don't have full support.
                // This should help with legacy situations where using the camera2 API causes
                // distorted or otherwise broken previews.
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

    val cameraId = "0"// chooseCamera()
    protected fun setFragment() {
        var fragment: Fragment? = null
        if (useCamera2API) {
            val camera2Fragment = CameraConnectionFragment.newInstance(
                    object : CameraConnectionFragment.ConnectionCallback {
                        override fun onPreviewSizeChosen(size: Size, cameraRotation: Int) {
                            previewHeight = size.height
                            previewWidth = size.width
                            this@eCameraActivity.onPreviewSizeChosen(size, 0)
                        }
                    },
                    this,
                    layoutId!!,
                    desiredPreviewFrameSize!!)

            camera2Fragment.setCamera(cameraId)
            fragment = camera2Fragment
        } else {
            eLog("layoutId:$layoutId-${layoutId==null}--$desiredPreviewFrameSize-${desiredPreviewFrameSize==null}")
            fragment = LegacyCameraConnectionFragment(this, layoutId!!,desiredPreviewFrameSize!!)
        }
        fragmentManager.beginTransaction().replace(R.id.container, fragment).commit()
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

    protected fun readyForNextImage() {
        if (postInferenceCallback != null) {
            postInferenceCallback!!.run()
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

    protected open fun processImage() {}

    protected open fun onPreviewSizeChosen(size: Size, rotation: Int){}

    protected open fun setNumThreads(numThreads: Int){}

    protected open fun setUseNNAPI(isChecked: Boolean){}

    companion object {

        private val PERMISSIONS_REQUEST = 1

        private val PERMISSION_CAMERA = Manifest.permission.CAMERA

        private fun allPermissionsGranted(grantResults: IntArray): Boolean {
            for (result in grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    return false
                }
            }
            return true
        }
    }
}
