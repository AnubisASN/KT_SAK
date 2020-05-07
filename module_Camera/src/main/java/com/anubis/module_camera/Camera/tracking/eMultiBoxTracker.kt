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

import android.app.Activity
import android.graphics.*
import android.graphics.Paint.Cap
import android.graphics.Paint.Join
import android.graphics.Paint.Style
import android.os.Build
import android.support.annotation.RequiresApi
import android.util.TypedValue
import com.anubis.kt_extends.eLog
import com.anubis.module_camera.Camera.customview.eBorderedText

import com.anubis.module_camera.Camera.customview.eOverlayView
import kotlinx.android.synthetic.main.fragment_camera.*

import java.util.ArrayList
import java.util.LinkedList

/**
 * A tracker that handles non-max suppression and matches existing objects to new detections.
 */
@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
open class eMultiBoxTracker(activity: Activity,overlayView:eOverlayView=activity.frame_ov_tracking) {
    private var trackedObjects = LinkedList<Pair<Rect, String>>()
    private var boxPaint = Paint()
    private val textSizePx: Float
    private val borderedText: eBorderedText
    private var width: Int = 0
    private var height: Int = 0
    private var verticalFlip = false //垂直翻转
    private var horizontalFlip = false //垂直翻转

    init {
        boxPaint.color = Color.GREEN
        boxPaint.style = Style.STROKE
        boxPaint.strokeWidth = 2.0f
        boxPaint.strokeCap = Cap.ROUND
        boxPaint.strokeJoin = Join.ROUND
        boxPaint.strokeMiter = 100f
        textSizePx = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, TEXT_SIZE_DIP, activity.resources.displayMetrics)
        borderedText = eBorderedText(textSizePx)
        borderedText.setInteriorColor(Color.GREEN)
        overlayView.addCallback {
            draw(it)
        }
    }

    @Synchronized
    fun setFrameConfiguration(bitmapWidth: Int, bitmapHeight: Int, isBoxHorizontalFlip: Boolean = false, isBoxVerticalFlip: Boolean = false, boxPaint: Paint? = null) {
        verticalFlip = isBoxVerticalFlip
        horizontalFlip = isBoxHorizontalFlip
        width = bitmapWidth
        height = bitmapHeight
        this.boxPaint = boxPaint ?: this.boxPaint
        borderedText.setInteriorColor(this.boxPaint.color)
    }


    @Synchronized
    fun eTrackResults(viewE: eOverlayView, results: LinkedList<Pair<Rect, String>>) {
        trackedObjects = results
        viewE.postInvalidate()
    }

    @Synchronized
    fun eTrackResults(viewE: eOverlayView, results: ArrayList<Rect>) {
        results.forEach {
            trackedObjects.add(Pair(it, ""))
        }
        viewE.postInvalidate()
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Synchronized
    open fun draw(canvas: Canvas) {
        val widthScale = if (width == 0) 1f else canvas.width / width.toFloat()
        val heightScale = if (height == 0) 1f else canvas.height / height.toFloat()
        for (rect in trackedObjects) {
            val cornerSize = Math.min(rect.first.width() * widthScale, rect.first.height() * heightScale) / 8.0f
            val tRect:Rect
            when {
                //水平翻转
                !verticalFlip && horizontalFlip ->
                    tRect= Rect((canvas.width - rect.first.right * widthScale).toInt(), (rect.first.top * heightScale).toInt(), (canvas.width - rect.first.left * widthScale).toInt(), (rect.first.bottom * heightScale).toInt())
                // 垂直翻转
                verticalFlip && !horizontalFlip ->
                    tRect= Rect((rect.first.left * widthScale).toInt(), (canvas.height - rect.first.bottom * heightScale).toInt(), (rect.first.right * widthScale).toInt(), (canvas.height - rect.first.top * heightScale).toInt())
                // 水平垂直翻转
                verticalFlip && horizontalFlip ->
                   tRect= Rect((canvas.width - rect.first.right * widthScale).toInt(), (canvas.height - rect.first.bottom * heightScale).toInt(), (canvas.width - rect.first.left * widthScale).toInt(), (canvas.height - rect.first.top * heightScale).toInt())
                //不翻转
                else ->
                    tRect= Rect((rect.first.left * widthScale).toInt(), (rect.first.top * heightScale).toInt(), (rect.first.right * widthScale).toInt(), (rect.first.bottom * heightScale).toInt())
            }
            canvas.drawRoundRect(tRect.left.toFloat(), tRect.top.toFloat(), tRect.right.toFloat(), tRect.bottom.toFloat(), cornerSize, cornerSize, boxPaint)
            borderedText.drawText(canvas, tRect.left   + cornerSize, tRect.top.toFloat() , rect.second, boxPaint)
        }
    }


    companion object {
        private val TEXT_SIZE_DIP = 18f
        private val MIN_SIZE = 16.0f
    }

    /**
     * @param srcWidth 源框架的宽度。
     * @param srcHeight 源框架的高度。
     * @param dstWidth 目标框架的宽度。
     * @param dstHeight 目标框架的高度。
     * @param applyRotation 从一帧应用于另一帧的旋转量。必须是倍数 90。
     * @param maintainAspectRatio 如果为true，将确保x和y的缩放比例保持恒定，必要时裁剪图像。
     * @return 满足所需要求的转换。
     */

    fun eGetTransformationMatrix(
            srcWidth: Int,
            srcHeight: Int,
            dstWidth: Int,
            dstHeight: Int,
            applyRotation: Int,
            maintainAspectRatio: Boolean): Matrix {
        val matrix = Matrix()

        if (applyRotation != 0) {
            if (applyRotation % 90 != 0) {
                applyRotation.eLog("Rotation of %d % 90 != 0")
            }
            // Translate so center of image is at origin.
            matrix.postTranslate(-srcWidth / 2.0f, -srcHeight / 2.0f)
            // Rotate around origin.
            matrix.postRotate(applyRotation.toFloat())
        }
        // Account for the already applied rotation, if any, and then determine how
        // much scaling is needed for each axis.
        val transpose = (Math.abs(applyRotation) + 90) % 180 == 0

        val inWidth = if (transpose) srcHeight else srcWidth
        val inHeight = if (transpose) srcWidth else srcHeight

        // Apply scaling if necessary.
        if (inWidth != dstWidth || inHeight != dstHeight) {
            val scaleFactorX = dstWidth / inWidth.toFloat()
            val scaleFactorY = dstHeight / inHeight.toFloat()
            if (maintainAspectRatio) {
                // Scale by minimum factor so that dst is filled completely while
                // maintaining the aspect ratio. Some image may fall off the edge.
                val scaleFactor = Math.max(scaleFactorX, scaleFactorY)
                matrix.postScale(scaleFactor, scaleFactor)
            } else {
                // Scale exactly to fill dst from src.
                matrix.postScale(scaleFactorX, scaleFactorY)
            }
        }
        if (applyRotation != 0) {
            // Translate back from origin centered reference to destination frame.
            matrix.postTranslate(dstWidth / 2.0f, dstHeight / 2.0f)
        }
        return matrix
    }
}
