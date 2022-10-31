package com.anubis.module_httpserver

import com.anubis.kt_extends.eFile
import com.anubis.kt_extends.eFile.Companion.eIFile
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
 *说明： 数据接收处理
 */
class eManage private  constructor(){
  var httpResult: String = "HTTP Server 创建成功"
  var eHttpPath = "/sdcard/Web/"
    companion object{
        val eIManage by lazy (LazyThreadSafetyMode.SYNCHRONIZED){ eManage() }
    }


    /**
     *说明： 文件上传解析
     * @调用方法：eFileParse()
     * @param session: IHTTPSession； 会话通道
     * @param fileParms: String ; 上传文件属性名
     * @param savePath: String ; 保存路径
     * @return: String?
     */
    fun eFileParse(session: IHTTPSession, fileParms: String, savePath: String = eHttpPath): String? {
        val hashMap = HashMap<String, String>()
        session.parseBody(hashMap)
        val tmpFilePath = hashMap[fileParms]
        eLog("tmpFilePath:$tmpFilePath--${session.parms[fileParms]}")
        val tmpFile = File(tmpFilePath)
        val targetFile = File("$savePath${session.parms[fileParms]}")
        return if (eIFile.eCopyFile(tmpFile.path, targetFile.path)) targetFile.path else null
    }

    /**
     *说明： 服务端文件推送
     * @调用方法：eFilePush()
     * @param session: IHTTPSession； 会话通道
     * @param pathName: String ; 文件路径
     * @return: Response?
     */
    fun eFilePush(session: IHTTPSession, pathName: String): Response {
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
     *说明：eRawParse 解析
     * @param session: IHTTPSession； 会话通道
     * @return: String? 结果
     */
    fun eRawParse(session: IHTTPSession): String? {
        return eRawParseBody(session)
    }

    private fun eRawParseBody(session: IHTTPSession): String? {
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
            body = hin.readText()
            return body
        } catch (ioe: IOException) {
            ioe.eLogE("IOException ")
            return "IOException: $ioe"
        }
    }

    /**
     *说明：常用数据解析
     * @param session: IHTTPSession； 会话通道
     * @return: MutableMap<String, String>? ； 参数集合
     */
    fun eSessionParse(session: IHTTPSession): MutableMap<String, String>? {
        session.parseBody(HashMap())
        return session.parms
    }

    /**
     *说明：文件下载
     * @param session: IHTTPSession； 会话通道
     * @return: MutableMap<String, String>? ； 参数集合
     */
    fun eFileDownload(file: File):Response? {
        if (!file.exists()){
            eLogE("文件不存在")
            return null
        }
        val fis=FileInputStream(file)
        return Response.newFixedLengthResponse(Status.OK, "application/octet-stream", fis, fis.available().toLong());
    }
}
