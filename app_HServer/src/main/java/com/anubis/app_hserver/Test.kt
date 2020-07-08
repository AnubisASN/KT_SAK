package com.anubis.app_hserver

import com.anubis.kt_extends.eLogI

/**
 * Author  ： AnubisASN   on 20-7-7 下午3:54.
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
class Test private constructor(){
    var s2:String="2"
    companion object{
        var s1:String="1"
        val eInit by lazy(LazyThreadSafetyMode.SYNCHRONIZED) { Test() }
    }

  fun  init() {
        s1="ss1"
        s2="ss2"
        eLogI("Test 类加载")
    }
}
