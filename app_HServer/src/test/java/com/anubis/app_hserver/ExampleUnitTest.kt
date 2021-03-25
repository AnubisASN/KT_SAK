package com.anubis.app_hserver

import com.anubis.kt_extends.eJson
import kotlinx.coroutines.*
import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see [Testing documentation](http://d.android.com/tools/testing)
 */
class ExampleUnitTest {
    @Test
    suspend fun addition_isCorrect() = coroutineScope {
        val s = GlobalScope.launch(start = CoroutineStart.LAZY) {
            while (isActive) {
                println("0")
                delay(1000L)
            }
        }
        println("1")
        delay(5000L)
        println("2")
        s.start()
        delay(10000L)
    }
}
