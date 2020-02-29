package com.anubis.module_tcp

import android.os.Handler
import android.os.Message
import com.anubis.kt_extends.eDevice
import com.anubis.kt_extends.eLog
import com.anubis.kt_extends.eLogE
import com.anubis.kt_extends.eString
import com.anubis.module_tcp.eTCP.eClientHashMap
import com.anubis.module_tcp.eTCP.eServerHashMap
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.jetbrains.anko.custom.async
import java.io.IOException
import java.io.PrintStream
import java.net.*
import java.util.HashMap

/**
 * Author  ： AnubisASN   on 19-9-27 上午11:59.
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
 *说明：
 */
object eTCP {
    /**
     *        TCP客户端-----------------------------------------------------------
     */
    val HANDLER_FAILURE_CODE = -1  //连接失败
    val HANDLER_ERROR_CODE = -2   //连接错误
    val HANDLER_CLOSE_CODE = 0     //关闭连接
    val HANDLER_CONNECT_CODE = 1  //连接成功
    val HANDLER_MSG_CODE = 2    //接收消息

    //消息处理类
    data class receiveMSG(var ip: String, var code: Int? = null, var msg: String? = null)


    //客户端多线程管理器
    val eClientHashMap: HashMap<String, Socket?> = HashMap()


    /**
     * 说明：TCP客户端连接
     * @方法：eSocketConnect()
     * @param ip: String；连接IP
     * @param port: Int；连接端口
     * @param tcpHandler: Handler；消息回调
     * @param isReceove: Boolean = true: 是否运行接收线程
     * @return: NULL
     */
    suspend fun eSocketConnect(ip: String, port: Int, tcpHandler: Handler, condition: ICallBack? = null, isReceove: Boolean = true) {
        val msg = Message()
        try {
            eLog("ip:$ip--port:$port")
            val socket = Socket(ip, port)
//                val os = PrintStream(socket.getOutputStream(), true, "utf-8")
//                val `in` = socket.getInputStream()
            msg?.obj = receiveMSG(ip, HANDLER_CONNECT_CODE, "$port  -TCP连接成功")
            if (isReceove && eClientHashMap[ip] == null) {
                eLog("启动接收线程")
                eSocketReceive(ip, socket, tcpHandler, eClientHashMap, condition)
            }
            return
        } catch (e: ConnectException) {
            msg?.obj = receiveMSG(ip, HANDLER_FAILURE_CODE, port.toString())
            eLogE("连接失败:$e")
            return
        } catch (e: UnknownHostException) {
            msg?.obj = receiveMSG(ip, HANDLER_FAILURE_CODE, port.toString())
            eLogE("未知主机异常:$e")
            return
        } catch (e: Exception) {
            msg?.obj = receiveMSG(ip, HANDLER_ERROR_CODE, port.toString())
            e.printStackTrace()
            eLogE("连接异常:$e")
            return
        } finally {
            tcpHandler.sendMessage(msg)
        }

    }


    //    /**
//     *  TCP服务端-----------------------------------------------------------------------
//     */
    val SHANDLER_FAILURE_CODE = -11  //创建失败
    val SHANDLER_ERROR_CODE = -22   //创建错误
    val SHANDLER_CLOSE_CODE = -33     //连接关闭
    val SHANDLER_SUCCEED_CODE = 33     //创建成功
    val SHANDLER_CREATE_CLOSE_CODE = 44  //创建关闭
    val SHANDLER_CONNECT_CODE = 11  //连接成功
    val SHANDLER_MSG_CODE = 22    //接收消息
    //    //    服务端多线程管理器
//服务端多线程管理器
    val eServerHashMap: HashMap<String, Socket?> = HashMap()
    private var serverSocket: ServerSocket? = null
    /**
     * 说明：TCP服务端创建
     * @方法：eServerSocket()
     * @param port: Int = 3335;本地开启端口
     * @param tcpHandler: Handler;消息回调
     * @return:NULL
     */
    fun eServerSocket(tcpHandler: Handler, port: Int = 3335, condition: ICallBack? = null) {
        var sSocket: Socket? = null
        val msg = Message()
        try {
            if (serverSocket == null){
                serverSocket = ServerSocket(3335)
            msg.obj = eTCP.receiveMSG(eDevice.eGetHostIP(), SHANDLER_SUCCEED_CODE, "serverSocket创建成功")
            tcpHandler.sendMessage(msg)
            while (serverSocket?.isClosed == false) {
                sSocket = serverSocket!!.accept()
                val clienIP = sSocket!!.inetAddress.hostAddress
                val msg = Message()
                msg.obj = eTCP.receiveMSG(clienIP, SHANDLER_CONNECT_CODE, "有客户端连接成功")
                tcpHandler.sendMessage(msg)
                if (eServerHashMap[clienIP] == null) {
                    eSocketReceive(clienIP, sSocket, tcpHandler, eServerHashMap, condition)
                }
            }}
        }   catch (e: IOException) {
            serverSocket?.close()
            serverSocket = null
            eLogE("serverSocket服务端关闭", e)
        } catch (e: Exception) {
            serverSocket?.close()
            serverSocket = null
            msg.obj = eTCP.receiveMSG(sSocket!!.inetAddress.hostAddress, SHANDLER_ERROR_CODE, "serverSocket客户端连接失败")
            eLogE("initServerSocket Exception错误", e)
            tcpHandler.sendMessage(msg)
        }
    }

