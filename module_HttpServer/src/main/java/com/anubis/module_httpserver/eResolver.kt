package com.anubis.module_httpserver


import android.annotation.TargetApi
import android.os.Build
import android.os.Handler
import com.anubis.kt_extends.eLog
import com.anubis.module_httpserver.eManage.fileParse
import com.anubis.module_httpserver.eManage.filePush
import com.anubis.module_httpserver.protocols.http.IHTTPSession
import com.anubis.module_httpserver.protocols.http.eHTTPD
import com.anubis.module_httpserver.protocols.http.response.Response
import com.anubis.module_httpserver.eManage.httpResult
import com.anubis.module_httpserver.eManage.rawParse
import com.anubis.module_httpserver.eManage.sessionParse


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
@TargetApi(Build.VERSION_CODES.N)
class eResolver(port: Int = 3335, handler: Handler? = null) : eHTTPD(port, handler) {


    public override fun serve(session: IHTTPSession, handler: Handler?): Response {
        val uri = session.uri.replace("/", "").apply { eLog("uri:$this") }
        return Response.newFixedLengthResponse(when (uri) {
            "File" -> if (fileParse(session, "file").apply { eLog("path:$this") } != null)
                "上传成功"
            else "上传失败"
            "Raw" -> rawParse(session)
            "Data"-> if (sessionParse(session).apply {
                        eLog("${this?.get("user")}--${this?.get("password")}")
                    }!=null) "成功" else "解析错误"
            "" ->return filePush(session, eManage.path + "Test.html")
            else -> return filePush(session, eManage.path + uri)

        })


    }


}
