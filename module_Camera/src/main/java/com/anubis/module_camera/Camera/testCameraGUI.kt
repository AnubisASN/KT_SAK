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
import android.util.Size
import android.view.View
import com.anubis.kt_extends.eLog
import com.anubis.kt_extends.eLogI
import com.anubis.module_detection.facemask.eFaceMask
import com.anubis.module_tensorflow.detection.eDetector
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.jetbrains.anko.image


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
    private var faceMask: eFaceMask? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        eDetector.eInit(this, assets).eLog("eDetector初始化：")
        faceMask = eFaceMask(assets)
        bt_body.setOnClickListener(this)
        iv_photo.setOnClickListener(this)
        bt_make.setOnClickListener(this)
        iv_image.setOnClickListener(this)
    }

    private var typeId: Int? = null
    override fun onClick(v: View) {
        typeId = v.id
        when (typeId) {
            iv_image.id -> {
//                eLogI("iv_image:${tBitmap!!.width*tBitmap!!.height}")
//                tBitmap?:return
//                eDetector.eDetector(tBitmap!!).eLogI("eDetector")
//                eLogI("iv_image  end")
            }
            bt_make.id -> {
                tv_hint.text = "口罩检测"
                eReadyForNextImage(bitmapRotation = 90f)
            }
            bt_body.id -> {
                tv_hint.text = "物体检测"
                eReadyForNextImage(bitmapRotation = 90f)
                eLog("test_button")
            }
        }
    }
private  var tBitmap:Bitmap?=null
    override fun processImage(bitmap: Bitmap?) {
        tBitmap=bitmap
        iv_image.imageBitmap = bitmap
        when (typeId) {
            bt_make.id -> {
                val box = faceMask?.detectFaceMasks(bitmap!!)
                val isMake = faceMask?.MasksDispose(box)
                if (isMake == null) {
                    tv_hint.text = "未知"
                } else
                    tv_hint.text = if (isMake) "带了口罩" else "没带口罩"
            }
            bt_body.id -> {
                    val result = eDetector.eDetector(bitmap!!)
                result.size.eLogI("result数量")
                    result.forEach {
                        tv_hint.post { tv_hint.append("ID:${it.id}--Tilte:${it.title}--Confidence:${it.confidence}--Location:${it.location}/n") }
                    }
            }

        }
    }
}
