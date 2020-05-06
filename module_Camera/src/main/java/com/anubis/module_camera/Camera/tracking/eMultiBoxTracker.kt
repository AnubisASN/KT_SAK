/* Copyright 2019 The TensorFlow Authors. All Rights Reserved.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
==============================================================================*/

package com.anubis.module_camera.Camera.tracking

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Paint.Cap
import android.graphics.Paint.Join
import android.graphics.Paint.Style
import android.graphics.Rect
import android.os.Build
import android.support.annotation.RequiresApi
import android.util.TypedValue
import com.anubis.module_camera.Camera.customview.eBorderedText

import com.anubis.module_camera.Camera.customview.eOverlayView

import java.util.ArrayList
import java.util.LinkedList

/**
 * A tracker that handles non-max suppression and matches existing objects to new detections.
 */
open class eMultiBoxTracker(context: Context) {
    private var trackedObjects = LinkedList<Pair<Rect, String>>()
    private var boxPaint = Paint()
    private val textSizePx: Float
    private val borderedText: eBorderedText
    private var width: Int = 0
    private var height: Int = 0
    private var sensorOrientation: Int = 0

    init {
        boxPaint.color = Color.GREEN
        boxPaint.style = Style.STROKE
        boxPaint.strokeWidth = 2.0f
        boxPaint.strokeCap = Cap.ROUND
        boxPaint.strokeJoin = Join.ROUND
        boxPaint.strokeMiter = 100f
        textSizePx = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, TEXT_SIZE_DIP, context.resources.displayMetrics)
        borderedText = eBorderedText(textSizePx)
        borderedText.setInteriorColor(Color.GREEN)
    }

    @Synchronized
    fun setFrameConfiguration(bitmapWidth: Int, bitmapHeight: Int, sensorOrientation: Int=0, boxPaint: Paint?=null) {
        width = bitmapWidth
        height = bitmapHeight
        this.sensorOrientation = sensorOrientation
        this.boxPaint = boxPaint?:this.boxPaint
        borderedText.setInteriorColor(this.boxPaint.color)
    }


    @Synchronized
    fun trackResults(viewE: eOverlayView, results:LinkedList<Pair<Rect, String>>) {
        trackedObjects = results
        viewE.postInvalidate()
    }

    @Synchronized
    fun trackResults(viewE: eOverlayView, results: ArrayList<Rect>) {
        results.forEach {
            trackedObjects.add(Pair(it, ""))
        }
        viewE.postInvalidate()
    }
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Synchronized
    open fun draw(canvas: Canvas) {
        val widthScale=if (width==0) 1f else canvas.width/width.toFloat()
        val heightScale=if (height==0)1f else canvas.height/height.toFloat()
        for (rect in trackedObjects) {
            val cornerSize = Math.min(rect.first.width()*widthScale, rect.first.height()*heightScale) / 8.0f
            canvas.drawRoundRect(rect.first.left.toFloat()*widthScale, rect.first.top.toFloat()*heightScale, rect.first.right.toFloat()*widthScale, rect.first.bottom.toFloat()*heightScale, cornerSize, cornerSize, boxPaint)
            borderedText.drawText(canvas, rect.first.left*widthScale + cornerSize, rect.first.top.toFloat()*heightScale, rect.second, boxPaint)
        }
    }



    companion object {
        private val TEXT_SIZE_DIP = 18f
        private val MIN_SIZE = 16.0f
    }

}
