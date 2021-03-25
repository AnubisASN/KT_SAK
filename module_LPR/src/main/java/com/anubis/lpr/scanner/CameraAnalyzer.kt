package com.anubis.lpr.scanner

import android.graphics.*
import android.os.Handler
import android.os.Message
import android.util.Log
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.anubis.kt_extends.eBitmap
import com.anubis.lpr.utils.PlateRecognition
import org.opencv.android.Utils
import org.opencv.core.CvType
import org.opencv.core.Mat
import java.io.ByteArrayOutputStream
import java.io.IOException

/**
 * @auther : Aleyn
 * time   : 2020/05/27
 */
class CameraAnalyzer internal constructor(private val scannerView: ScannerView) : ImageAnalysis.Analyzer {
    private var prAddress: Long = 0
    private var previewHandler: Handler? = null
    fun setHandle(previewHandler: Handler?) {
        this.previewHandler = previewHandler
    }

    override fun analyze(image: ImageProxy) {
        if (previewHandler != null) {
            val mat = ImagetoMat(image)
            if (mat != null) {
                if (prAddress == 0L) {
                    prAddress = scannerView.prAddress
                    image.close()
                    return
                }
                val res = PlateRecognition.SimpleRecognization(mat.nativeObjAddr, prAddress)
                val message: Message
                if ("" != res) {
                    message = Message.obtain(previewHandler, Scanner.OCR_SUCCEEDED, res)
                    previewHandler = null
                } else {
                    message = Message.obtain(previewHandler, Scanner.OCR_FAILED)
                }
                message.sendToTarget()
            } else Log.d("analyze", "Mat is null")
        } else {
            Log.d(TAG, "previewHandler is null")
        }
        try {
            Thread.sleep(100)
        } catch (e: InterruptedException) {
            e.printStackTrace()
            image.close()
        }
        image.close()
    }

    private fun ImagetoMat(imageProxy: ImageProxy): Mat? {
        val plane = imageProxy.planes
        val yBuffer = plane[0].buffer // Y
        val uBuffer = plane[1].buffer // U
        val vBuffer = plane[2].buffer // V
        val ySize = yBuffer.remaining()
        val uSize = uBuffer.remaining()
        val vSize = vBuffer.remaining()
        val nv21 = ByteArray(ySize + uSize + vSize)

        //U and V are swapped
        yBuffer[nv21, 0, ySize]
        vBuffer[nv21, ySize, vSize]
        uBuffer[nv21, ySize + vSize, uSize]
        try {
            val yuvImage = YuvImage(nv21, ImageFormat.NV21, imageProxy.width, imageProxy.height, null)
            val stream = ByteArrayOutputStream(nv21.size)
            yuvImage.compressToJpeg(Rect(0, 0, yuvImage.width, yuvImage.height), 90, stream)
            var bitmap = BitmapFactory.decodeByteArray(stream.toByteArray(), 0, stream.size())
            val matrix = Matrix()
            matrix.postRotate(90f)
            val rect = scannerView.getFramingRectInPreview(bitmap.width, bitmap.height)
            bitmap = Bitmap.createBitmap(bitmap, rect.top, rect.left, rect.height(), rect.width(), matrix, true)
            stream.close()
            val mat = Mat(bitmap.width, bitmap.height, CvType.CV_8UC4)
            Utils.bitmapToMat(bitmap, mat)
            return mat
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return null
    }
    /*出现绿线转换*/
   private fun imageToBitmap(image: ImageProxy): Bitmap? {
        val yBuffer = image.planes[0].buffer
        val uBuffer = image.planes[1].buffer
        val uStride = image.planes[1].pixelStride
        val vBuffer = image.planes[2].buffer
        val vStride = image.planes[2].pixelStride
        val buffer = ByteArray(image.width * image.height * 3 / 2)
        val rowStride = image.planes[0].rowStride
        val padding = rowStride - image.width
        var pos = 0
        if (padding == 0) {
            pos = yBuffer.remaining()
            yBuffer.get(buffer, 0, pos)
        } else {
            var yBufferPos = 0
            for (row in 0 until image.height) {
                yBuffer.position(yBufferPos)
                yBuffer.get(buffer, pos, image.width)
                yBufferPos += rowStride
                pos += image.width
            }
        }

        var i = 0

        val uRemaining = uBuffer.remaining()
        while (i < uRemaining) {
            buffer[pos++] = uBuffer[i]
            i += uStride

            if (padding == 0) continue

            val rowLen = i % rowStride
            if (rowLen >= image.width) {
                i += padding
            }
        }

        i = 0
        val vRemaining = vBuffer.remaining()
        while (i < vRemaining) {
            buffer[pos++] = vBuffer[i]
            i += vStride

            if (padding == 0) continue

            val rowLen = i % rowStride
            if (rowLen >= image.width) {
                i += padding
            }
        }
        var bitmap = eBitmap.eInit.eNV21ByteArrayToBitmp(buffer, image.width, image.height, rotate = 90f, quality = 100)
        val rect =
                scannerView.getFramingRectInPreview(image.width, image.height)
        bitmap = Bitmap.createBitmap(
                bitmap,
                rect.left,
                rect.top,
                rect.width(),
                rect.height(),
                Matrix(),
                true
        )
        return bitmap
    }
    companion object {
        private val TAG = CameraAnalyzer::class.java.simpleName
    }

}
