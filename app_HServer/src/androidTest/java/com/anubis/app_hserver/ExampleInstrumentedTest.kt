package com.anubis.app_hserver

import com.anubis.kt_extends.eEncryption
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see [Testing documentation](http://d.android.com/tools/testing)
 */
class ExampleInstrumentedTest {
    @Test
    fun useAppContext() {
        print(eEncryption.eInit.eEncrypt("1111111111111111", "1234567891234567"))
    }
}
