package com.anubis.module_ftp

import android.app.Activity
import android.app.Application
import android.content.Context
import android.os.Handler
import android.os.Message
import android.support.annotation.UiThread
import android.support.v4.app.Fragment
import android.util.Log
import com.anubis.kt_extends.eJson
import com.anubis.kt_extends.eLog
import com.anubis.kt_extends.eLogE
import com.anubis.kt_extends.eNetWork

import org.apache.commons.net.ftp.FTP
import org.apache.commons.net.ftp.FTPClient
import org.apache.commons.net.ftp.FTPFile
import org.apache.commons.net.ftp.FTPReply
import org.json.JSONObject
import java.io.*
import java.lang.Exception
import java.net.ConnectException
import java.net.NoRouteToHostException
import java.net.SocketException
import kotlin.math.log

/**
 * Author  ： AnubisASN   on 19-3-26 上午9:04.
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

object eFTPc {
    private var ftpClient: FTPClient? = null
    private var FTPUrl: String = "192.168.43.1"
    private var FTPPort: Int = 3333
    private var UserName: String = "sxk"
    private var UserPassword: String = "sxk"

    init {
        ftpClient = FTPClient()
    }

    fun getFtpClient() = ftpClient!!

    /**
     * 设置FTP服务器
     * @param FTPUrl   FTP服务器ip地址
     * @param FTPPort   FTP服务器端口号
     * @param UserName    登陆FTP服务器的账号
     * @param UserPassword    登陆FTP服务器的密码
     * @return
     */

