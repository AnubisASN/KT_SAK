package com.anubis.app_coroutine

import com.anubis.kt_extends.eTime
import com.anubis.kt_extends.eTime.Companion.eITime
import kotlinx.coroutines.*
import org.junit.Test
import java.util.*
import kotlin.system.measureTimeMillis

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class CoroutineTest {
    @Test
    fun 协程延迟() {
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
    fun 隐式协程延时() = runBlocking {
        println(eITime.eGetTime("HH:mm:ss"))
        println("开始")
        launch {
            //            delay(1000L)
            for (i in 0..10) {
            }
            println("协成完成" + eITime.eGetTime("HH:mm:ss"))
        }
//        delay(2000L)
        println("主线程执行" + eITime.eGetTime("HH:mm:ss"))
        delay(2000L)
        println("结束")
    }

    @Test
    fun 等待作业() = runBlocking {
        val job = GlobalScope.launch {
            delay(1000L)
            println("等待作业完成")
            delay(3000L)
        }
        println("开始")
        job.join()
        println("作业完成")
        delay(1000L)
        println("结束")
    }

    @Test  //结构化并发
    fun 结构化并发() = runBlocking {
        launch {
            delay(2000L)
            println("协程完成")
        }
        println("开始协程")
    }

    @Test
    fun 作用域构建器1() = runBlocking {
        println(eITime.eGetTime("HH:mm:ss"))
        launch {
            delay(200L)
            println("新协程2" + eITime.eGetTime("HH:mm:ss"))
        }
        println("runBlocking作用域0" + eITime.eGetTime("HH:mm:ss"))
        coroutineScope {
            //子协程
            //会阻塞主协程runBlocking
            launch {
                delay(500L)
                println("嵌套协程3" + eITime.eGetTime("HH:mm:ss"))
            }
            delay(100L)
            println("协程作用域1" + eITime.eGetTime("HH:mm:ss"))
        }
//        delay(3000L)
        println("runBlocking作用域4" + eITime.eGetTime("HH:mm:ss"))
    }

    @Test
    fun 作用域关闭() = runBlocking {
        var job: Job? = null
        launch {
            delay(10000)
//            job1?.cancelAndJoin()
            job?.cancel()

            println(eITime.eGetTime("HH:mm:ss") + "cancel")
        }

        job = launch {
            supervisorScope {
                try {
                    launch {
                        try {
                            while (isActive) {
                                println(eITime.eGetTime("HH:mm:ss") + "等待关闭")
                                delay(1000L)
                            }
                        } catch (e: Exception) {
                            println("伴程关闭；$e")
//                                val s:Int="ss".toInt()
                        }
                    }

                    while (isActive) {
                        println(eITime.eGetTime("HH:mm:ss") + "开始接收-----")
                        delay(6000L)
                        println(eITime.eGetTime("HH:mm:ss") + "接收完成------")
                    }
                } catch (e: Exception) {
                    println(eITime.eGetTime("HH:mm:ss") + "job关闭")
                }

            }
        }

        delay(20000L)
        println(eITime.eGetTime("HH:mm:ss") + "结束")
    }

    @Test
    fun 提取函数重构() = runBlocking {
        val job = launch { 挂起() }
        job.join()
        println("结束")
    }

    suspend fun 挂起() {
        println("挂起函数")
        delay(1000L)
        println("完成")
    }

    @Test   //1s 输出10w World
    fun 启动大量协程() = runBlocking {
        repeat(100000) {
            launch {
                delay(1000L)
                println("协程$it")
            }

        }
    }

    @Test
    fun 倒计时() = runBlocking {
        GlobalScope.launch {
            repeat(10) {
                println("倒计时:${10 - it}")
                delay(1000L)
            }
            println("倒计时完成")
        }
        delay(20000)
    }


    @Test   //1s 输出10w World
    fun 全局协程守护() = runBlocking<Unit> {
        GlobalScope.launch {
            repeat(1000) {
                println("协程$it")
                delay(500L)
            }
        }
        delay(1300L)
    }

    @Test   //1s 输出10w World
    fun 取消与超时() = runBlocking {
        val job = launch {
            repeat(10000000) { i ->
                println("协成$i")
                delay(1L)
            }
        }
        delay(200L)
        println("超时")
        job.cancelAndJoin()
        println("已取消")
    }

    @Test  //协程收尾，非阻塞
    fun 协程取消0() = runBlocking {
        val job = launch {
            try {
                var i = 0
                while (isActive) {
                    println("协程${i++}")
                    delay(1000L)
                }
            } finally {
                println("协程收尾，非阻塞")
            }
        }
        delay(3000L)
        println("取消协程")
        job.cancelAndJoin()
        println("结束")
    }


    @Test //协程收尾，阻塞
    fun 协程取消1() = runBlocking {
        val job = launch {
            try {
                repeat(1000) { i ->
                    println("协程$i")
                    delay(200L)
                }
            } finally {
                withContext(NonCancellable)
                {
                    println("协程收尾，阻塞")
                    delay(3000L)
                    println("运行完成")
                }
            }

        }
        delay(2000L)
        job.cancelAndJoin()
        println("完成")
    }

    @Test  //超时协程 return Exception
    fun 超时协程0() = runBlocking {
        val result = withTimeout(3000L) {
            repeat(10) { i ->
                println("协程$i")
                delay(100L)
            }
            "执行完成"
        }
        println("Result:$result")
    }

    @Test  //超时协程 return null
    fun 超时协程1() = runBlocking {
        val result = withTimeoutOrNull(2000L) {
            repeat(10) { i ->
                println("协程$i")
                delay(100L)
            }
            "完成"
        }
        println("Result:$result")
    }


    @Test //并发计算 惰性协程
    fun 组合挂起函数() = runBlocking {
        val time1 = measureTimeMillis {
            val one = async(start = CoroutineStart.LAZY) { doSomethingUsefulOne() }
            val two = async(start = CoroutineStart.LAZY) { doSomethingUsefulTwo() }
            one.start()
            two.start()
            println("计算1：${one.await() + two.await()}")
        }
        println("耗时1：$time1")
        delay(2000L)
        //async结构化并发
        val time2 = measureTimeMillis {

            println("计算：${concurrentSum()}")
        }
        println("耗时2：$time2")
    }

    suspend fun doSomethingUsefulOne(): Int {
        delay(1000L)
        return 13
    }

    suspend fun doSomethingUsefulTwo(): Int {
        delay(5000L)
        return 29
    }

    //async结构化并发  性能优异
    suspend fun concurrentSum(): Int = coroutineScope {
        val one = async { doSomethingUsefulOne() }
        val two = async { doSomethingUsefulTwo() }
        one.await() + two.await()
    }

    @Test
    fun 调度器与线程() = runBlocking {
        launch {
            println("main runBlocking  运行在父协程的上下文上，runBlocking主协程:${Thread.currentThread().name} ")
        }
        launch(Dispatchers.Unconfined) {
            println("Unconfined  不受限的将工作在主线程中:${Thread.currentThread().name} ")
        }
        launch(Dispatchers.Default) {
            println("Default  获取默认调度器:${Thread.currentThread().name} ")
        }
        launch(newSingleThreadContext("My")) {
            println("newSingleThreadContext  获取一个新的线程:${Thread.currentThread().name} ")
        }
        delay(1000L)
        println("完成")
    }


    @Test
    fun 协程骨架() {
        GlobalScope.launch {
            println("launch不会阻塞")
        }

        runBlocking {
            println("runBlocking会阻塞")
            delay(2000L)
        }
        println("结束")
    }


}
