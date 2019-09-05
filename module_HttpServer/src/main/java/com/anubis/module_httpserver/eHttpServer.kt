package com.anubis.module_httpserver

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.anubis.kt_extends.eLog
import com.anubis.kt_extends.eLogE
import com.anubis.kt_extends.eReflection
import com.anubis.kt_extends.eShowTip
import com.anubis.module_httpserver.eHttpServer.server
import com.anubis.module_httpserver.protocols.http.eHTTPD

import java.lang.Exception

/**
 * Author  ： AnubisASN   on 19-7-1 下午4:36.
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
object eHttpServer {
    var server: eHTTPD? = null
    fun eStart(`class`: Class<*>, port: String = "3334"): eHTTPD? {
        if (server == null) {
            try {
                val con = eReflection.eGetClass(`class`.name).getConstructor(String::class.java)
                server = con.newInstance(port) as eHTTPD
                eLog("开启HTTP服务")
            } catch (e: NoSuchMethodException) {
                val con = eReflection.eGetClass(`class`.name).getConstructor()
                server = con.newInstance() as eHTTPD
                eLog("开启HTTP服务")
            } catch (e: Exception) {
                e.printStackTrace()
                eLogE("HTTP服务开启失败", e)
            }
            server?.start()
        }else{
            eLog("已存在")
        }
        return server
    }

    fun eStop(): Boolean {
        val status = server?.stop() ?: false
        server=null
        return status
    }

}
