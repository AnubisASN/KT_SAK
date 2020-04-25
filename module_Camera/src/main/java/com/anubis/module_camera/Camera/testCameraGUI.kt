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

import android.media.ImageReader.OnImageAvailableListener
import android.os.Build
import android.os.Bundle
import android.support.annotation.RequiresApi
import com.anubis.module_camera.R
import kotlinx.android.synthetic.main.test_gui.*
import org.jetbrains.anko.imageBitmap
import org.jetbrains.anko.onClick
import android.hardware.Camera
import android.util.Size
import android.view.View
import com.anubis.kt_extends.eBitmap
import com.anubis.kt_extends.eLog


/**
 * An activity that uses a TensorFlowMultiBoxDetector and ObjectTracker to detect and then track
 * objects.
 */
@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
class testCameraGUI : eCameraActivity(), OnImageAvailableListener, View.OnClickListener {


    override var useCamera2API: Boolean = true
    override val eActivityLayout: Int = R.layout.test_gui
    override val eFrameLayoutId: Int = R.id.test_container

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        test_button.setOnClickListener(this)
        iv_photo.setOnClickListener(this)
        iv_image.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        when (v.id) {
            iv_image.id -> {
                eReadyForNextImage()
                eLog("iv_image")
            }
            test_button.id -> {
                eReadyForNextImage()
                val bitmap = eBitmap.eByteArrayToBitmp(eGetYuvBytes!!, previewWidth, previewHeight, rotate = 90f)
                iv_photo.imageBitmap = bitmap
                eLog("test_button")
            }
        }
    }

    override fun onPreviewSizeChosen(size: Size, rotation: Int) {
        eLog("size:$size")
    }

    override fun processImage(bytes: ByteArray) {
        val bitmap = eBitmap.eByteArrayToBitmp(bytes, previewWidth, previewHeight, rotate = 90f)
//        val     rgbFrameBitmap = Bitmap.createBitmap(previewWidth, previewHeight, Bitmap.Config.ARGB_8888)
//        eBitmap.eYUV420SPToARGB8888(byteArray, previewWidth, previewHeight, rgbBytes!!)
//        getRgbBytes()
//        rgbFrameBitmap!!.setPixels(rgbBytes, 0, previewWidth, 0, 0, previewWidth, previewHeight)
        iv_image.imageBitmap = bitmap
        eReadyForNextImage()

    }
}
