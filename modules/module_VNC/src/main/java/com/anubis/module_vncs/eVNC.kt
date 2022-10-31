package com.anubis.module_vncs

import android.content.Context
import com.anubis.kt_extends.eShell
import com.anubis.kt_extends.eShell.Companion.eIShell
import com.anubis.kt_extends.eShowTip
import org.jetbrains.anko.custom.async
import java.io.*


/**
 * Author  ： AnubisASN   on 18-9-20 上午11:34.
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
open  class eVNC internal  constructor(){
    companion object{
        private  lateinit var  mContext: Context
        fun  eInit(context: Context):eVNC{
            mContext=context
            return  eInit
        }
        private val eInit by lazy (LazyThreadSafetyMode.SYNCHRONIZED){ eVNC() }
    }
    open  fun eStartVNCs(): Boolean {
        val inputStream: InputStream
        var result = false
        try {
            if (!eIShell.eHaveRoot()) {
                mContext.eShowTip("无ROOT权限，无法执行")
                return false
            }
            if (File("/data/local/vncs").exists()) {
                async { eIShell.eExecShellSilent("/data/local/vncs") }
            } else {
                inputStream = mContext.getResources().getAssets().open("vncs")// assets文件夹下的文件
                val file = File(mContext.externalCacheDir.path)
                if (!file.exists()) {
                    file.mkdirs()
                }
                val fileOutputStream = FileOutputStream(mContext.externalCacheDir.path + "/" + "vncs")// 保存到本地的文件夹下的文件
                val buffer = ByteArray(1024)
                var count = 0
                while (inputStream.read(buffer).apply { count = this } > 0) {
                    fileOutputStream.write(buffer, 0, count)
                }
                fileOutputStream.flush()
                fileOutputStream.close()
                inputStream.close()
                async {eIShell.eExecShellSilent("cp  ${mContext.externalCacheDir.path}/vncs /data/local && chmod 777 /data/local/vncs && rm -rf ${mContext.externalCacheDir.path}/vncs && /data/local/vncs") }
            }
            result = true
        } catch (e: IOException) {
            result = false
            e.printStackTrace()
        }
        return result
    }
}
