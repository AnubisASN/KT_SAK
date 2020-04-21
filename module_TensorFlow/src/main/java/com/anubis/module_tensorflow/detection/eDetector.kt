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

package com.anubis.module_tensorflow.detection

import android.content.Context
import android.content.res.AssetManager
import android.graphics.Bitmap
import android.graphics.Matrix
import android.os.Build
import android.support.annotation.RequiresApi
import android.util.Size
import com.anubis.kt_extends.eLog
import com.anubis.kt_extends.eLogE
import com.anubis.kt_extends.eShowTip

import com.anubis.module_tensorflow.detection.env.Logger
import com.anubis.module_tensorflow.detection.customview.OverlayView
import com.anubis.module_tensorflow.detection.env.BorderedText
import com.anubis.module_tensorflow.detection.tflite.Classifier
import com.anubis.module_tensorflow.detection.tflite.TFLiteObjectDetectionAPIModel
import com.anubis.module_tensorflow.detection.tracking.MultiBoxTracker

import java.io.IOException


/**
 * An activity that uses a TensorFlowMultiBoxDetector and ObjectTracker to detect and then track
 * objects.
 */
@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
object eDetector {
    private val LOGGER = Logger()
    // Configuration values for the prepackaged SSD model.
    private val TF_OD_API_INPUT_SIZE = 300
    private val TF_OD_API_IS_QUANTIZED = true
    private val TF_OD_API_MODEL_FILE = "detect.tflite"
    private val TF_OD_API_LABELS_FILE = "file:///android_asset/labelmap.txt"
    private val MODE = DetectorMode.TF_OD_API
    // Minimum detection confidence to track a detection.
    private val MINIMUM_CONFIDENCE_TF_OD_API = 0.6f
    private val MAINTAIN_ASPECT = false
    private val DESIRED_PREVIEW_SIZE = Size(640, 480)
    private val SAVE_PREVIEW_BITMAP = false
    private val TEXT_SIZE_DIP = 10f

    internal lateinit var trackingOverlay: OverlayView
    private var sensorOrientation: Int? = null

    private var detector: Classifier? = null

    private var lastProcessingTimeMs: Long = 0
    private var rgbFrameBitmap: Bitmap? = null
    private var croppedBitmap: Bitmap? = null
    private var cropCopyBitmap: Bitmap? = null

    private var computingDetection = false

    private var timestamp: Long = 0

    private var frameToCropTransform: Matrix? = null
    private var cropToFrameTransform: Matrix? = null

    private var tracker: MultiBoxTracker? = null

    private var borderedText: BorderedText? = null

    fun eInit(context: Context,assetManager: AssetManager):Boolean{
        try {
            detector = TFLiteObjectDetectionAPIModel.create(
                    assetManager,
                    TF_OD_API_MODEL_FILE,
                    TF_OD_API_LABELS_FILE,
                    TF_OD_API_INPUT_SIZE,
                    TF_OD_API_IS_QUANTIZED)
//            cropSize = TF_OD_API_INPUT_SIZE
            return true
        } catch (e: IOException) {
            e.eLogE("Exception initializing classifier!")
            context.eShowTip("Classifier could not be initialized")
            return false
        }

    }
    fun eDetector(deBitmap: Bitmap,minConfidence:Float=MINIMUM_CONFIDENCE_TF_OD_API): ArrayList<Classifier.Recognition> {
        val results = detector!!.recognizeImage(deBitmap)
        val mResult= arrayListOf<Classifier.Recognition>()
        results.forEach {
            eLog("id:${it.id}--confidence:${it.confidence}--location:${it.location}--title:${it.title}")
            if (  it.confidence >= minConfidence) {
                mResult.add(it)
            }
        }
        return mResult
    }




    // Which detection model to use: by default uses Tensorflow Object Detection API frozen
    // checkpoints.
    private enum class DetectorMode {
        TF_OD_API
    }



}
