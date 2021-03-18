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

import android.content.Intent
import android.graphics.*
import android.hardware.Camera
import android.media.ImageReader.OnImageAvailableListener
import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import android.view.View
import com.anubis.app_discern.Activity.DiscernActivity
import com.anubis.kt_extends.eBitmap
import com.anubis.kt_extends.eLog
import com.anubis.kt_extends.eLogI
import com.anubis.module_camera.Camera.customview.eOverlayView
import com.anubis.module_camera.Camera.eCameraActivity
import com.anubis.module_camera.Camera.eMultiBoxTracker
import com.anubis.module_detection.face_mask.eFaceMask
import com.anubis.module_detection.face_mnn.eFaceSDK
import com.anubis.module_facelandmark.Landmark.MainActivity
import com.anubis.module_hwlive.eHWLive
import com.anubis.module_tensorflow.detection.eDetector
import com.anubis.module_tensorflow.detection.eDetectorGUI
import com.huawei.hms.mlsdk.livenessdetection.MLLivenessCapture
import com.huawei.hms.mlsdk.livenessdetection.MLLivenessCaptureResult
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
    override val eScreenOrientation: Int = 90
    override var eUseCamera2API: Boolean = true
    override val eActivityLayout: Int = R.layout.test_gui
    override val eFrameLayoutId: Int = R.id.test_container
    private var faceMask: eFaceMask? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.test_gui)
//        物体检测
        eDetector.eInit(this, assets).eLog("eDetector初始化")
//        人脸检测
        eFaceSDK.eInit(this)
//        口罩检测
        faceMask = eFaceMask.eInit(this)
        iv_photo.setOnClickListener(this)
        iv_image.setOnClickListener(this)
        bt_body.setOnClickListener(this)
        bt_make.setOnClickListener(this)
        bt_face.setOnClickListener(this)
        bt_net.setOnClickListener(this)
        bt_TF.setOnClickListener(this)
        bt_hwLive.setOnClickListener(this)
        bt_landmark.setOnClickListener(this)
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
            bt_hwLive.id -> {
                eHWLive.eInit(object : MLLivenessCapture.Callback {
                    override fun onFailure(p0: Int) {
                    }
                    override fun onSuccess(p0: MLLivenessCaptureResult?) {
                        tv_hint.text = p0?.isLive.toString()
                        iv_image.setImageBitmap(p0?.bitmap)
                    }
                }).eStart(this)
            }
            bt_body.id -> {
                tv_hint.text = "物体检测:"
                eReadyForNextImage(bitmapRotation = 90f)
            }
            bt_face.id -> {
                tv_hint.text = "人脸检测"
                eReadyForNextImage(bitmapRotation = 90f)
            }
//            bt_lpr.id->startActivity(Intent(this,LPRActivity::class.java))
            bt_net.id -> startActivity(Intent(this, DiscernActivity::class.java))
            bt_landmark.id -> startActivity(Intent(this, MainActivity::class.java))
            bt_TF.id -> startActivity(Intent(this, eDetectorGUI::class.java))
            test_container.id -> {
                tv_hint.text = "覆盖图层"
                eReadyForNextImage(bitmapRotation = 90f)
            }
        }
    }

    private var mTrackerE: eMultiBoxTracker? = null
    override fun eProcessImage(bitmap: Bitmap?) {
        async {
            when (typeId) {
                bt_make.id -> {
                    val box = faceMask?.eDetectFaceMasksData(bitmap!!)
                    val isMake = faceMask?.eMasksDispose(box)
                    uiThread {
                        if (isMake == null)
                            tv_hint.text = "未知"
                        else
                            tv_hint.text = if (isMake) "带了口罩" else "没带口罩"
                        if (sw_continued.isChecked)
                            onClick(bt_make)
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
                        if (sw_continued.isChecked)
                            onClick(bt_body)
                    }
                }
                bt_face.id -> {
                    eLog("onclick：bt_face")
                    iv_photo.post { iv_photo.imageBitmap = bitmap }
                    val results = eFaceSDK.eInit(this@testCameraGUI).eFaceDetect(bitmap!!)
                    results.size.eLogI("人脸检测数量")
                    iv_photo.post {
                        iv_photo.imageBitmap = eBitmap.eInit.eBitmapRect(bitmap, results, Color.RED)
                        results.forEach {
                            tv_hint.append("\n$it\n")
                        }
                        if (sw_continued.isChecked)
                            onClick(bt_face)
                    }
                }
                test_container.id -> {
                    //跟踪器
                    mTrackerE = eMultiBoxTracker.einit(this@testCameraGUI)
                    findViewById<eOverlayView>(R.id.frame_ov_tracking).addCallback {
                        mTrackerE!!.draw(it)
                    }
                    //比例计算
                    mTrackerE!!.setFrameConfiguration(bitmap!!.width, bitmap.height, true)
                    val re = eFaceSDK.eInit(this@testCameraGUI).eFaceDetect(bitmap)
                    val res = LinkedList<Pair<Rect, String>>()
                    re.forEach {
                        res.add(Pair(it, re.indexOf(it).toString()))
                    }
                    mTrackerE!!.eTrackResults(res)
                }
            }
        }
    }
}


