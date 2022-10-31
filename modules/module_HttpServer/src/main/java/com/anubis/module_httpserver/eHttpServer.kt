package com.anubis.module_httpserver

import android.os.Handler
import com.anubis.kt_extends.eLog
import com.anubis.kt_extends.eLogE
import com.anubis.kt_extends.eReflection
import com.anubis.kt_extends.eReflection.Companion.eIReflection
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
 * 说明： http 服务
 */
class eHttpServer private constructor() {
    private var server: eHTTPD? = null
    companion object {
        val eIHttpServer by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
            eHttpServer()
        }
    }

    fun eStart(`class`: Class<*>? = eResolver::class.java, port: Int = 3334, handler: Handler? = null): eHTTPD? {
        if (server == null) {
            try {
                val con =eIReflection.eGetClass(`class`!!.name).getConstructor(Int::class.java, Handler::class.java)
                server = con.newInstance(port, handler) as eHTTPD
                eLog("定义开启HTTP服务成功")
            } catch (e: NoSuchMethodException) {
                val con =eIReflection.eGetClass(`class`!!.name).getConstructor()
                server = con.newInstance() as eHTTPD
                eLog("定义开启失败,默认配置：3334")
            } catch (e: Exception) {
                e.eLogE("HTTP服务开启失败")
            }
            server?.start()
        } else {
            eLog("服务运行中")
        }
        return server
    }

    fun eStop(): Boolean {
        val status = server?.stop() ?: false
        server = null
        return status
    }

}