    /**
     * 说明：TCP服务端关闭
     * @方法：eCloseServer()
     * @return:Boolean
     */
    fun eCloseServer(): Boolean {
        return try {
            eCloseReceives(eServerHashMap)
            serverSocket?.close()
            serverSocket = null
            true
        } catch (e: IOException) {
            eLogE("tcp服务端关闭失败", e)
            false
        }
    }


    /**
     * 说明：启动TCP接收线程  共用----------------------------------------------------------
     * @方法：eSocketReceive()
     * @param ip: String；连接IP
     * @param tcpHandler: Handler；消息回调
     * @param hashMap: HashMap<String, dataSocket> ;线程管理器
     * @param isReceove: Boolean = true: 是否自动运行接收线程
     * @return:Boolean
     */
    fun eSocketReceive(ip: String, socket: Socket, tcpHandler: Handler, hashMap: HashMap<String, Socket?>, condition: ICallBack? = null): Boolean {
        return try {
            eLog("启动接收")
            val msg = tcpHandler.obtainMessage()
            val `in` = socket.getInputStream()
            val buffer = ByteArray(1024)
            hashMap[ip] = socket
            async {
                try {
                    while (hashMap.containsKey(ip)) {
                        val count = `in`!!.read(buffer)
                        val receiveData = String(buffer, 0, count)
                        val msg = Message()
                        msg.obj = receiveMSG(ip, if (hashMap == eClientHashMap) HANDLER_MSG_CODE else SHANDLER_MSG_CODE, receiveData)
                        tcpHandler.sendMessage(msg)
                    }
                } catch (e: StringIndexOutOfBoundsException) {
                    msg.obj = receiveMSG(ip, if (hashMap == eClientHashMap) HANDLER_CLOSE_CODE else SHANDLER_CLOSE_CODE, "StringIndexOutOfBoundsException")
                } catch (e: Exception) {
                    msg.obj = receiveMSG(ip, if (hashMap == eClientHashMap) HANDLER_CLOSE_CODE else SHANDLER_CLOSE_CODE, "Exception")
                    eLogE("$ip  接收线程错误", e)
                } finally {
                    hashMap[ip] = null
                    tcpHandler.sendMessage(msg)
                }
            }
            true
        } catch (e: Exception) {
            false
        }
    }


    /**
     * 说明：TCP发送
     * @方法：eSocketSend()
     * @param ip: String；对象IP
     * @param str: String；发送消息
     * @param hashMap: HashMap<String, dataSocket> ;线程管理器
     * @return:Boolean
     */
    fun eSocketSend(str: String, ip: String? = null, hashMap: HashMap<String, Socket?> = eServerHashMap, symbol: String = "|"): Boolean {
        //指定发送
        eLog("str:$str--ip:$ip")
        val type = if (hashMap == eTCP.eServerHashMap) "服务端" else "客户端"
        try {
            if (ip == null) {
                hashMap.forEach {
                    if (it.value != null) {
                        val msgs = eString.eInterception(str, symbol = symbol).split(symbol)
                        val os = PrintStream(it.value!!.getOutputStream(), true, "utf-8")
                        for (msg in msgs) {
                            os.print(msg)
                        }
                    }
                }
                return true
            }
            if (hashMap[ip] == null) {
                return false
            }
            val msgs = eString.eInterception(str, symbol = "|").split("|")
            val os = PrintStream(hashMap[ip]!!.getOutputStream(), true, "utf-8")
            for (msg in msgs) {
                os.print(msg)
            }
            return true
        } catch (e: Exception) {
            com.anubis.kt_extends.eLogE("$ip-TCP $type 消息发送错误", e)
            return false
        }
    }

