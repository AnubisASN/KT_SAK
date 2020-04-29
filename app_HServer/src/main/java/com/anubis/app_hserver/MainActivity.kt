package com.anubis.app_hserver

import android.annotation.SuppressLint
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Message
import com.anubis.kt_extends.eAssets.eAssetsToFile
import com.anubis.kt_extends.eDevice
import com.anubis.kt_extends.eLog
import com.anubis.module_httpserver.eResolver
import com.anubis.module_httpserver.eHttpServer
import com.anubis.module_httpserver.eResolverType
import com.anubis.module_httpserver.protocols.http.eHTTPD
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File

@SuppressLint("SetTextI18n")
class MainActivity : AppCompatActivity() {
    private var mHttpServer: eHTTPD? = null

    private var httpHandler = @SuppressLint("HandlerLeak")
    object : Handler() {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            when (msg.what) {
                eResolverType.FILE_PARSE -> eLog("文件路径：${msg.obj}")
                eResolverType.FILE_PUSH -> eLog("文件推送：${msg.obj}")
                eResolverType.NULL_PARSE -> eLog("自定义返回：${msg.obj}")
                eResolverType.RAW_PARSE -> eLog("RAW解析：${msg.obj}")
                eResolverType.SESSION_PARSE -> {
                    eLog("常用解析")
                    (msg.obj as HashMap<*, *>).forEach{
                        eLog("Key:${it.key}--Value:${it.value}")
                    }
                }
                else->{}
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        File("/sdcard/Web").apply { if (!this.exists()) this.mkdirs() }
        eAssetsToFile(this, "Web/index.html", "/sdcard/Web/index.html")
        eAssetsToFile(this, "Web/base64.js", "/sdcard/Web/base64.js")
        eAssetsToFile(this, "Web/index.js", "/sdcard/Web/index.js")
        eAssetsToFile(this, "Web/jquery.js", "/sdcard/Web/jquery.js")
        eAssetsToFile(this, "Web/Vysor5.5.5.crx", "/sdcard/Web/Vysor5.5.5.crx")
        eAssetsToFile(this, "Web/Vysor.zip", "/sdcard/Web/Vysor.zip")
        mHttpServer = eHttpServer.eStart(eResolver::class.java, handler = httpHandler)
    }


    override fun onResume() {
        super.onResume()
        tvHint.text = "浏览器输入地址：${eDevice.eGetHostIP()}:${mHttpServer?.myPort} 以访问"
    }
//    fun onClick(v: View) {
//        val flutterView=Flutter.createView(this,lifecycle,"route1")
//        cl.addView(flutterView)
//    }
}
