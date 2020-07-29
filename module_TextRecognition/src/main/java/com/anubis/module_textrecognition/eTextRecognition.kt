package com.anubis.module_textrecognition

import android.app.ProgressDialog
import android.content.Context
import android.graphics.Bitmap
import android.os.Environment
import com.googlecode.tesseract.android.TessBaseAPI
import com.anubis.kt_extends.eAssets
import com.anubis.kt_extends.eShowTip
import org.jetbrains.anko.custom.async
import java.io.File


/**
 * Author  ： AnubisASN   on 19-12-28 上午9:19.
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
object eTextRecognition {
    private var mDialog: ProgressDialog? = null
    private var tessBaseAPI: TessBaseAPI?=null
    private val DATA_PATH = Environment.getExternalStorageDirectory().toString() + "/Tess"
    private val tessdata = DATA_PATH + File.separator + "tessdata"
   private var language = "chi_sim"




      fun eInit(context: Context) {
        val dataDir = File(tessdata)
        if (!dataDir.exists()) {
            dataDir.mkdirs()
            mDialog = ProgressDialog(context)
            mDialog?.setMessage("拷贝训练数据中......")
            mDialog?.setCanceledOnTouchOutside(false)
            mDialog?.show()
            async {
                eAssets.eInit.eAssetsToFile(context, "chi_sim.traineddata", tessdata + File.separator +
                        language + ".traineddata")
                context.eShowTip("训练文件复制完成")
            }
        }
          tessBaseAPI  = TessBaseAPI()
    }
    fun eRecognition(bitmap: Bitmap):String?{
            tessBaseAPI?.init(DATA_PATH, language)//传入训练文件目录和训练文件
            tessBaseAPI?.setImage(bitmap)
            val text = tessBaseAPI?.getUTF8Text()
            return  text
    }
}
