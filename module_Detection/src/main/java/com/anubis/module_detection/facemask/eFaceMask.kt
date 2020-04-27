package com.anubis.module_detection.facemask

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.content.res.AssetManager
import android.graphics.Bitmap
import android.os.Build
import android.util.Log
import com.anubis.kt_extends.eLog
import com.anubis.kt_extends.eLogE


import com.anubis.module_detection.util.ImageUtils

import org.tensorflow.lite.Interpreter

import java.io.IOException
import java.util.HashMap
import java.util.Vector

/**
 * 口罩检测
 */
class eFaceMask
constructor(assetManager: AssetManager) {

    private var anchors: Array<FloatArray>? = null

    private var interpreter: Interpreter?=null

    internal var TAG = "TAG"

    init {
        val options = Interpreter.Options()
        options.setNumThreads(4)
        try {
            interpreter = Interpreter(ImageUtils.loadModelFile(assetManager, MODEL_FILE), options)
        } catch (e: Exception) {
            eLog("interpreter:$e")
            if (e.toString().contains("it is probably compressed")){
                eLogE("请在build.gradle中的android{}块内添加：\n   " +
                        "  aaptOptions {\n" +
                        "        noCompress \"tflite\"\n" +
                        "        noCompress \"lite\"\n" +
                        "    }")
            }
        }
        generateAnchors()
    }

    @SuppressLint("UseSparseArrays")
    fun detectFaceMasks(bitmap: Bitmap): Vector<Box>? {
        interpreter?:return null
       val head=  Bitmap.createScaledBitmap(bitmap,INPUT_IMAGE_SIZE,INPUT_IMAGE_SIZE, true);
        val len = 5972
        val ddims = intArrayOf(1, INPUT_IMAGE_SIZE, INPUT_IMAGE_SIZE, 3)
        val datasets = Array(ddims[0]) { Array(ddims[1]) { Array(ddims[2]) { FloatArray(ddims[3]) } } }
        datasets[0] = ImageUtils.normalizeImage(head)
        val loc = Array(1) { Array(len) { FloatArray(4) } }
        val cls = Array(1) { Array(len) { FloatArray(2) } }
        Log.i(TAG, "detectFaceMasks: " + arrayOf<Any>(datasets).size)
        val outputs:HashMap<Int,Any>?= HashMap()
        outputs!!.put(interpreter!!.getOutputIndex("loc_branch_concat_1/concat"),loc)
        outputs.put(interpreter!!.getOutputIndex("cls_branch_concat_1/concat"),cls)
        interpreter!!.runForMultipleInputsOutputs(arrayOf<Any>(datasets), outputs)

        //先通过score筛选Box，减少后续计算
        val filteredBoxes = Vector<Box>()
        for (i in 0 .. len-1) {
            var idxCls = -1
            if (cls[0][i][0] > cls[0][i][1]) {
                idxCls = 0
            } else {
                idxCls = 1
            }

            if (cls[0][i][idxCls] > CONF_THRESHOLD) {
                val box = Box()
                // core
                box.score = cls[0][i][idxCls]
                // box
                box.box[0] = loc[0][i][0]
                box.box[1] = loc[0][i][1]
                box.box[2] = loc[0][i][2]
                box.box[3] = loc[0][i][3]

                box.cls = idxCls

                if (idxCls == 0) {
                    box.title = "有口罩"
                } else {
                    box.title = "无口罩"
                }

                box.index = i

                filteredBoxes.add(box)
            }
        }
        //        Log.i(TAG, "detectFaceMasks: "+filteredBoxes.get(0).deleted);

        //解码Box参数
        decodeBBox(filteredBoxes)

        //NMS
        nms(filteredBoxes, IOU_THRESHOLD, "Union")

        //Log.i(LOGGING_TAG, String.format("Detected: %d", filteredBoxes.size()));

        return filteredBoxes
    }


    fun MasksDispose(box: Vector<Box>?): Boolean? {
        box ?: return null
        if (box.size<4)
            return null
        box.sortByDescending { it.score }
        if (box.first().cls==0){
            if (box[0].score>0.85 && box[1].score>0.85)
                return   true
            return  null
        }else{
            if (box.first().score>0.75)
                return   false
            return   null
        }

    }

    private fun generateAnchors() {
        var anchorTotal = 0
        for (i in 0..4) {
            anchorTotal += feature_map_sizes[i] * feature_map_sizes[i]
        }
        anchorTotal *= 4

        anchors = Array(anchorTotal) { FloatArray(4) }

        var index = 0
        for (i in 0..4) {
            val center = FloatArray(feature_map_sizes[i])

            for (j in 0 .. feature_map_sizes[i]-1) {
                center[j] = 1.0f * (-feature_map_sizes[i] / 2 + j).toFloat() / feature_map_sizes[i].toFloat() + 0.5f
            }
            val offset = Array(4) { FloatArray(4) }
            for (j in 0..1) {
                val ratio = anchor_ratios[0]
                val width = anchor_sizes[i][j] * Math.sqrt(ratio.toDouble()).toFloat()
                val height = anchor_sizes[i][j] / Math.sqrt(ratio.toDouble()).toFloat()
                offset[j] = floatArrayOf(-width / 2.0f, -height / 2.0f, width / 2.0f, height / 2.0f)
            }
            for (j in 0..1) {
                val s1 = anchor_sizes[i][0]
                val ratio = anchor_ratios[1 + j]
                val width = anchor_sizes[i][j] * Math.sqrt(ratio.toDouble()).toFloat()
                val height = anchor_sizes[i][j] / Math.sqrt(ratio.toDouble()).toFloat()
                offset[2 + j] = floatArrayOf(-width / 2.0f, -height / 2.0f, width / 2.0f, height / 2.0f)
            }
            for (y in 0 .. feature_map_sizes[i]-1) {
                for (x in 0 .. feature_map_sizes[i]-1) {
                    for (j in 0..3) {
                        anchors!![index] = floatArrayOf(center[x] + offset[j][0], center[y] + offset[j][1], center[x] + offset[j][2], center[y] + offset[j][3])
                        index++
                    }
                }
            }
        }
    }

    private fun decodeBBox(boxes: Vector<Box>) {
        Log.i(TAG, "decodeBBox: " + boxes.size)
        for (i in boxes.indices) {
            val box = boxes[i]

            val anchor_center_x = (anchors!![box.index][0] + anchors!![box.index][2]) / 2
            val anchor_center_y = (anchors!![box.index][1] + anchors!![box.index][3]) / 2
            val anchor_w = anchors!![box.index][2] - anchors!![box.index][0]
            val anchor_h = anchors!![box.index][3] - anchors!![box.index][1]

            val predict_center_x = box.box[0] * 0.1f * anchor_w + anchor_center_x
            val predict_center_y = box.box[1] * 0.1f * anchor_h + anchor_center_y
            val predict_w = Math.exp(box.box[2].toDouble() * 0.2).toFloat() * anchor_w
            val predict_h = Math.exp(box.box[3].toDouble() * 0.2).toFloat() * anchor_h

            box.box[0] = predict_center_x - predict_w / 2
            box.box[1] = predict_center_y - predict_h / 2
            box.box[2] = predict_center_x + predict_w / 2
            box.box[3] = predict_center_y + predict_h / 2
        }
    }

    @TargetApi(Build.VERSION_CODES.N)
    private fun nms(boxes: Vector<Box>, threshold: Float, method: String) {
        // NMS.两两比对
        // int delete_cnt = 0;
        boxes.forEach {
            if (!it.deleted) {
                // score<0表示当前矩形框被删除
                for (j in boxes.indexOf(it) + 1 .. boxes.size-1) {
                    val box2 = boxes[j]
                    if (!box2.deleted && box2.cls == it.cls) {
                        val x1 = Math.max(it.box[0], box2.box[0])
                        val y1 = Math.max(it.box[1], box2.box[1])
                        val x2 = Math.min(it.box[2], box2.box[2])
                        val y2 = Math.min(it.box[3], box2.box[3])
                        if (x2 < x1 || y2 < y1) continue
                        val areaIoU = (x2 - x1 + 1) * (y2 - y1 + 1)
                        var iou = 0f
                        if (method == "Union")
                            iou = 1.0f * areaIoU / (it.area() + box2.area() - areaIoU)
                        else if (method == "Min")
                            iou = 1.0f * areaIoU / Math.min(it.area(), box2.area())
                        if (iou >= threshold) { // 删除prob小的那个框
                            if (it.score > box2.score)
                                box2.deleted = true
                            else
                                it.deleted = true
                        }
                    }
                }
            }
        }
    }

    companion object {
        private val MODEL_FILE = "face_mask_detection.tflite"

        val INPUT_IMAGE_SIZE = 260 // 需要feed数据的placeholder的图片宽高
        val CONF_THRESHOLD = 0.5f // 置信度阈值
        val IOU_THRESHOLD = 0.4f // IoU阈值

        private val feature_map_sizes = intArrayOf(33, 17, 9, 5, 3)
        private val anchor_sizes = arrayOf(floatArrayOf(0.04f, 0.056f), floatArrayOf(0.08f, 0.11f), floatArrayOf(0.16f, 0.22f), floatArrayOf(0.32f, 0.45f), floatArrayOf(0.64f, 0.72f))
        private val anchor_ratios = floatArrayOf(1.0f, 0.62f, 0.42f)
    }
}



