package com.anubis.app_discern

import com.anubis.kt_extends.eEncryption
import com.anubis.kt_extends.eEncryption.Companion.eIEncryption
import org.junit.Test
import java.util.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see [Testing documentation](http://d.android.com/tools/testing)
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        val hash = HashMap<String, String>()
        hash["appId"] = "123"

        println(eIEncryption.eEncrypt("123","111111111111111"))
    }

    @Test
    fun s() {
        for (i in 0..4){
            for (j in 0..i)
                print("*")
            println("")
        }
    }
}
