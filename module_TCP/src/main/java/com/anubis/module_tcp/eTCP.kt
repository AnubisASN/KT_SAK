
package com.anubis.module_tcp

import android.os.Handler
import android.os.NetworkOnMainThreadException
import com.anubis.kt_extends.*
import com.anubis.kt_extends.eDevice.Companion.eIDevice
import com.anubis.kt_extends.eString.Companion.eIString
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
open class eTCP internal constructor() {

    companion object {
        /*客户端*/
        val HANDLER_FAILURE_CODE = -1  //连接失败
        val HANDLER_ERROR_CODE = -2   //连接错误
        val HANDLER_CLOSE_CODE = 0     //连接关闭
        val HANDLER_CONNECT_CODE = 1  //连接成功
        val HANDLER_MSG_CODE = 2    //接收消息
        val HANDLER_RECEIV_SUCC_CODE = 4    //接收线程启动成功
        val HANDLER_RECEIV_ERROR_CODE = -4    //接收线程启动失败
        /*服务端*/
        val SHANDLER_FAILURE_CODE = -11  //创建失败
        val SHANDLER_ERROR_CODE = -22   //连接失败
        val SHANDLER_CLOSE_CODE = -33     //连接关闭
        val SHANDLER_CREATE_CLOSE_CODE = -44  //服务关闭
        val SHANDLER_SUCCEED_CODE = 33     //创建成功
        val SHANDLER_CONNECT_CODE = 11  //连接成功
        val SHANDLER_MSG_CODE = 22    //接收消息
        val SHANDLER_RECEIV_SUCC_CODE = 44    //接收线程启动成功
        val SHANDLER_RECEIV_ERROR_CODE = -44    //接收线程启动失败
        private lateinit var mHandler: Handler
        private val eInit by lazy(LazyThreadSafetyMode.SYNCHRONIZED) { eTCP() }
        fun eInit(handler: Handler): eTCP {
            mHandler = handler
            return eInit
        }
    }

    /**
     *TCP客户端-----------------------------------------------------------
     */
    //客户端多线程管理器
    /*H<K,V>  K=null，V=null  ：H.containsKey ==false 完全抛弃，无任务
    * H<K,V>  K，V=null  ：H.containsKey ==true 重连
    * H<K,V>  K,V  :  H[K]==Socket  完全功能 接收，发送
    * H<K,V>  K，V=null  ：H[K] ==null 通道创建
    * */
    val eClientHashMap: HashMap<String, Socket?> = HashMap()


    /**
     * 说明：TCP客户端连接
     * @方法suspend： eSocketConnect()
     * @param ip: String；连接IP
     * @param port: Int；连接端口
     * @param isReceove: Boolean = true: 是否运行接收线程
     * @param condition: ICallBack? = null; 接收数据校验回调
     * @param reconnectionTime: Int；重连间隔差 0-10  X*10 秒 0关闭
     * @param reconnectionBlock: Unit；重连调用
     * @param msgBlock: Unit；消息回调
     */
    open suspend fun eSocketConnect(
            ip: String,
            port: Int,
            isReceove: Boolean = true,
            condition: ICallBack? = null,
            reconnectionTime: Int = 0,
            bufferSize: Int = 2048,
            reconnectionBlock: (() -> Unit)? = null,
            msgBlock: (address: String, code: Int, msg: String, HashMap<String, Socket?>) -> Unit
    ) {
        try {
            eClientHashMap["$ip:$port"] = null
            val socket = Socket(ip, port)
            msgBlock("$ip:$port", HANDLER_CONNECT_CODE, "TCP连接成功", eClientHashMap)
            if (isReceove && eClientHashMap["$ip:$port"] == null) {
                eSocketReceive(
                        ip,
                        socket,
                        eClientHashMap,
                        condition,
                        reconnectionTime,
                        bufferSize,
                        eReconnection(
                                "$ip:$port",
                                eClientHashMap,
                                reconnectionBlock
                        ),
                        msgBlock
                )
            }
            return
        } catch (e: ConnectException) {
            msgBlock("$ip:$port", HANDLER_FAILURE_CODE, "TCP服务连接失败", eClientHashMap)
            eLogE("TCP服务连接失败:$ip-$port ")
            return
        } catch (e: UnknownHostException) {
            msgBlock("$ip:$port", HANDLER_FAILURE_CODE, "未知主机异常", eClientHashMap)
            e.eLogE("未知主机异常 ")
            return
        } catch (e: Exception) {
            msgBlock("$ip:$port", HANDLER_ERROR_CODE, "未知异常:${eErrorOut(e)}", eClientHashMap)
            e.eLogE("连接异常 ")
            return
        } finally {
            reconnectionBlock?.let {
                eRestartConnect(
                        "$ip:$port",
                        eClientHashMap,
                        reconnectionTime,
                        eReconnection(
                                "$ip:$port",
                                eClientHashMap,
                                it
                        )
                )
            }
        }
    }


