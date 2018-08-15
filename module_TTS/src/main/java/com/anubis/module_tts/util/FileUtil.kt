package com.anubis.module_tts.util

import android.content.Context
import android.content.res.AssetManager
import android.os.Environment

import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream

/**
 * Created by fujiayi on 2017/5/19.
 */

object FileUtil {

    // 创建一个临时目录，用于复制临时文件，如assets目录下的离线资源文件
    fun createTmpDir(context: Context): String {
        val sampleDir = "baiduTTS"
        var tmpDir = Environment.getExternalStorageDirectory().toString() + "/" + sampleDir
        if (!FileUtil.makeDir(tmpDir)) {
            tmpDir = context.getExternalFilesDir(sampleDir)!!.absolutePath
            if (!FileUtil.makeDir(sampleDir)) {
          throw RuntimeException("create model resources dir failed :" + tmpDir)
            }
        }
        return tmpDir
    }

    fun fileCanRead(filename: String): Boolean {
        val f = File(filename)
        return f.canRead()
    }

    fun makeDir(dirPath: String): Boolean {
        val file = File(dirPath)
        return if (!file.exists()) {
            file.mkdirs()
        } else {
            true
        }
    }

    @Throws(IOException::class)
    fun copyFromAssets(assets: AssetManager, source: String, dest: String, isCover: Boolean) {
        val file = File(dest)
        if (isCover || !isCover && !file.exists()) {
            var `is`: InputStream? = null
            var fos: FileOutputStream? = null
            try {
                `is` = assets.open(source)
                fos = FileOutputStream(dest)
                val buffer = ByteArray(1024)
                var size = 0
                do {
                    size = `is`!!.read(buffer, 0, 1024)
                    if(size>=0){
                        fos.write(buffer, 0, size)
                    }

                }
                while (size >= 0)
            } finally {
                if (fos != null) {
                    try {
                        fos.close()
                    } finally {
                        if (`is` != null) {
                            `is`.close()
                        }
                    }
                }
            }
        }
    }
}
