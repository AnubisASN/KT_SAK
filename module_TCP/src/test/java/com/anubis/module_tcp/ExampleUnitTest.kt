package com.anubis.module_tcp

import kotlinx.coroutines.*
import org.junit.Test

import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see [Testing documentation](http://d.android.com/tools/testing)
 */
class ExampleUnitTest {
    @Test
    fun runBlocking() {
        println("开始")
        GlobalScope.launch {
            delay(1000L)
            println("协成完成")
        }
//        Thread.sleep(2000L)
        runBlocking {
            //阻塞主线程
            println("阻塞主线程")
            delay(2000L)
        }
        println("结束")
    }

    @Test
      fun 协程延迟() {
        println("开始")
        GlobalScope.launch {
            delay(1000L)
            println("协成完成")
        }
//        Thread.sleep(2000L)
        launch{
        coroutineScope{
            //阻塞主线程
            println("阻塞主线程")
            delay(2000L)
        }
        }
        println("结束")
    }
}
