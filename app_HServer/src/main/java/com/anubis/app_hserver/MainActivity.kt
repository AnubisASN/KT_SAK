package com.anubis.app_hserver

import android.annotation.SuppressLint
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.view.View
import com.anubis.module_httpserver.eResolver
import com.anubis.module_httpserver.eHttpServer
import com.anubis.module_httpserver.eResolverType
import com.anubis.module_httpserver.protocols.http.eHTTPD
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File
import kotlin.collections.HashMap
import com.anubis.kt_extends.*


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
                    (msg.obj as HashMap<*, *>).forEach {
                        eLog("Key:${it.key}--Value:${it.value}")
                    }
                }
                else -> {
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        File("/sdcard/Web").apply { if (!this.exists()) this.mkdirs() }
        eAssets.eInit.eAssetsToFile(this, "Web/index.html", "/sdcard/Web/index.html")
        eAssets.eInit.eAssetsToFile(this, "Web/base64.js", "/sdcard/Web/base64.js")
        eAssets.eInit.eAssetsToFile(this, "Web/index.js", "/sdcard/Web/index.js")
        eAssets.eInit.eAssetsToFile(this, "Web/jquery.js", "/sdcard/Web/jquery.js")
        eAssets.eInit.eAssetsToFile(this, "Web/Vysor5.5.5.crx", "/sdcard/Web/Vysor5.5.5.crx")
        eAssets.eInit.eAssetsToFile(this, "Web/Vysor.zip", "/sdcard/Web/Vysor.zip")
        mHttpServer = eHttpServer.instance.eStart(eResolver::class.java, handler = httpHandler)


    }
    override fun onResume() {
        super.onResume()
        tvHint.text = String.format(resources.getString(R.string.hint), "${eDevice.eInit.eGetHostIP()}:${mHttpServer?.myPort}")
    }

    fun onClick(v: View) {
        when (v.id) {
        }
    }


}