    /**
     * 说明：接收线程关闭
     * @方法：eCloseReceives()
     * @param hashMap: HashMap<String, dataSocket> ;线程管理器
     * @param ip: String；对象IP
     * @param isClear: Boolean = true； 是否启用线程暂停功能
     * @return:Boolean
     * 说明：用于关闭接收线程，可关闭管理端连接
     */
    fun eCloseReceives(hashMap: HashMap<String, Socket?>, ip: String? = null): Boolean {
        try {
            if (ip == null || ip.isEmpty()) {
                hashMap.forEach {
                    it.value?.close()
                    hashMap.remove(it.key)
                }
                return true
            } else {
                if (hashMap[ip] == null) {
                    return false
                }
                hashMap[ip]!!.close()
                hashMap.remove(ip)
                return true
            }
        } catch (e: Exception) {
            eLogE("${if (hashMap == eClientHashMap) "客户端" else "服务端"}连接关闭错误", e)
            return false
        }
    }


    interface ICallBack {
        fun callCondition(receiveData: String?): String?
    }

}
//回调处理示例
//private fun handleTCP(msg: Message) {
//    val obj = msg.obj as eTCP.receiveMSG
//    when (obj.code) {
////                val HANDLER_FAILURE_CODE = -1  //连接失败
//        eTCP.HANDLER_FAILURE_CODE -> mainActivity!!.Hint("TCP客户端-${obj.ip}：${obj.msg}  连接失败")
////                val HANDLER_ERROR_CODE = -2   //连接错误
//        eTCP.HANDLER_ERROR_CODE -> mainActivity!!.Hint("TCP客户端-${obj.ip}：${obj.msg}  连接错误")
////                val HANDLER_CLOSE_CODE = 0     //关闭连接
//        eTCP.HANDLER_CLOSE_CODE -> mainActivity!!.Hint("TCP客户端-${obj.ip}：${obj.msg}  连接关闭")
////                val HANDLER_CONNECT_CODE = 1  //连接成功
//        eTCP.HANDLER_CONNECT_CODE -> mainActivity!!.Hint("TCP客户端-${obj.ip}：${obj.msg}  连接成功")
////                val HANDLER_MSG_CODE = 2    //接收消息
//        eTCP.HANDLER_MSG_CODE -> mainActivity!!.Hint("TCP客户端-${obj.ip}：接收到 ${obj.msg}")
//
////                val SHANDLER_FAILURE_CODE = -11  //创建失败
//        eTCP.SHANDLER_FAILURE_CODE -> mainActivity!!.Hint("TCP服务端-${obj.ip}：${obj.msg}  创建失败")
////                val SHANDLER_ERROR_CODE = -22   //创建错误
//        eTCP.SHANDLER_ERROR_CODE -> mainActivity!!.Hint("TCP服务端-${obj.ip}：${obj.msg}  创建错误")
////                val SHANDLER_CLOSE_CODE = -33     //连接关闭
//        eTCP.SHANDLER_CLOSE_CODE -> mainActivity!!.Hint("TCP服务端-${obj.ip}：${obj.msg}  连接关闭")
////                val SHANDLER_SUCCEED_CODE = 33     //创建成功
//        eTCP.SHANDLER_SUCCEED_CODE -> mainActivity!!.Hint("TCP服务端-${obj.ip}：${obj.msg}  创建成功")
////                val SHANDLER_CONNECT_CODE = 11  //连接成功
//        eTCP.SHANDLER_CONNECT_CODE -> mainActivity!!.Hint("TCP服务端-${obj.ip}：${obj.msg}  连接成功")
////                val SHANDLER_MSG_CODE = 22    //接收消息
//        eTCP.SHANDLER_MSG_CODE -> mainActivity!!.Hint("TCP服务端-${obj.ip}：接收到${obj.msg}")
//    }
//}

