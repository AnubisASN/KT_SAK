package com.anubis.module_httpserver

import com.anubis.kt_extends.eFile.eCopyFile
import com.anubis.kt_extends.eLog
import com.anubis.kt_extends.eLogE
import com.anubis.module_httpserver.protocols.http.IHTTPSession
import com.anubis.module_httpserver.protocols.http.eHTTPD
import com.anubis.module_httpserver.protocols.http.response.Response
import com.anubis.module_httpserver.protocols.http.response.Status
import java.io.*

/**
 * Author  ： AnubisASN   on 19-11-1 上午9:00.
 * E-mail  ： anubisasn@gmail.com ( anubisasn@qq.com )
 *  Q Q： 773506352
 *命名规则定义：
 *Module :  module_'ModuleName'
 *Library :  lib_'LibraryName'
 *Package :  'PackageName'_'Module'
 *Class :  'Mark'_'Function'_'Tier'
 *Layout :  'Module'_'Function'
 *Resource :  'Module'_'ResourceName'_'Mark'
 *Layout Id :  'LoayoutName'_'Widget'_'FunctionName'
 *Class Id :  'LoayoutName'_'Widget'+'FunctionName'
 *Router :  /'Module'/'Function'
 *说明： 处理
 */
object eManage {
    val path = "/sdcard/Web/"
    var httpResult: String = "HTTP Server 创建成功"
    var delay = 0L

    /**
     *  文件上传解析
     */
    fun fileParse(session: IHTTPSession, fileParms: String, savePath: String = path): String? {
        val hashMap = HashMap<String, String>()
        session.parseBody(hashMap)
        val tmpFilePath = hashMap[fileParms]
        eLog("tmpFilePath:$tmpFilePath--${session.parms[fileParms]}")
        val tmpFile = File(tmpFilePath)
        val targetFile = File("$savePath${session.parms[fileParms]}")
        return if (eCopyFile(tmpFile.path, targetFile.path))targetFile.path else null
    }

    /**
     *  文件推送
     */
    fun filePush(session: IHTTPSession, pathName: String): Response {
        val fis = FileInputStream(pathName)
        return Response.newFixedLengthResponse(Status.OK, eHTTPD.MIME_HTML, readHtml(pathName))
    }

    private fun readHtml(pathname: String): String {
        var br: BufferedReader? = null
        val sb = StringBuffer()
        try {
            br = BufferedReader(InputStreamReader(FileInputStream(pathname), "UTF-8"))
            var temp: String? = null
            while ((br.readLine()).apply { temp = this } != null) {
                sb.append(temp)
            }
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return sb.toString()
    }

    /**
     * Raw 解析
     */
    fun rawParse(session: IHTTPSession): String? {
        return rawParseBody(session)
//        return Response.newFixedLengthResponse(rawParseBody(session))
    }

    private fun rawParseBody(session: IHTTPSession): String? {
        var body: String? = null
        try {
            val `is` = session.inputStream ?: return body
            val bufsize = 100000
            val buf = ByteArray(bufsize)
            val rlen = `is`.read(buf, 0, bufsize)        //发现在这里阻塞了！
            if (rlen <= 0) {
                return null
            }
            val hbis = ByteArrayInputStream(buf, 0, rlen)
            val hin = BufferedReader(InputStreamReader(hbis, Charsets.UTF_8))
            body = hin.readLine()
            return body
        } catch (ioe: IOException) {
            eLogE("IOException: $ioe",ioe)
            return "IOException: $ioe"
        }
    }

    /**
     * 常用数据解析
     */
    fun sessionParse(session: IHTTPSession) : MutableMap<String, String>? {
        session.parseBody(HashMap())
        return session.parms
    }
}