    /**
     *  TCP服务端----------------------------------------------------------------------------------------------------------
     */

    //   服务端多线程管理器
//服务端多线程管理器
    val eServerHashMap: HashMap<String, Socket?> = HashMap()
    private var serverSocket: ServerSocket? = null

    /**
     * 说明：TCP服务端创建
     * @方法：eServerSocket()
     * @param port: Int = 3335;本地开启端口
     * @param tcpHandler: Handler;消息回调
     * @param condition: ICallBack? = null; 接收数据校验回调
     */
    open fun eServerSocket(
        port:Int = 3335,
        bufferSize:Int=2048,
        condition: ICallBack? = null,
        returnBlock:(address: String, code: Int, msg: String, HashMap<String, Socket?>) -> Unit
    ) {
        var sSocket: Socket?
        if (serverSocket == null) {
            try {
                serverSocket = ServerSocket(port)
                returnBlock(
                        "${eIDevice.eGetHostIP()}:$port",
                        SHANDLER_SUCCEED_CODE,
                        "TCP服务端创建成功", eServerHashMap
                )
            } catch (e: Exception) {
                serverSocket = null
                returnBlock(
                        "${eIDevice.eGetHostIP()}:$port",
                        SHANDLER_FAILURE_CODE,
                        "TCP服务端创建失败", eServerHashMap
                )
                return
            }
            while (serverSocket?.isClosed == false) {
                var clienIP = ""
                var clienPort: Int = 0
                try {
                    sSocket = serverSocket!!.accept()
                    clienIP = sSocket!!.inetAddress.hostAddress
                    clienPort = sSocket.port
                    returnBlock(
                            "$clienIP:$clienPort",
                            SHANDLER_CONNECT_CODE,
                            "客户端连接成功",
                            eServerHashMap
                    )
                } catch (e: SocketException) {
                    returnBlock(
                        "${eIDevice.eGetHostIP()}:$port",
                        SHANDLER_CREATE_CLOSE_CODE,
                        "TCP服务关闭", eServerHashMap
                    )
                    continue
                }catch (e: NetworkOnMainThreadException){
                    returnBlock(
                        "$clienIP:$clienPort",
                        SHANDLER_FAILURE_CODE,
                        "主线程上的网络异常",
                        eServerHashMap
                    )
                    continue
                }catch (e: Exception) {
                    e.printStackTrace()
                    returnBlock(
                            "$clienIP:$clienPort",
                            SHANDLER_ERROR_CODE,
                            "客户端连接失败",
                            eServerHashMap
                    )
                    continue
                }
                if (eServerHashMap["$clienIP:$clienPort"] == null) {
                    eSocketReceive(
                            clienIP,
                            sSocket,
                            eServerHashMap,
                            condition,
                            0,
                            bufferSize,
                            null,
                            returnBlock
                    )
                }
            }
        }
    }

    /**
     * 说明：TCP服务端关闭
     * @方法：eCloseServer()
     * @return:Boolean
     */
    open fun eCloseServer(): Boolean {
        return try {
            eCloseTask(eServerHashMap)
            serverSocket?.close()
            serverSocket = null
            true
        } catch (e: IOException) {
            e.eLogE("tcp服务端关闭失败")
            false
        }
    }

    /**
     * 公用------------------------------------------------------------------------------------------------------
     */

