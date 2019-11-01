package com.anubis.app_hserver

import android.annotation.SuppressLint
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Message
import com.anubis.kt_extends.eDevice
import com.anubis.kt_extends.eLog
import com.anubis.module_httpserver.eResolver
import com.anubis.module_httpserver.eHttpServer
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.custom.async

@SuppressLint("SetTextI18n")
class MainActivity : AppCompatActivity() {
    private var mHttpServer: eResolver? = null

    private var httpHandler = @SuppressLint("HandlerLeak")
    object : Handler() {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            val map = msg.obj as HashMap<*, *>
//            mHttpServer?.httpResult="成功：${map["userAccount"]}"
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
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
