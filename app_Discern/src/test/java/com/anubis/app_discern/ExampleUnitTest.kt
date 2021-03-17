package com.anubis.app_discern

import com.anubis.kt_extends.eEncryption
import org.junit.Assert
import org.junit.Test
import java.util.HashMap

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

        println(eEncryption.eInit.eMD5Sign("123",hash))
        println(eEncryption.eInit.eEncrypt("123","111111111111111"))
    }
}
