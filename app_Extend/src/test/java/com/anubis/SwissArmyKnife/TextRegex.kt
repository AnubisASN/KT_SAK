package com.anubis.SwissArmyKnife

import com.anubis.kt_extends.eLog
import com.anubis.kt_extends.eRegex
import com.anubis.kt_extends.eString
import org.junit.Test

/**
 * Author  ： AnubisASN   on 18-9-29 上午11:03.
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
class TextRegex {
    @Test
    fun isEmail() {
        println(eRegex.eInit.eIsEmail("1951536@qq.com"))
        println(eRegex.eInit.eIsEmail("1951536@gmail.com"))
        println(eRegex.eInit.eIsEmail("1951536@qqcom"))
    }

    @Test
    fun isIDcard() {
        println(eRegex.eInit.eIsIDCard("43100219960501101X"))
        println(eRegex.eInit.eIsIDCard("431002200005012010"))
        println(eRegex.eInit.eIsIDCard("43100219900001001X"))
    }

    @Test
    fun isZh() {
        println(eRegex.eInit.eIsZh("43100219960501101X"))
        println(eRegex.eInit.eIsZh("ss"))
        println(eRegex.eInit.eIsZh("是"))
    }

}
