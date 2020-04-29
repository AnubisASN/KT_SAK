package com.anubis.module_detection.util

import android.content.Context
import android.content.res.AssetManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.graphics.Rect
import android.media.Image


import com.anubis.module_detection.face_mask.Box

import java.io.FileInputStream
import java.io.IOException
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel

import java.lang.Math.max
import java.lang.Math.min

object ImageUtils {
    // This value is 2 ^ 18 - 1, and is used to clamp the RGB values before their ranges
    // are normalized to eight bits.
    internal val kMaxChannelValue = 262143

    fun convertYUVToARGB(image: Image, previewWidth: Int, previewHeight: Int): IntArray {
        val planes = image.planes
        val yuvBytes = fillBytes(planes)
        return ImageUtils.convertYUV420ToARGB8888(yuvBytes[0]!!, yuvBytes[1]!!, yuvBytes[2]!!, previewWidth,
                previewHeight, planes[0].rowStride, planes[1].rowStride, planes[1].pixelStride)
    }

    private fun fillBytes(planes: Array<Image.Plane>): Array<ByteArray?> {
        val yuvBytes = arrayOfNulls<ByteArray>(3)
        for (i in planes.indices) {
            val buffer = planes[i].buffer
            if (yuvBytes[i] == null) {
                yuvBytes[i] = ByteArray(buffer.capacity())
            }
            buffer.get(yuvBytes[i])
        }

        return yuvBytes
    }

    private fun convertYUV420ToARGB8888(yData: ByteArray, uData: ByteArray, vData: ByteArray, width: Int, height: Int,
                                        yRowStride: Int, uvRowStride: Int, uvPixelStride: Int): IntArray {
        val out = IntArray(width * height)
        var i = 0
        for (y in 0 until height) {
            val pY = yRowStride * y
            val uv_row_start = uvRowStride * (y shr 1)

            for (x in 0 until width) {
                val uv_offset = (x shr 1) * uvPixelStride
                out[i++] = YUV2RGB(
                        convertByteToInt(yData, pY + x),
                        convertByteToInt(uData, uv_row_start + uv_offset),
                        convertByteToInt(vData, uv_row_start + uv_offset))
            }
        }

        return out
    }

    private fun convertByteToInt(arr: ByteArray, pos: Int): Int {
        return arr[pos].toInt() and 0xFF
    }

    private fun YUV2RGB(nY: Int, nU: Int, nV: Int): Int {
        var nY = nY
        var nU = nU
        var nV = nV
        nY -= 16
        nU -= 128
        nV -= 128
        if (nY < 0) nY = 0

        var nR = 1192 * nY + 1634 * nV
        var nG = 1192 * nY - 833 * nV - 400 * nU
        var nB = 1192 * nY + 2066 * nU

        nR = Math.min(kMaxChannelValue, Math.max(0, nR))
        nG = Math.min(kMaxChannelValue, Math.max(0, nG))
        nB = Math.min(kMaxChannelValue, Math.max(0, nB))

        nR = nR shr 10 and 0xff
        nG = nG shr 10 and 0xff
        nB = nB shr 10 and 0xff

        return -0x1000000 or (nR shl 16) or (nG shl 8) or nB
    }