    /**
     * 说明：启动TCP接收线程
     * @方法：eSocketReceive()
     * @param ip: String；连接IP
     * @param socket: Socket；管道
     * @param hashMap: HashMap<String, dataSocket?> ; 资源管理
     * @param condition: ICallBack? = null: 接收数据校验回调
     * @param reconnectionTime: Int；重连间隔差 0-10  X*10 秒 0关闭
     * @param reconnectionBlock: Unit；重连调用
     * @param msgBlock: Unit；消息回调
     * @return:Boolean
     */
     open fun eSocketReceive(
            ip: String,
            socket: Socket,
            hashMap: HashMap<String, Socket?>,
            condition: ICallBack? = null,
            reconnectionTime: Int = 0,
            bufferSize: Int = 2048,
            reconnectionBlock: (() -> Unit)? = null,
            msgBlock: (address: String, code: Int, msg: String, HashMap<String, Socket?>) -> Unit
    ): Boolean {
        var port=0
        return try {
            eLog("启动接收协程")
            val `in` = socket.getInputStream()
            val buffer = ByteArray(bufferSize)
            port = socket.port
            eLogI("Socket Port :$port")
            hashMap["$ip:$port"] = socket
            async {
                try {
                    while (hashMap["$ip:$port"] is Socket) {
                        val count = `in`!!.read(buffer)
                        val receiveData = String(buffer, 0, count, Charsets.UTF_8)
                        val Json = condition?.callCondition(buffer,receiveData,count)?: receiveData
                        msgBlock(
                                "$ip:$port",
                                if (hashMap == eClientHashMap) HANDLER_MSG_CODE else SHANDLER_MSG_CODE,
                                Json,
                                hashMap
                        )
                    }
                } catch (e: StringIndexOutOfBoundsException) {
                    e.eLogE()
                    hashMap["$ip:$port"] = null
                    msgBlock(
                            "$ip:$port",
                            if (hashMap == eClientHashMap) HANDLER_CLOSE_CODE else SHANDLER_CLOSE_CODE,
                            "TCP服务关闭,连接不可达",
                            hashMap
                    )
                } catch (e: Exception) {
                    var msg = e.toString()
                    when {
                        e.toString().contains("Socket closed") -> {
                            eCloseTask(hashMap, "$ip:$port")
                            msg = "TCP连接关闭-$e"
                        }
                        else -> hashMap["$ip:$port"] = null
                    }
                    msgBlock(
                            "$ip:$port",
                            if (hashMap == eClientHashMap) HANDLER_CLOSE_CODE else SHANDLER_CLOSE_CODE,
                            msg,
                            hashMap
                    )
                } finally {
                    reconnectionBlock?.let {
                        eRestartConnect(
                                "$ip:$port",
                                hashMap,
                                reconnectionTime,
                                it
                        )
                    }
                }
            }
            msgBlock(
                "$ip:$port",
                if (hashMap == eClientHashMap) HANDLER_RECEIV_SUCC_CODE  else SHANDLER_RECEIV_SUCC_CODE,
                "接收线程启动成功",
                hashMap
            )
            true
        } catch (e: Exception) {
            msgBlock(
                "$ip:$port",
                if (hashMap == eClientHashMap) HANDLER_RECEIV_ERROR_CODE  else SHANDLER_RECEIV_ERROR_CODE,
                "接收线程启动失败",
                hashMap
            )
            false
        }
    }

    /**
     * 说明：TCP发送
     * @方法：eSocketSend()
     * @param str: String；发送消息
     * @param address: String?=null；对象IP:Port
     * @param hashMap: HashMap<String, dataSocket?>? = eServerHashMap ; 资源管理, null不发生
     * @param symbol: String = "|" 分隔符(每次发送1024)
     * @return:Boolean
     */
    @Throws
    open fun eSocketSend(
            str: String,
            address: String? = null,
            hashMap: HashMap<String, Socket?>? = eServerHashMap,
            symbol: String = "|",
    exBlock:((PrintStream)->Unit)?=null
    ): Boolean {
        hashMap ?: return false
        //指定发送
        val type = if (hashMap == eInit.eServerHashMap) "服务端" else "客户端"
        try {
            if (address == null) {
                if (hashMap.size == 0)
                    return false
                hashMap.forEach {
                    if (it.value != null) {
                        val msgs = eIString.eInterception(str, symbol = symbol).split(symbol)
                        val os = PrintStream(it.value!!.getOutputStream(), true, "utf-8")
                        for (msg in msgs) {
                            exBlock?.let { it(os) }?:os.print(msg)
                        }
                    }
                }
                return true
            }
            if (!address.contains(":"))
                throw Exception("地址必须包含\":\",格式: IP:Port")
            if (hashMap[address] == null) {
                return false
            }
            val msgs = eIString.eInterception(str, symbol = symbol).split(symbol)
            val os = PrintStream(hashMap[address]!!.getOutputStream(), true, "utf-8")
            for (msg in msgs) {
                exBlock?.let { it(os) }?:os.print(msg)
            }
            return true
        } catch (e: Exception) {
            e.eLogE("$address-TCP $type 消息发送错误")
            return false
        }
    }

    /**
     * 说明：任务关闭
     * @方法：eCloseTask()
     * @param hashMap: HashMap<String, Socket?> ;资源管理
     * @param address: String?=null；对象IP:Port
     * @return:Boolean
     */
    private val restartHashMap: HashMap<String, Int> = HashMap()

