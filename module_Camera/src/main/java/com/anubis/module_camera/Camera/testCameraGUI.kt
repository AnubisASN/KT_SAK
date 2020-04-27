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

import android.graphics.Bitmap
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
import com.tencent.bugly.proguard.t


/**
 * An activity that uses a TensorFlowMultiBoxDetector and ObjectTracker to detect and then track
 * objects.
 */
@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
class testCameraGUI : eCameraActivity(), OnImageAvailableListener, View.OnClickListener {

    override val screenOrientation: Int = 90
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
                eLog("iv_image")
            }
            test_button.id -> {
                eReadyForNextImage(bitmapRotation = 90f,isFlip = true)
                eLog("test_button")
            }
        }
    }

    override fun onPreviewSizeChosen(size: Size, rotation: Int) {
        eLog("size:$size--$rotation")
    }

    override fun   processImage(bitmap: Bitmap?) {
        iv_image.post {iv_image .imageBitmap = bitmap }
//        eReadyForNextImage(bitmapRotation = 90f)
    }
}
