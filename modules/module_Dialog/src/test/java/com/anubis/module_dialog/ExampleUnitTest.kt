package com.anubis.module_dialog

import com.anubis.kt_extends.eLog
import org.junit.Test

import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
      val  dataList: ArrayList<String?> = arrayListOf()
        dataList.add("0")
        dataList.add("1")
        dataList.add("2")

        dataList.add(null)
//        dataList[5].eLog("data")

    }
}
