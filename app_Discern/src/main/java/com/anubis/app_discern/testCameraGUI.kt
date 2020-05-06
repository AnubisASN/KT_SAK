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

package com.anubis.app_discern
import android.graphics.*
import android.media.ImageReader.OnImageAvailableListener
import android.os.Build
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.view.View
import com.anubis.kt_extends.eBitmap
import com.anubis.kt_extends.eLog
import com.anubis.kt_extends.eLogI
import com.anubis.module_camera.Camera.customview.eOverlayView
import com.anubis.module_camera.Camera.eCameraActivity
import com.anubis.module_camera.Camera.tracking.eMultiBoxTracker
import com.anubis.module_detection.face_mask.eFaceMask
import com.anubis.module_detection.face_mnn.eFaceSDK
import com.anubis.module_tensorflow.detection.eDetector
import kotlinx.android.synthetic.main.test_gui.*
import kotlinx.coroutines.*
import org.jetbrains.anko.custom.async
import org.jetbrains.anko.imageBitmap
import org.jetbrains.anko.uiThread
import java.util.*


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
//        物体检测
        eDetector.eInit(this, assets).eLog("eDetector初始化")
//        人脸检测
        eFaceSDK.eInit(this)
//        口罩检测
        faceMask = eFaceMask(assets)
        iv_photo.setOnClickListener(this)
        iv_image.setOnClickListener(this)
        bt_body.setOnClickListener(this)
        bt_make.setOnClickListener(this)
        bt_face.setOnClickListener(this)
        test_container.setOnClickListener(this)
    }

    private var typeId: Int? = null
    private var jobFace: Job? = null
    override fun onClick(v: View) {
        typeId = v.id
        when (typeId) {
            iv_image.id -> {
            }
            iv_photo.id -> {
                eLog("onclick：iv_photo")
                if (jobFace == null) {
                    eLog("jobFace==null")
                    jobFace = GlobalScope.launch {
                        while (isActive) {
                            delay(100)
                            typeId = bt_face.id
                            eReadyForNextImage(bitmapRotation = 90f)
                        }
                    }
                } else {
                    eLog("jobFace.cancelAndJoin")
                    GlobalScope.launch {
                        jobFace?.cancelAndJoin()
                        jobFace = null
                    }
                }
            }
            bt_make.id -> {
                tv_hint.text = "口罩检测:"
                eReadyForNextImage(bitmapRotation = 90f)
            }
            bt_body.id -> {
                tv_hint.text = "物体检测:"
                eReadyForNextImage(bitmapRotation = 90f)
            }
            bt_face.id -> {
                tv_hint.text = "人脸检测"
                eReadyForNextImage(bitmapRotation = 90f)
            }
            test_container.id -> {
                tv_hint.text = "覆盖图层"
                eReadyForNextImage(bitmapRotation = 90f)
            }
        }
    }

    private var mTrackerE: eMultiBoxTracker? = null
    override fun processImage(bitmap: Bitmap?) {
        async {
            when (typeId) {
                bt_make.id -> {
                    iv_image.post { iv_image.imageBitmap = bitmap }
                    val box = faceMask?.detectFaceMasks(bitmap!!)
                    val isMake = faceMask?.MasksDispose(box)
                    uiThread {
                        if (isMake == null)
                            tv_hint.text = "未知"
                        else
                            tv_hint.text = if (isMake) "带了口罩" else "没带口罩"
                    }
                }
                bt_body.id -> {
                    iv_image.post { iv_image.imageBitmap = bitmap }
                    val results = eDetector.eDetector(bitmap!!)
                    results.size.eLogI("物体检测数量")
                    uiThread {
                        results.forEach {
                            tv_hint.append("\nID:${it.id}--Tilte:${it.title}--Confidence:${it.confidence}--Location:${it.location}\n")
                        }
                    }
                }
                bt_face.id -> {
                    eLog("onclick：bt_face")
                    iv_photo.post { iv_photo.imageBitmap = bitmap }
                    val results = eFaceSDK.eFaceDetect(bitmap!!)
                    results.size.eLogI("人脸检测数量")
                    iv_photo.post {
                        iv_photo.imageBitmap = eBitmap.eBitmapRect(bitmap, results)
                        results.forEach {
                            tv_hint.append("\n$it\n")
                        }
                    }
                }
                test_container.id -> {
                    mTrackerE = eMultiBoxTracker(this@testCameraGUI)
                    findViewById<eOverlayView>(R.id.frame_ov_tracking).addCallback { canvas ->
                        mTrackerE!!.draw(canvas)
                    }
                    mTrackerE!!.setFrameConfiguration(bitmap!!.width,bitmap.height)
                    val re = eFaceSDK.eFaceDetect(eBitmap.eBitmapRotateFlip(bitmap, isFlip = true)!!)
                  val res=  LinkedList<Pair<Rect,String>>()
                    re.forEach {
                        res.add(Pair(it,re.indexOf(it).toString()))
                    }
                    mTrackerE!!.trackResults( findViewById<eOverlayView>(R.id.frame_ov_tracking), res)
                }
            }
        }
    }
}


