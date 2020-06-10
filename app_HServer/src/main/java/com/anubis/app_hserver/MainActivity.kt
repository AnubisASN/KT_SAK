package com.anubis.app_hserver

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.view.View
import com.anubis.kt_extends.eApp
import com.anubis.kt_extends.eAssets.eAssetsToFile
import com.anubis.kt_extends.eDevice
import com.anubis.kt_extends.eLog
import com.anubis.module_httpserver.eResolver
import com.anubis.module_httpserver.eHttpServer
import com.anubis.module_httpserver.eResolverType
import com.anubis.module_httpserver.protocols.http.eHTTPD
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File
import java.util.*
import kotlin.collections.HashMap
import android.content.Intent
import com.anubis.kt_extends.eShell
import com.anubis.kt_extends.eShell.eAppReboot


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
        tvHint.text = String.format(resources.getString(R.string.hint), "${eDevice.eGetHostIP()}:${mHttpServer?.myPort}")
    }

    fun onClick(v: View) {
        when (v.id) {
            btChinese.id -> switchLanguage(Locale.CHINESE)
            btEnglish.id -> switchLanguage(Locale.ENGLISH)
        }
    }

    /**
     * 切换语言
     *
     * @param language
     */

    private fun switchLanguage(locale: Locale = Locale.getDefault()) {
        //设置应用语言类型
        val resources = resources
        val config = resources.configuration
        val dm = resources.displayMetrics
        config.locale = locale
        resources.updateConfiguration(config, dm)
//        //更新语言后，destroy当前页面，重新绘制
        finish()
        val it = Intent(this@MainActivity, MainActivity::class.java)
        //清空任务栈确保当前打开activit为前台任务栈栈顶
        it.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(it)
    }

}