    @Throws
    open fun eCloseTask(hashMap: HashMap<String, Socket?>, address: String? = null): Boolean {
        try {
            if (address == null || address.isEmpty()) {
                hashMap.forEach {
                    it.value?.close()
                    hashMap.remove(it.key)
                }
                return true
            } else {
                if (!address.contains(":"))
                    throw Exception("地址必须包含\":\",格式: IP:Port")
                if (!hashMap.containsKey(address))
                    return false
                hashMap[address]?.close()
                hashMap.remove(address)
                return true
            }
        } catch (e: Exception) {
            e.eLogE("${if (hashMap == eClientHashMap) "客户端" else "服务端"}连接关闭错误")
            return false
        }
    }

    /**
     * 说明：重连
     * @方法：eRestartConnect()
     * @param address: String；对象IP:Port
     * @param hashMap: HashMap<String, Socket?> ;资源管理
     * @param reconnectionTime: Int；重连间隔差 0-10  X*10 秒 0关闭
     * @param reconnectionBlock: Unit；重连调用
     */
    open fun eRestartConnect(
            address: String,
            hashMap: HashMap<String, Socket?>,
            reconnectionTime: Int = 0,
            reconnectionBlock: (() -> Unit)? = null
    ) {
        reconnectionBlock?.let {
            if (reconnectionTime in 1..10 && !eIsConnect(
                            address,
                            hashMap
                    )
            ) {
                mHandler.postDelayed({ it() }, reconnectionTime * 10000L)
                restartHashMap[address] = 0
            }
        }
    }


    /**
     * 说明：是否连接
     * @方法：eIsConnect()
     * @param address: String；对象IP:Port
     * @param hashMap: HashMap<String, Socket?> ;资源管理
     * @return:Boolean
     */
    @Throws
    open fun eIsConnect(address: String, hashMap: HashMap<String, Socket?>): Boolean {
        if (!address.contains(":"))
            throw Exception("地址必须包含\":\",格式: IP:Port")
        return hashMap[address] != null
    }


    /**
     * 说明：是否存在任务
     * @方法：eIsExistTask()
     * @param address: String?=null；对象IP:Port
     * @param hashMap: HashMap<String, Socket?> ;资源管理
     * @return:Boolean
     */
    @Throws
    open fun eIsExistTask(address: String, hashMap: HashMap<String, Socket?>): Boolean {
        if (!address.contains(":"))
            throw Exception("地址必须包含\":\",格式: IP:Port")
        return hashMap.containsKey(address)
    }


    /**
     * 说明：是否存在重连任务
     * @方法：eIsExistReTask()
     * @param address: String；对象IP:Port
     * @return:Boolean
     */
    @Throws
    fun eIsExistReTask(address: String): Boolean {
        if (!address.contains(":"))
            throw Exception("地址必须包含\":\",格式: IP:Port")
        return restartHashMap.containsKey(address)
    }

    /**
     * 说明：是否可以执行连接
     * @方法：eIsExistReTask()
     * @param address: String；对象IP:Port
     * @return:Boolean
     */
    @Throws
    fun eIsExecuteConnect(
            address: String,
            hashMap: HashMap<String, Socket?>,
            content: (() -> Unit)? = null,
            delayTime: Long = 1000
    ): Boolean {
        if (!address.contains(":"))
            throw Exception("地址必须包含\":\",格式: IP:Port")
        val status = if (eIsExistTask(address, hashMap))
            true
        else
            eIsExistReTask(address)
        content?.let {
            while (!status) {
                mHandler.postDelayed({ eIsExecuteConnect(address, hashMap, content) }, delayTime)
            }
        }
        return status
    }

    /**
     * 说明：重连装饰
     * @方法：eReconnection()
     * @param address: String；对象IP:Port
     * @param hashMap: HashMap<String, Socket?> ;资源管理
     * @param reconnectionBlock: Unit ;重连回调调用
     * @return:Boolean
     */
    @Throws
    private fun eReconnection(
            address: String,
            hashMap: HashMap<String, Socket?>,
            reconnectionBlock: (() -> Unit)? = null
    ): () -> Unit = {
        if (!address.contains(":"))
            throw Exception("地址必须包含\":\",格式: IP:Port")
        reconnectionBlock?.let {
            if (eIsExistTask(address, hashMap) && eIsExistReTask(address))
                it()
            else
                restartHashMap.remove(address).apply { eLog("完全清理：$address") }
        }
    }

    /**
     * 说明：校验过滤接口
     * @方法：eReconnection()
     * @param receiveData: String? ；接收的数据
     * @return:String?  返回处理后的数据
     */
    interface ICallBack {
        fun callCondition(data: ByteArray?,receiveData: String?,int: Int): String?
    }

}



