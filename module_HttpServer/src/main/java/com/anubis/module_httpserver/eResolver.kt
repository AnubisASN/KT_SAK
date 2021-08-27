package com.anubis.module_httpserver


import android.annotation.TargetApi
import android.os.Build
import android.os.Handler
import com.anubis.module_httpserver.eManage.Companion.eIManage
import com.anubis.module_httpserver.protocols.http.IHTTPSession
import com.anubis.module_httpserver.protocols.http.eHTTPD
import com.anubis.module_httpserver.protocols.http.response.Response
import com.anubis.module_httpserver.eResolverType.FILE_PARSE
import com.anubis.module_httpserver.eResolverType.FILE_PUSH
import com.anubis.module_httpserver.eResolverType.NULL_PARSE
import com.anubis.module_httpserver.eResolverType.RAW_PARSE
import com.anubis.module_httpserver.eResolverType.SESSION_PARSE
import java.io.File


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
 * 说明：默认常用解析(接口定义)   可用Handler 异步回调处理  eResultBlock 同步返回结果  选其一
 */
@TargetApi(Build.VERSION_CODES.N)
open class eResolver(port: Int = 3335, handler: Handler? = null) : eHTTPD(port, handler) {
    companion object {
        var eResultBlock: ((String,IHTTPSession) -> Response)? = null
    }

    open public override fun serve(session: IHTTPSession, handler: Handler?): Response {
        val uri = session.uri.replace("/", "")
        return eResultBlock?.let { it(uri,session) } ?:Response.newFixedLengthResponse(when (uri) {
            "File" ->
                    if (eIManage.eFileParse(session, "file").apply {
                                val msg = handler?.obtainMessage()
                                msg?.what = FILE_PARSE
                                msg?.obj = this
                                handler?.sendMessage(msg)
                            } != null)
                        "上传成功"
                    else "上传失败"

            "Raw" -> eIManage.eRawParse(session).apply {
                val msg = handler?.obtainMessage()
                msg?.what = RAW_PARSE
                msg?.obj = this
                handler?.sendMessage(msg)
            }
            "Data" -> if (eIManage.eSessionParse(session).apply {
                        val msg = handler?.obtainMessage()
                        msg?.what = SESSION_PARSE
                        msg?.obj = this
                        handler?.sendMessage(msg)
                    } != null) "成功" else "解析错误"
            "" -> if (uri.contains(".zip")) {
                val fs = eIManage.eFileDownload(File("/sdcard/Web/$uri"))
                if (fs == null) "404" else return fs
            } else
                eIManage.httpResult.apply {
                    val msg = handler?.obtainMessage()
                    msg?.what = NULL_PARSE
                    msg?.obj = this
                    handler?.sendMessage(msg)
                }

            else -> return eIManage.eFilePush(session, (eIManage.eHttpPath + uri).apply {
                val msg = handler?.obtainMessage()
                msg?.what = FILE_PUSH
                msg?.obj = this
                handler?.sendMessage(msg)
            })

        })


    }


}

object eResolverType {
    val NULL_PARSE = 0  //httpResult  返回自定义结果
    val FILE_PARSE = 1   //文件上传解析 （文件路径）
    val RAW_PARSE = 2   //raw解析 返回上传字符串
    val SESSION_PARSE = 3  //常用数据解析 返回 HashMap
    val FILE_PUSH = 4   //文件推送  返回推送文件路径
}
