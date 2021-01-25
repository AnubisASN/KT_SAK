package com.anubis.lpr

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Message
import android.util.Log
import com.anubis.kt_extends.eBitmap
import com.anubis.kt_extends.eLogE
import com.anubis.lpr.scanner.Scanner
import com.anubis.lpr.utils.DeepAssetUtil
import com.anubis.lpr.utils.PlateRecognition
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.jetbrains.anko.custom.onUiThread
import org.opencv.android.BaseLoaderCallback
import org.opencv.android.LoaderCallbackInterface
import org.opencv.android.OpenCVLoader
import org.opencv.android.Utils
import org.opencv.core.CvType
import org.opencv.core.Mat
import java.io.File

/**
 * Author  ： AnubisASN   on 21-1-11 上午11:00.
 * E-mail  ： anubisasn@gmail.com ( anubisasn@qq.com )
 *  Q Q： 773506352
 *命名规则定义：
 *Module :  module_'ModuleName'
 *Library :  lib_'LibraryName'
 *Package :  'PackageName'_'Module'
 *Class :  'Mark'_'Function'_'Tier'
 *Layout :  'Module'_'Function'
 *Resource :  'Module'_'ResourceName'_'Mark'
 *Layout Id :  'LoayoutName'_'Widget'_'FunctionName'
 *Class Id :  'LoayoutName'_'Widget'+'FunctionName'
 *Router :  /'Module'/'Function'
 *说明：
 */
open class eLPR internal constructor() {

    companion object {
        private lateinit var mContext: Context
        private var lprAddress: Long? = null
        private var mCallback: BaseLoaderCallback get() {
            return  object : BaseLoaderCallback(mContext) {
                @SuppressLint("StaticFieldLeak")
                override fun onManagerConnected(status: Int) {
                    if (status == LoaderCallbackInterface.SUCCESS) {
                        System.loadLibrary("lpr")
                    } else {
                        super.onManagerConnected(status)
                    }
                }}
        }
            set(value) {
                mCallback=value
            }

        fun eInit(context: Context, callback: BaseLoaderCallback? = null): eLPR {
            mContext = context
            callback?.let { mCallback = it }
            if (!OpenCVLoader.initDebug()) {
                eLogE("Internal OpenCV library not found. Using OpenCV Manager for initialization","OpenCV")
                OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_4_0, context, mCallback)
            } else {
                eLogE("OpenCV library found inside package. Using it!","OpenCV")
                mCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS)
            }
            lprAddress = DeepAssetUtil.getPRAddress(mContext)
            return eInit
        }

        private val eInit by lazy(LazyThreadSafetyMode.SYNCHRONIZED) { eLPR() }
    }

    /*识别*/
    fun eLPRIdentify(filePath: String, isCleanBitmap: Boolean = true, resultBlock: (String?) -> Unit) {
        eLPRIdentify(BitmapFactory.decodeFile(filePath), isCleanBitmap, resultBlock)
    }

    fun eLPRIdentify(bitmap: Bitmap, isCleanBitmap: Boolean = true, resultBlock: (String?) -> Unit) {
        var plateResult: String? = null
        GlobalScope.launch {
            val mat = Mat(bitmap.width, bitmap.height, CvType.CV_8UC4)
            Utils.bitmapToMat(bitmap, mat, false)
            lprAddress?.let {
                plateResult = PlateRecognition.SimpleRecognization(mat.nativeObjAddr, it)
            } ?: eLogE("识别器初始化错误")
            mContext.onUiThread { resultBlock(plateResult) }
            if (isCleanBitmap)
                eBitmap.eInit.eGcBitmap(bitmap)
        }


    }

    fun eCleanRecognizer() = lprAddress?.let { PlateRecognition.ReleasePlateRecognizer(it) }
}

