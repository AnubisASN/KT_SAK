package com.anubis.module_camera.Camera

import androidx.appcompat.app.AppCompatActivity
import android.graphics.*
import android.graphics.Paint.Cap
import android.graphics.Paint.Join
import android.graphics.Paint.Style
import android.os.Build
import androidx.annotation.RequiresApi
import android.util.TypedValue
import com.anubis.module_camera.Camera.customview.eBorderedText
import com.anubis.module_camera.Camera.customview.eOverlayView
import kotlinx.android.synthetic.main.fragment_camera.*
import java.util.ArrayList
import java.util.LinkedList

/**
 * 类说明：跟踪器封装
 */
@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
object eMultiBoxTracker {
    private var trackedObjects = LinkedList<Pair<Rect, String>>()
    private var boxPaint = Paint()
    private var textSizePx: Float = 0.0f
    private var borderedText: eBorderedText? = null
    private var width: Int = 0
    private var height: Int = 0
    private val TEXT_SIZE_DIP = 18f
    private var overlayView: eOverlayView? = null
    private var verticalFlip = false //垂直翻转
    private var horizontalFlip = false //垂直翻转
    /**
     * 方法说明：初始化
     * @param activity: Activity；活动
     * @param eOverlayView = activity.frame_ov_tracking；叠加试图控件
     * @param paint: Paint? = null ；画笔
     * @return eMultiBoxTracker
     */
    fun einit(activity:  AppCompatActivity, overlayView: eOverlayView = activity.frame_ov_tracking, paint: Paint? = null): eMultiBoxTracker {
        boxPaint.color = Color.GREEN
        boxPaint.style = Style.STROKE
        boxPaint.strokeWidth = 3.0f
        boxPaint.strokeCap = Cap.ROUND
        boxPaint.strokeJoin = Join.ROUND
        boxPaint.strokeMiter = 100f
        boxPaint = paint ?: boxPaint
        eMultiBoxTracker.overlayView = overlayView
        textSizePx = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, TEXT_SIZE_DIP, activity.resources.displayMetrics)
        borderedText = eBorderedText(textSizePx)
        borderedText!!.setInteriorColor(Color.GREEN)
        overlayView.addCallback {
            draw(it)
        }
        return this
    }

    /**
     * @param bitmapWidth: Int；幕布宽度
     * @param bitmapHeight: Int；目标高度
     * @param isBoxHorizontalFlip: Boolean = false ；是否水平翻转
     * @param isBoxVerticalFlip: Boolean = false；是否垂直翻转
     */
    @Synchronized
    fun setFrameConfiguration(bitmapWidth: Int, bitmapHeight: Int, isBoxHorizontalFlip: Boolean = false, isBoxVerticalFlip: Boolean = false) {
        verticalFlip = isBoxVerticalFlip
        horizontalFlip = isBoxHorizontalFlip
        width = bitmapWidth
        height = bitmapHeight
        borderedText!!.setInteriorColor(boxPaint.color)
    }


    @Synchronized
    fun eTrackResults(results: LinkedList<Pair<Rect, String>>) {
        trackedObjects = results
        overlayView!!.postInvalidate()
    }

    @Synchronized
    fun eTrackResults(results: ArrayList<Rect>) {
        trackedObjects.clear()
        results.forEach {
            trackedObjects.add(Pair(it, ""))
        }
        overlayView!!.postInvalidate()
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Synchronized
    open fun draw(canvas: Canvas) {
        val widthScale = if (width == 0) 1f else canvas.width / width.toFloat()
        val heightScale = if (height == 0) 1f else canvas.height / height.toFloat()
        for (rect in trackedObjects) {
            val tRect: Rect
            when {
                //水平翻转
                !verticalFlip && horizontalFlip ->
                    tRect = Rect((canvas.width - rect.first.right * widthScale).toInt(), (rect.first.top * heightScale).toInt(), (canvas.width - rect.first.left * widthScale).toInt(), (rect.first.bottom * heightScale).toInt())
                // 垂直翻转
                verticalFlip && !horizontalFlip ->
                    tRect = Rect((rect.first.left * widthScale).toInt(), (canvas.height - rect.first.bottom * heightScale).toInt(), (rect.first.right * widthScale).toInt(), (canvas.height - rect.first.top * heightScale).toInt())
                // 水平垂直翻转
                verticalFlip && horizontalFlip ->
                    tRect = Rect((canvas.width - rect.first.right * widthScale).toInt(), (canvas.height - rect.first.bottom * heightScale).toInt(), (canvas.width - rect.first.left * widthScale).toInt(), (canvas.height - rect.first.top * heightScale).toInt())
                //不翻转
                else ->
                    tRect = Rect((rect.first.left * widthScale).toInt(), (rect.first.top * heightScale).toInt(), (rect.first.right * widthScale).toInt(), (rect.first.bottom * heightScale).toInt())
            }
            canvas.drawRect(tRect.left.toFloat(), tRect.top.toFloat(), tRect.right.toFloat(), tRect.bottom.toFloat(), boxPaint)
            borderedText!!.drawText(canvas, tRect.left.toFloat(), tRect.top.toFloat(), rect.second, boxPaint)
        }
    }
}