    fun getTransformationMatrix(srcWidth: Int, srcHeight: Int,
                                dstWidth: Int, dstHeight: Int,
                                applyRotation: Int, maintainAspectRatio: Boolean): Matrix {
        val matrix = Matrix()

        if (applyRotation != 0) {
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

    /**
     * 从assets中读取图片
     *
     * @param context
     * @param filename
     * @return
     */
    fun readFromAssets(context: Context, filename: String): Bitmap? {
        val bitmap: Bitmap
        val asm = context.assets
        try {
            val `is` = asm.open(filename)
            bitmap = BitmapFactory.decodeStream(`is`)
            `is`.close()
        } catch (e: IOException) {
            e.printStackTrace()
            return null
        }

        return bitmap
    }

    /**
     * 给rect增加margin
     *
     * @param bitmap
     * @param rect
     * @param marginX
     * @param marginY
     */
    fun rectExtend(bitmap: Bitmap, rect: Rect, marginX: Int, marginY: Int) {
        rect.left = max(0, rect.left - marginX / 2)
        rect.right = min(bitmap.width - 1, rect.right + marginX / 2)
        rect.top = max(0, rect.top - marginY / 2)
        rect.bottom = min(bitmap.height - 1, rect.bottom + marginY / 2)
    }

    /**
     * 加载模型文件
     *
     * @param assetManager
     * @param modelPath
     * @return
     * @throws IOException
     */
    @Throws(IOException::class)
    fun loadModelFile(assetManager: AssetManager, modelPath: String): MappedByteBuffer {
        val fileDescriptor = assetManager.openFd(modelPath)
        val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
        val fileChannel = inputStream.channel
        val startOffset = fileDescriptor.startOffset
        val declaredLength = fileDescriptor.declaredLength
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
    }

    /**
     * 归一化图片到[0, 1]
     *
     * @param bitmap
     * @return
     */
    fun normalizeImage(bitmap: Bitmap): Array<Array<FloatArray>> {
        val h = bitmap.height
        val w = bitmap.width
        val floatValues = Array(h) { Array(w) { FloatArray(3) } }

        val imageMean = 0.0f
        val imageStd = 255.0f

        val pixels = IntArray(h * w)
        bitmap.getPixels(pixels, 0, bitmap.width, 0, 0, w, h)
        for (i in 0 until h) { // 注意是先高后宽
            for (j in 0 until w) {
                val `val` = pixels[i * w + j]
                val r = ((`val` shr 16 and 0xFF) - imageMean) / imageStd
                val g = ((`val` shr 8 and 0xFF) - imageMean) / imageStd
                val b = ((`val` and 0xFF) - imageMean) / imageStd
                val arr = floatArrayOf(r, g, b)
                floatValues[i][j] = arr
            }
        }
        return floatValues
    }

    /**
     * 缩放图片
     *
     * @param bitmap
     * @param scale
     * @return
     */
    fun bitmapResize(bitmap: Bitmap, scale: Float): Bitmap {
        val width = bitmap.width
        val height = bitmap.height
        val matrix = Matrix()
        matrix.postScale(scale, scale)
        return Bitmap.createBitmap(
                bitmap, 0, 0, width, height, matrix, true)
    }

    /**
     * 图片矩阵宽高转置
     *
     * @param in
     * @return
     */
    fun transposeImage(`in`: Array<Array<FloatArray>>): Array<Array<FloatArray>> {
        val h = `in`.size
        val w = `in`[0].size
        val channel = `in`[0][0].size
        val out = Array(w) { Array(h) { FloatArray(channel) } }
        for (i in 0 until h) {
            for (j in 0 until w) {
                out[j][i] = `in`[i][j]
            }
        }
        return out
    }

    /**
     * 4维图片batch矩阵宽高转置
     *
     * @param in
     * @return
     */
    fun transposeBatch(`in`: Array<Array<Array<FloatArray>>>): Array<Array<Array<FloatArray>>> {
        val batch = `in`.size
        val h = `in`[0].size
        val w = `in`[0][0].size
        val channel = `in`[0][0][0].size
        val out = Array(batch) { Array(w) { Array(h) { FloatArray(channel) } } }
        for (i in 0 until batch) {
            for (j in 0 until h) {
                for (k in 0 until w) {
                    out[i][k][j] = `in`[i][j][k]
                }
            }
        }
        return out
    }

    /**
     * 截取box中指定的矩形框(越界要处理)，并resize到size*size大小，返回数据存放到data中。
     *
     * @param bitmap
     * @param box
     * @param size   return
     */
    fun cropAndResize(bitmap: Bitmap, box: Box, size: Int): Array<Array<FloatArray>> {
        // crop and resize
        val matrix = Matrix()
        val scaleW = 1.0f * size / box.width()
        val scaleH = 1.0f * size / box.height()
        matrix.postScale(scaleW, scaleH)
        val rect = box.transform2Rect()
        val croped = Bitmap.createBitmap(
                bitmap, rect.left, rect.top, box.width().toInt(), box.height().toInt(), matrix, true)

        return normalizeImage(croped)
    }

    /**
     * 按照rect的大小裁剪
     *
     * @param bitmap
     * @param rect
     * @return
     */
    fun crop(bitmap: Bitmap, rect: Rect): Bitmap {
        var x = rect.left
        if (x < 0) {
            x = 0
        }
        var y = rect.top
        if (y < 0) {
            y = 0
        }
        var width = rect.right - rect.left
        if (x + width > bitmap.width) {
            width = bitmap.width - x
        }
        var height = rect.bottom - rect.top
        if (y + height > bitmap.height) {
            height = bitmap.height - y
        }
        return Bitmap.createBitmap(bitmap, x, y, width, height)
    }

    /**
     * l2正则化
     *
     * @param embeddings
     * @param epsilon    惩罚项
     * @return
     */
    fun l2Normalize(embeddings: Array<FloatArray>, epsilon: Double) {
        for (i in embeddings.indices) {
            var squareSum = 0f
            for (j in 0 until embeddings[i].size) {
                squareSum += Math.pow(embeddings[i][j].toDouble(), 2.0).toFloat()
            }
            val xInvNorm = Math.sqrt(Math.max(squareSum.toDouble(), epsilon)).toFloat()
            for (j in 0 until embeddings[i].size) {
                embeddings[i][j] = embeddings[i][j] / xInvNorm
            }
        }
    }
}
