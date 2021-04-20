package com.anubis.module_detection.face_mnn

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Rect
import android.os.Environment
import com.anubis.kt_extends.*

/**
 * Author  ： AnubisASN   on 20-4-28 上午10:49.
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
 *说明：人脸检测器
 */
open class eFaceSDK internal  constructor(){
    companion object{
       private lateinit var mContext: Context
        fun eInit(context: Context):eFaceSDK{
            mContext=context
          return  eInit
        }
     private   val eInit by lazy (LazyThreadSafetyMode.SYNCHRONIZED){ eFaceSDK() }
    }
    init {
        eLog("FaceSDK准备初始化")
        val sdDir = Environment.getExternalStorageDirectory()//get model store dir
        val sdPath = "$sdDir/facesdk/"
        eAssets.eInit.eAssetsToFile(mContext, "RFB-320.mnn", sdPath+"RFB-320.mnn")
        eAssets.eInit.eAssetsToFile(mContext, "RFB-320-quant-ADMM-32.mnn",sdPath+"RFB-320-quant-ADMM-32.mnn")
        eAssets.eInit.eAssetsToFile(mContext, "RFB-320-quant-KL-5792.mnn",sdPath+"RFB-320-quant-KL-5792.mnn")
        eAssets.eInit.eAssetsToFile(mContext, "slim-320.mnn",sdPath+"slim-320.mnn")
        eAssets.eInit.eAssetsToFile(mContext, "slim-320-quant-ADMM-50.mnn",sdPath+"slim-320-quant-ADMM-50.mnn")
        eLog("文件复制完成")
        FaceSDKNative.eInit.FaceDetectionModelInit(sdPath).eLogI("faceSDK初始化")
    }

    open fun eFaceDetect(bitmap: Bitmap, imageChannel: Int = 4): ArrayList<Rect> {
        return eFaceDetect(eBitmap.eInit.eBitmapToByteArray(bitmap), bitmap.width, bitmap.height, imageChannel)
    }

    open fun eFaceDetect(byteArray: ByteArray, width: Int, height: Int, imageChannel: Int = 4): ArrayList<Rect> {
        val results = arrayListOf<Rect>()
        val faceInfo = FaceSDKNative.eInit.FaceDetect(byteArray, width, height, imageChannel)
        val faceNum = faceInfo[0]
        for (i in 0 until faceNum) {
            results.add(Rect(faceInfo[1 + 4 * i], faceInfo[2 + 4 * i], faceInfo[3 + 4 * i], faceInfo[3 + 4 * i]+faceInfo[2 + 4 * i]-faceInfo[1 + 4 * i]+(faceInfo[4 + 4 * i]-faceInfo[3 + 4 * i])/10))
        }
        return results
    }

    open fun eDestroy() {
        FaceSDKNative.eInit.FaceDetectionModelUnInit()
    }
}
