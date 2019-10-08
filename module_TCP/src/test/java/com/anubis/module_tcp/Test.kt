package com.anubis.module_tcp

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.junit.Test

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
}
