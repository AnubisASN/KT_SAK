package com.anubis.app_webrtc

import android.util.Log
import com.anubis.kt_extends.eErrorOut
import com.anubis.kt_extends.eLog
import com.tencent.bugly.proguard.s
import org.json.JSONObject
import org.junit.Test

import java.io.File

import org.junit.Assert.*
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see [Testing documentation](http://d.android.com/tools/testing)
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        val str = "12"
        println(setY(12))
        println(setY(false))
        println(setY(""))
    }

    fun <T> setY(y: T): T {
        y as Any
        print(y.toString() + ":" + y.javaClass.simpleName)
        val s = when (y) {
            is Int -> 0
            is Boolean -> true
            else -> "ss"
        }
        return s as T
    }


}
