package com.anubis.module_qrcode

/**
 * *          _       _
 * *   __   _(_)_   _(_) __ _ _ __
 * *   \ \ / / \ \ / / |/ _` | '_ \
 * *    \ V /| |\ V /| | (_| | | | |
 * *     \_/ |_| \_/ |_|\__,_|_| |_|
 *
 *
 * Created by vivian on 2016/11/28.
 */

import android.app.Activity
import android.app.Application
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.hardware.Camera
import android.net.Uri
import com.anubis.kt_extends.eLogE
import com.anubis.kt_extends.eSystemSelectImg
import com.anubis.kt_extends.eUri
import com.anubis.uuzuche.lib_zxing.activity.ZXingLibrary
import com.anubis.uuzuche.lib_zxing.activity.eCodeUtils
import com.anubis.uuzuche.lib_zxing.activity.eDefaultCaptureActivity
import com.anubis.uuzuche.lib_zxing.camera.BitmapLuminanceSource
import com.anubis.uuzuche.lib_zxing.decoding.DecodeFormatManager
import com.google.zxing.*
import com.google.zxing.common.HybridBinarizer
import java.util.*

open class eQRCodeScan internal constructor() {
    companion object {
        val CAMERA_ID_CODE = "cameraIdCode"

        /**
         * 扫描跳转Activity RequestCode
         */
        var REQUEST_CODE = 111

        /**
         * 选择系统图片Request Code
         */
        var REQUEST_IMAGE = 112
        private lateinit var mApp: Application
        private val eInit by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
            ZXingLibrary.initDisplayOpinion(mApp)
            eQRCodeScan()
        }

        fun eInit(application: Application): eQRCodeScan {
            mApp = application
            return eInit
        }
    }

    /**
     * 解析二维码结果
     */
    interface AnalyzeCallback {
        fun onAnalyzeSuccess(mBitmap: Bitmap?, result: String?)
        fun onAnalyzeFailed()
    }

    /*结果解析*/
    open fun eResult(requestCode: Int, resultCode: Int, data: Intent?): String? {
        data ?: return null.eLogE("eResult data==null")
        return when (requestCode) {
            REQUEST_CODE -> {
                with(data.extras ?: return null.eLogE("eResult extras==null")) {
                    if (getInt(eCodeUtils.RESULT_TYPE) == eCodeUtils.RESULT_SUCCESS)
                        getString(eCodeUtils.RESULT_STRING)
                    else
                        null.eLogE("二维码解析失败")
                }
            }
            REQUEST_IMAGE -> {
                var resultStr: String? = null
                resultStr = eResourceAnalyze(tActivity, data.data) {
                    resultStr = it
                }
                return resultStr
            }
            else -> null.eLogE("未知结果代码")
        }
    }

    private var tActivity: Activity? = null
    fun eSlsectImgAnalyze(activity: Activity, requestCode: Int = REQUEST_IMAGE) {
        tActivity = activity
        REQUEST_IMAGE = requestCode
        activity.eSystemSelectImg(REQUEST_IMAGE)

    }

    fun eResourceAnalyze(activity: Activity?, uri: Uri?, callback: AnalyzeCallback? = null, successBlock:( (String) -> Unit)?=null): String? {
        val path = eUri.eInit.eGetImageAbsolutePath(activity, uri) ?: return null
        return eResourceAnalyze(path, callback, successBlock)
    }

    fun eResourceAnalyze(path: String, callback: AnalyzeCallback? = null, successBlock: ((String) -> Unit)?=null): String? {
        var tResult: String? = null
        try {
            /**
             * 首先判断图片的大小,若图片过大,则执行图片的裁剪操作,防止OOM
             */
            val options = BitmapFactory.Options()
            options.inJustDecodeBounds = true // 先获取原大小
            var mBitmap = BitmapFactory.decodeFile(path, options)
            options.inJustDecodeBounds = false // 获取新的大小
            var sampleSize = (options.outHeight / 400.toFloat()).toInt()
            if (sampleSize <= 0) sampleSize = 1
            options.inSampleSize = sampleSize
            mBitmap = BitmapFactory.decodeFile(path, options)
            tResult = eAnalyzeBitmap(mBitmap, callback ?: object : AnalyzeCallback {
                override fun onAnalyzeSuccess(mBitmap: Bitmap?, result: String?) {
                    result ?: return
                    tResult = result
                    successBlock?.let { it(result) }
                }

                override fun onAnalyzeFailed() {
                }
            })
        } catch (e: Exception) {
            e.eLogE("eResourceAnalyze")
        }
        return tResult
    }


    /**
     * 解析二维码图片工具类
     * @param analyzeCallback
     */
    open fun eAnalyzeBitmap(mBitmap: Bitmap?, analyzeCallback: AnalyzeCallback?=null,hintsBlock:( Hashtable<DecodeHintType, Any?>)->Unit={}, successBlock:( (String) -> Unit)?=null):String? {
        val multiFormatReader = MultiFormatReader()
        // 解码的参数
        val hints = Hashtable<DecodeHintType, Any?>(2)
        // 可以解析的编码类型
        var decodeFormats = Vector<BarcodeFormat?>()
        if (decodeFormats == null || decodeFormats.isEmpty()) {
            decodeFormats = Vector()

            // 这里设置可扫描的类型，我这里选择了都支持
            decodeFormats.addAll(DecodeFormatManager.ONE_D_FORMATS)
            decodeFormats.addAll(DecodeFormatManager.QR_CODE_FORMATS)
            decodeFormats.addAll(DecodeFormatManager.DATA_MATRIX_FORMATS)
        }
        hints[DecodeHintType.POSSIBLE_FORMATS] = decodeFormats
        // 设置继续的字符编码格式为UTF8
        // hints.put(DecodeHintType.CHARACTER_SET, "UTF8");
        // 设置解析配置参数
        hintsBlock(hints)
        multiFormatReader.setHints(hints)

        // 开始对图像资源解码
        var rawResult: Result? = null
        try {
            rawResult = multiFormatReader.decodeWithState(BinaryBitmap(HybridBinarizer(BitmapLuminanceSource(mBitmap))))
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
       return if (rawResult != null) {
            successBlock?.let {it(rawResult.text)}
            analyzeCallback?.onAnalyzeSuccess(mBitmap, rawResult.text)
           rawResult.text
        } else {
            analyzeCallback?.onAnalyzeFailed()
           null
        }
    }

    /**
     * 默认扫码界面
     *
     * @param text 文字或网址
     * @param size 生成二维码的大小
     * @return bitmap
     */
    open fun eScanActivity(activity: Activity,  clazz: Class<*>? = eDefaultCaptureActivity::class.java, cameraId: Int = Camera.CameraInfo.CAMERA_FACING_BACK,requestCode: Int = REQUEST_CODE) {
        REQUEST_CODE = requestCode
        val intent = Intent(mApp, clazz)
        intent.putExtra(CAMERA_ID_CODE, cameraId)
        activity.startActivityForResult(intent, REQUEST_CODE)
    }


}

