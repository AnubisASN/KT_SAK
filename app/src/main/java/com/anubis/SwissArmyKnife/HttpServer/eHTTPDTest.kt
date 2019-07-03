package com.anubis.SwissArmyKnife.HttpServer

import android.support.annotation.UiThread
import com.anubis.SwissArmyKnife.MainActivity

import com.anubis.module_httpserver.protocols.http.IHTTPSession
import com.anubis.module_httpserver.protocols.http.eHTTPD
import com.anubis.module_httpserver.protocols.http.response.Response

import java.io.IOException
import java.util.HashMap

import com.anubis.module_httpserver.protocols.http.response.Response.newFixedLengthResponse

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
 * 说明：
 */
class eHTTPDTest: eHTTPD() {
    public override fun serve(session: IHTTPSession): Response {
        try {
            // 这一句话必须要写，否则在获取数据时，获取不到数据
            session.parseBody(HashMap())
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: eHTTPD.ResponseException) {
            e.printStackTrace()
        }
        val method = session.method
        val uri = session.uri
        val parms = session.parms
        val data = parms["data"]
        MainActivity.mainActivity?.mHandler?.post { MainActivity.mainActivity?.Hint("method:$method---uri:$uri---data:$data") }
        return newFixedLengthResponse(StringBuilder().append("HTTP Server 创建成功").toString())
    }
}
