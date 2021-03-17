///*
// * Copyright 2019 The TensorFlow Authors. All Rights Reserved.
// *
// * Licensed under the Apache License, Version 2.0 (the "License");
// * you may not use this file except in compliance with the License.
// * You may obtain a copy of the License at
// *
// *       http://www.apache.org/licenses/LICENSE-2.0
// *
// * Unless required by applicable law or agreed to in writing, software
// * distributed under the License is distributed on an "AS IS" BASIS,
// * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// * See the License for the specific language governing permissions and
// * limitations under the License.
// */
//
//package com.anubis.module_face_recognition.test
//
//import android.content.Intent
//import android.graphics.*
//import android.media.ImageReader.OnImageAvailableListener
//import android.os.Build
//import android.os.Bundle
//import androidx.annotation.RequiresApi
//import android.view.View
//import com.anubis.kt_extends.eBitmap
//import com.anubis.kt_extends.eLog
//import com.anubis.kt_extends.eShowTip
//import com.anubis.module_camera.Camera.customview.eOverlayView
//import com.anubis.module_camera.Camera.eCameraActivity
//import com.anubis.module_camera.Camera.eMultiBoxTracker
//import  com.anubis.module_face_recognition.R
//import com.anubis.module_face_recognition.eFaceRe
//import com.anubis.module_picker.ePicker
//import kotlinx.android.synthetic.main.test.*
//import kotlinx.coroutines.Job
//import mobile.ReadFace.YMFace
//import px_core.model.MediaEntity
//import px_picker.util.logD
//import java.util.*
//
//
///**
// * An activity that uses a TensorFlowMultiBoxDetector and ObjectTracker to detect and then track
// * objects.
// */
//@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
//class testCamera : eCameraActivity(), OnImageAvailableListener, View.OnClickListener {
//    override val eScreenOrientation: Int = 90
//    override var eUseCamera2API: Boolean = true
//    override val eActivityLayout: Int = R.layout.test
//    override val eFrameLayoutId: Int = R.id.fragment
//    private var mFaceRe: eFaceRe? = null
//
//    //    private  var mFaceSDK:eFaceSDK?=null
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
////        人脸检测
////        mFaceSDK=  eFaceSDK.eInit(this)
//        mFaceRe = eFaceRe.eInit(this)
//    }
//
//    private var photo = false
//    private var isFeature = false
//    override fun onClick(v: View) {
//        when (v) {
//            bt1 -> {
//                mFaceRe?.eStartTrack().eLog("eStartTrack")
//                eReadyForNextImage(bitmapRotation = 90f)
//            }
//            bt2 -> photo = true
//            bt3 -> eShowTip("总数：${mFaceRe?.eGetAlbumSize()}")
//            bt4 -> mFaceRe?.eFaceDelete(mFaceRe?.eGetEnrolledPersonId())
//            bt5 -> mFaceRe?.eStopTrack()
//            bt6 -> isFeature = true
//            bt7 -> feature = null
//            bt8 -> ePicker.eInit.eImageStart(this)
//            bt9 -> tBitmap?.let {
//                mFaceRe?.eAnalyse(it) { it0 ->
//                    eLog("图片结果00：${it0.faceQuality}~~${it0.liveness}~~${it0.personId}~~${it0.gender}~~${it0.isHasGlass}")
//                }
//            }
//        }
//    }
//
//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        eLog("onActivityResult")
//        with(ePicker.eInit.eResult(this, requestCode, resultCode, data)?.get(0) as? MediaEntity) {
//            this ?: return Unit.eLog("==null")
//            val path = this.localPath.eLog("path")
//            BitmapFactory.decodeFile(path).apply {
//                iv.setImageBitmap(this)
//                tBitmap = this
//            }
//        }
//        super.onActivityResult(requestCode, resultCode, data)
//    }
//
//    private var mTrackerE: eMultiBoxTracker? = null
//    private var isSetTrackerE = false
//    private var feature: FloatArray? = null
//    protected var tBitmap: Bitmap? = null
//    override fun eProcessImage(bitmap: Bitmap?) {
//        bitmap ?: return
//        if (photo) {
//            photo = false
//            mFaceRe?.eFaceRegister(bitmap) { i: Int, list: List<YMFace>? ->
//                i.eLog("注册ID")
//            }
//        }
//        if (isFeature) {
//            isFeature = false
//            feature = mFaceRe?.eGetBitMapFeature(bitmap)
//        } else {
//            if (feature != null) {
//                mFaceRe?.eGetFeature()?.let { mFaceRe?.eCompareFeature(feature!!, it).eLog("eCompareFeature") }
//            }
//        }
//        val faceList = mFaceRe?.eAnalyse(bitmap) {
//            eLog("识别结果1：${it.faceQuality}~~${it.liveness}~~${it.personId}~~${it.gender}~~${it.isHasGlass}")
//        }
//
////        val faceList=mFaceSDK?.eFaceDetect(bitmap)?:return
//        if (!isSetTrackerE) {
//            isSetTrackerE = true
//            //跟踪器
//            mTrackerE = eMultiBoxTracker.einit(this@testCamera)
//
//            findViewById<eOverlayView>(R.id.frame_ov_tracking).addCallback {
//                mTrackerE!!.draw(it)
//            }
//            mTrackerE?.setFrameConfiguration(bitmap.width, bitmap.height, true)
//        }
//        val res = LinkedList<Pair<Rect, String>>()
//        faceList?.forEach {
//            res.add(Pair(mFaceRe!!.eYMFaceToRect(it.rect), ""))
//        }
//        /*追踪绘制*/
//        mTrackerE?.eTrackResults(res)
//        eReadyForNextImage(bitmapRotation = 90f)
//    }
//
//
//}
//
//
