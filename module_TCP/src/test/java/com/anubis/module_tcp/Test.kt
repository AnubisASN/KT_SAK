package com.anubis.module_tcp

import android.os.Handler
import com.anubis.module_tcp.eTCP.eSocketConnect
import kotlinx.coroutines.*
import org.jetbrains.anko.custom.async
import org.junit.Test
import java.io.PrintStream
import java.net.Socket

/**
 * Author  ： AnubisASN   on 19-9-30 下午3:43.
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
class Test {
    @Test
    fun main() = runBlocking<Unit> {
        launch (){
            sA()
        }
        launch {
            sB()
        }

    }

    suspend  fun sA() = coroutineScope() {
        launch { A() }
    }

    suspend  fun sB() = coroutineScope {
        launch { B() }
    }

    suspend fun A() {
        for (i in 1..10) {
            println(i)
//            delay(1000L)
        }
    }

    suspend fun B() {
        for (i in 11..20) {
            println(i)
            delay(1000L)
        }
    }
    @Test
    fun s(){
        val ha: HashMap<String, String?> = HashMap()
        ha["1"]="01"
        ha["2"]=null
        println(ha["1"])
        println(ha["2"])
        println(ha["3"])
        println(ha.containsKey("1"))
    }


    @Test
    fun ss(){
      val s1=  eSocketConnect("192.168.1.110",3335, Handler())
        val s2=  eSocketConnect("192.168.1.123",3335, Handler())
        GlobalScope.launch {
            delay(10000)
            println("关闭通道")
//            s1.close()
        }
        runBlocking {
            delay(120*1000)
        }
    }
    fun connect(ip:String,port:Int):Socket{
        println("$ip 开始连接")
        val socket = Socket(ip, port)
        val os = PrintStream(socket.getOutputStream(), true, "utf-8")
        val `in` = socket.getInputStream()
        println("$ip 连接成功")
        GlobalScope.launch {
            while (true) {
                val buffer = ByteArray(1024)
                val count = `in`.read(buffer)
                val receiveData = String(buffer, 0, count ?: 0)
                println("$ip 接收到:$receiveData")
            }
        }
        return socket
    }

}
