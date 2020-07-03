package com.anubis.module_vncs

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.IBinder
import com.anubis.kt_extends.eLog
import com.anubis.kt_extends.eLogE
import com.anubis.kt_extends.eShell
import com.anubis.kt_extends.eShowTip
import org.jetbrains.anko.custom.async
import java.io.*
import java.nio.file.Files.exists


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
object eVNC {
    fun startVNCs(context: Context): Boolean {
        val inputStream: InputStream
        var result = false
        try {
            if (!eShell.eInit.eHaveRoot()) {
                context.eShowTip("无ROOT权限，无法执行")
                return false
            }
            if (File("/data/local/vncs").exists()) {
                async { eShell.eInit.eExecShellSilent("/data/local/vncs") }
            } else {
                inputStream = context.getResources().getAssets().open("vncs")// assets文件夹下的文件
                val file = File(context.externalCacheDir.path)
                if (!file.exists()) {
                    file.mkdirs()
                }
                val fileOutputStream = FileOutputStream(context.externalCacheDir.path + "/" + "vncs")// 保存到本地的文件夹下的文件
                val buffer = ByteArray(1024)
                var count = 0
                while (inputStream.read(buffer).apply { count = this } > 0) {
                    fileOutputStream.write(buffer, 0, count)
                }
                fileOutputStream.flush()
                fileOutputStream.close()
                inputStream.close()
                async {eShell.eInit.eExecShellSilent("cp  ${context.externalCacheDir.path}/vncs /data/local && chmod 777 /data/local/vncs && rm -rf ${context.externalCacheDir.path}/vncs && /data/local/vncs") }
            }
            result = true
        } catch (e: IOException) {
            result = false
            e.printStackTrace()
        }
        return result
    }
}
