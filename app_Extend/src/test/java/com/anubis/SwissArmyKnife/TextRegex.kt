package com.anubis.SwissArmyKnife

import com.anubis.kt_extends.eLog
import com.anubis.kt_extends.eRegex
import com.anubis.kt_extends.eRegex.Companion.eIRegex
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
        println(eIRegex.eIsEmail("1951536@qq.com"))
        println(eIRegex.eIsEmail("1951536@gmail.com"))
        println(eIRegex.eIsEmail("1951536@qqcom"))
    }

    @Test
    fun isIDcard() {
        println(eIRegex.eIsIDCard("43100219960501101X"))
        println(eIRegex.eIsIDCard("431002200005012010"))
        println(eIRegex.eIsIDCard("43100219900001001X"))
    }

    @Test
    fun isZh() {
        println(eIRegex.eIsZh("43100219960501101X"))
        println(eIRegex.eIsZh("ss"))
        println(eIRegex.eIsZh("是"))
    }

}