//    fun scanService(mAPP: Application, Urls: ArrayList<String>?, FTPPort: Int = 3333, UserName: String = "sxk", UserPassword: String = "sxk", mHandler: Handler?): ArrayList<dataInfo?>? {
//        val msg = Message()
//        msg.what = eUtils.FTP_SCAN
//        eLog("FTP扫描")
//        var infos: ArrayList<dataInfo?>? = arrayListOf()
//        val segment = eUtils.getIP()?.split(".")
//        if (Urls != null) {
//            eLog("FTP定义扫描")
//            for (url in Urls) {
//                if (initFTP(url, FTPPort, UserName, UserPassword)) {
//                    infos!!.add(readInfo(mAPP, url))
//                    ftpClient!!.disconnect()
//                    eLog("$url 成功")
//                }
//            }
//        }
//        if (Urls == null && segment != null) {
//            eLog("FTP自动扫描")
//            for (i in 0..2) {//255
//                val url = "${segment[0]}.${segment[1]}.${segment[2]}.$i"
//                try {
//                    eLog("IP:" + url)
//                    if (initFTP(url, FTPPort, UserName, UserPassword)) {
//                        eLog("$url 连接成功")
//                        val data = readInfo(mAPP, url)
//                        infos!!.add(data)
//                        ftpClient!!.disconnect()
//
//                    } else {
//                        eLog("$url 连接失败")
//                    }
//                } catch (e: NoRouteToHostException) {
//                    eLogE("FTP自动扫描异常：$e")
//                }
//            }
//        }
//        msg.obj = "FTP扫描:-结束"
//        mHandler?.sendMessage(msg)
//        return infos
//    }
//
//    fun readInfo(application: Application, url: String): dataInfo? {
//        var file: File? = null
//        downLoadFile(application.cacheDir.path + "/info", "/info/", "info")
//        eLog("下载完成")
//        if (File(application.cacheDir.path + "/info").apply { file = this }.exists()) {
//            val str = file!!.readText()
//            return dataInfo(eJson.eGetJsonObject(str, "versionCode"), url, eJson.eGetJsonObject(str, "deviceID"), eJson.eGetJsonObject(str, "etFaceSetDeviceInfo"))
//        }
//        return null
//    }

    fun initFTP(FTPUrl: String = this.FTPUrl, FTPPort: Int = this.FTPPort, UserName: String = this.UserName, UserPassword: String = this.UserName): Boolean {
        this.FTPUrl = FTPUrl
        this.FTPPort = FTPPort
        this.UserName = UserName
        this.UserPassword = UserPassword
        try {
            //1.要连接的FTP服务器Url,Port
            ftpClient!!.connect(FTPUrl, FTPPort)
            //2.登陆FTP服务器
            ftpClient!!.login(UserName, UserPassword)
            //3.看返回的值是不是230，如果是，表示登陆成功
            val reply = ftpClient!!.replyCode
            if (!FTPReply.isPositiveCompletion(reply)) {
                //断开
                ftpClient!!.disconnect()
                return false
            }
            return true
        } catch (e: ConnectException) {
            eLogE("错误:$e")
            return false
        }

    }

    fun openFile(path: String = "/"): Array<out FTPFile>? {
        if (!ftpClient!!.isConnected) {
            if (!initFTP(FTPUrl, FTPPort, UserName, UserPassword)) {
                return null
            }
        }
        // 转到指定下载目录
        ftpClient!!.changeWorkingDirectory(path)
        // 列出该目录下所有文件
        return ftpClient!!.listFiles()
    }

    /**
     * 上传文件
     * @param LocalFilePath    要上传文件所在SDCard的路径
     * @param LocalFileName    要上传的文件的文件名(如：Sim唯一标识码)
     * @param FTPFilePath  要存放的文件的路径
     * @return    true为成功，false为失败
     */

    var LOCAL_CHARSET = "UTF-8"
    fun uploadFile(LocalFilePath: String, FTPFilePath: String, FTPFileName: String = "12345.jpg"): Boolean {
        if (!ftpClient!!.isConnected) {
            if (!initFTP(FTPUrl, FTPPort, UserName, UserPassword)) {
                eLog("连接失败")
                return false
            }
        }
        try {
            //设置存储路径
            ftpClient!!.controlEncoding = LOCAL_CHARSET
            ftpClient!!.makeDirectory(FTPFilePath)
            ftpClient!!.changeWorkingDirectory(FTPFilePath)
            //设置上传文件需要的一些基本信息
            ftpClient!!.bufferSize = 1024
            ftpClient!!.enterLocalPassiveMode()
            ftpClient!!.setFileType(FTP.BINARY_FILE_TYPE)
            //文件上传吧～
            val fileInputStream = FileInputStream(LocalFilePath)
            ftpClient!!.storeFile(String(FTPFileName.toByteArray(),
                    Charsets.UTF_8), fileInputStream)
            //关闭文件流
            fileInputStream.close()
            return true
        } catch (e: IOException) {
            // TODO Auto-generated catch block
            e.printStackTrace()
            return false
        }


    }


    /**
     * 下载文件
     * @param LocalFilePath  要存放的文件的路径
     * @param FTPFilePath   远程FTP服务器上的那个文件的路径
     * @param FTPFileName   远程FTP服务器上的那个文件的名字
     * @return   true为成功，false为失败
     */
    fun downLoadFile(LocalFile: String, FTPFilePath: String, FTPFileName: String): Boolean {
        if (!ftpClient!!.isConnected) {
            if (!initFTP(FTPUrl, FTPPort, UserName, UserPassword)) {
                return false
            }
        }
        try {
            ftpClient!!.setFileType(FTP.BINARY_FILE_TYPE)
            // 转到指定下载目录
            ftpClient!!.changeWorkingDirectory(FTPFilePath)
            // 列出该目录下所有文件
            val files = ftpClient!!.listFiles()
            // 遍历所有文件，找到指定的文件
            for (file in files) {
                Log.i("TAG", "downLoadFile: $file--$FTPFileName")
                val mkFile = File(LocalFile)
                if (!mkFile.exists()) {
                    mkFile.mkdirs()
                }
                if ("*" == FTPFileName) {
                    //根据绝对路径初始化文件
                    val localFile = File("$LocalFile${file.name}")
                    if (!localFile.exists()) {
                        localFile.createNewFile()
                    }
                    // 输出流
                    val outputStream = FileOutputStream(localFile)
                    // 下载文件
                    val state = ftpClient!!.retrieveFile(file.name, outputStream)
                    //关闭流
                    outputStream.close()
                    eLog("下载完成：" + file.name)
                } else
                    if (file.name == FTPFileName) {
                        //根据绝对路径初始化文件
                        val localFile = File("$LocalFile/${file.name}")
                        if (!localFile.exists()) {
                            localFile.createNewFile()
                        }
                        // 输出流
                        val outputStream = FileOutputStream(localFile)
                        // 下载文件
                        ftpClient!!.retrieveFile(file.name, outputStream)
                        //关闭流
                        outputStream.close()
                        eLog("下载完成：" + file.name)
                    }
            }
            //退出登陆FTP，关闭ftpCLient的连接

        } catch (e: IOException) {
            // TODO Auto-generated catch block
            e.printStackTrace()
        }

        return true
    }

    fun close() {
        //退出登陆FTP，关闭ftpCLient的连接
        ftpClient!!.logout()
        ftpClient!!.disconnect()
    }

}
