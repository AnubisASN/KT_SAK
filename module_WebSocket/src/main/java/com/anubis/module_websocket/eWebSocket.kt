package com.anubis.module_websocket

import android.os.Handler
import android.os.Message
import com.anubis.kt_extends.*
import com.google.gson.GsonBuilder
import kotlinx.coroutines.*
import org.jetbrains.anko.custom.async
import org.json.JSONException
import java.net.URI
import java.nio.ByteBuffer
import java.nio.charset.Charset
import javax.websocket.ClientEndpoint
import javax.websocket.ContainerProvider
import javax.websocket.OnClose
import javax.websocket.OnError
import javax.websocket.OnMessage
import javax.websocket.OnOpen
import javax.websocket.Session
import javax.websocket.WebSocketContainer


@ClientEndpoint
class eWebSocket internal constructor() {
    companion object {
        val WEBSOCKET_CONNECT_CLOSE = 0     //连接关闭
        val WEBSOCKET_CONNECT_FAILURE = -1  //连接失败
        val WEBSOCKET_CONNECT_SUCCESS = 1  //连接成功
        val WEBSOCKET_CONNECT_STOP = -2   //连接中断
        val WEBSOCKET_RECEIVE_MSG = 2    //接收消息
        val WEBSOCKET_SENDMSG_SUCCESS = 3    //消息发送成功
        val WEBSOCKET_SENDMSG_FAILURE = -3    //消息发送失败
        private var mHandler: Handler? = null
        private var isReconnect = true
        private var mReconnectTime = 10000L
        private var wsURI: URI? = null
        private var wsURL: String? = null
        private var container: WebSocketContainer? = null
        private var session: Session? = null
        private var reconJob: Job? = null
        private var client: eWebSocket? = null
        val eInit by lazy(LazyThreadSafetyMode.SYNCHRONIZED) { eWebSocket() }
    }

    fun eConnect(url: String, handler: Handler? = null, reconnect: Boolean = true, reconnectTime: Long = mReconnectTime) {
        /*WebSocket 配置*/
        isReconnect = reconnect
        mHandler = handler
        mReconnectTime = reconnectTime
        wsURL = url
        wsURI = URI.create(url)
        client = eWebSocket()
        client?.start()
    }

    fun eSendMSG(any: Any) {
        async {
            try {
                when (any) {
                    is String -> {
                        session?.asyncRemote?.sendText(any)
                        client?.handlerReceiveMSG(WEBSOCKET_SENDMSG_SUCCESS, any)
                    }
                    is ByteBuffer -> {
                        session?.asyncRemote?.sendBinary(any)
                        client?.handlerReceiveMSG(WEBSOCKET_SENDMSG_SUCCESS, Charset.forName("utf-8").decode(any).toString())
                    }
                    else -> {
                        val gson = GsonBuilder().disableHtmlEscaping().create()
                        val json = gson.toJson(any).replace("\\n", "").trim()
                        session?.asyncRemote?.sendText(json)
                        client?.handlerReceiveMSG(WEBSOCKET_SENDMSG_SUCCESS, json)
                    }
                }
            } catch (e: Exception) {
                client?.handlerReceiveMSG(WEBSOCKET_SENDMSG_FAILURE, e.toString())
            }
        }
    }

    fun eClose() {
        isReconnect = false
        GlobalScope.launch { session?.close() }
    }

    //消息封装类
    private data class receiveMSG(var address: String, var code: Int? = null, var msg: String? = null)

    private fun handlerReceiveMSG(code: Int? = null, str: String? = null) {
        eLog("webSocketMSG:$str")
        val msg = Message()
        msg.obj = receiveMSG(wsURL.toString(), code, str)
        mHandler?.sendMessage(msg)
    }

    @OnOpen
    @Throws(JSONException::class)
    fun onOpen(session: Session) {
        handlerReceiveMSG(WEBSOCKET_CONNECT_SUCCESS, "连接已打开")
    }

    @OnMessage
    fun onMessage(message: String) {
        handlerReceiveMSG(WEBSOCKET_RECEIVE_MSG, message)
    }

    @OnClose
    fun onClose() {
        handlerReceiveMSG(WEBSOCKET_CONNECT_CLOSE, "连接已关闭")
        if (isReconnect)
            reconnect()
    }

    @OnError
    fun onError(session: Session, error: Throwable) {
        handlerReceiveMSG(WEBSOCKET_CONNECT_STOP, "连接中断")
        if (isReconnect)
            reconnect()
    }

    private fun start() {
        GlobalScope.launch {
            try {
                container = ContainerProvider.getWebSocketContainer()
            } catch (ex: Exception) {
                ex.eLogE("error")
            }
            try {
                connect()
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }
    }

    //断开自动重连
    private fun reconnect() {
        if (session == null || !session!!.isOpen) {
            if (isReconnect) {
                reconJob?.cancel()
                reconJob = GlobalScope.launch {
                    delay(mReconnectTime)
                    connect()
                }
            }
        }
    }


    private fun connect() {
        if (session == null || !session!!.isOpen) {
            try {
                session = container!!.connectToServer(eWebSocket::class.java, wsURI)
                synchronized(this) {
                    try {
//                        session!!.wait()
                    } catch (e: InterruptedException) {
                        e.eLogE("connect")
                        handlerReceiveMSG(WEBSOCKET_CONNECT_FAILURE, e.toString())
                    }
                }
            } catch (e: Exception) {
                reconnect()
            }

        }
    }
}
