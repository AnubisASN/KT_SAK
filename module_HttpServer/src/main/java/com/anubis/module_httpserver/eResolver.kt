package com.anubis.module_httpserver


import android.os.Handler
import com.anubis.kt_extends.eLog
import com.anubis.module_httpserver.protocols.http.IHTTPSession
import com.anubis.module_httpserver.protocols.http.eHTTPD
import com.anubis.module_httpserver.protocols.http.request.Method
import com.anubis.module_httpserver.protocols.http.response.IStatus
import com.anubis.module_httpserver.protocols.http.response.Response

import java.util.HashMap

import com.anubis.module_httpserver.protocols.http.response.Response.newFixedLengthResponse
import com.anubis.module_httpserver.protocols.http.response.Status
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import java.io.*


/**
 * Author  ： AnubisASN   on 19-7-1 下午4:32.
 * E-mail  ： anubisasn@gmail.com ( anubisasn@qq.com )
 * Q Q： 773506352
 * 命名规则定义：
 * Module :  module_'ModuleName'
 * Library :  lib_'LibraryName'
 * Package :  'PackageName'_'Module'
 * Class :  'Mark'_'Function'_'Tier'
 * Layout :  'Module'_'Function'
 * Resource :  'Module'_'ResourceName'_'Mark'
 * Layout Id :  'LoayoutName'_'Widget'_'FunctionName'
 * Class Id :  'LoayoutName'_'Widget'+'FunctionName'
 * Router :  /'Module'/'Function'
 * 说明：默认常用解析   可用Handler 回调参数
 */
class eResolver(port: Int = 3335, handler: Handler? = null) : eHTTPD(port, handler) {
    val path = "/sdcard"
    var httpResult: String = "HTTP Server 创建成功"
    var delay = 0L
    public override fun serve(session: IHTTPSession, handler: Handler?): Response {
        var json: String? = null
        try {
            // 这一句话必须要写，否则在获取数据时，获取不到数据
//            json = parseBody(session)   //raw 解析
            session.parseBody(HashMap()) //常用数据解析
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: eHTTPD.ResponseException) {
            e.printStackTrace()
        }

        val message = handler?.obtainMessage()
        message?.obj = session.parms
        handler?.sendMessage(message)
        eLog("uri:${session.uri}-")
        runBlocking {
            delay(delay)
        }
        return newFixedLengthResponse(StringBuilder().append(httpResult).toString())
    }





}
