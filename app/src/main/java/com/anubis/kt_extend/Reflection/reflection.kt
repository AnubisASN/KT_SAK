package com.anubis.kt_extend.Reflection

import android.app.Activity
import com.anubis.kt_extends.eShowTip

/**
 * Author  ： AnubisASN   on 2018-07-20 15:37.
 * E-mail  ： anubisasn@gmail.com ( anubisasn@qq.com )
 *  Q Q： 773506352
 *命名规则定义：
 *Module :  module_'ModuleName'
 *Library :  lib_'LibraryName'
 *Package :  'PackageName'_'Module'
 *Class :  'Mark'_'Function'_'Tier'
 *Layout :  'Module'_'Function'
 *Resource :  'Module'_'ResourceName'_'Mark'
 * /+Id :  'LoayoutName'_'Widget'+FunctionName
 *Router :  /'Module'/'Function'
 *说明：
 */
class Reflection {
    fun toastr(activity: Activity,str: String){
        activity.eShowTip(str)
    }
    fun ss(){

    }
    fun ss2(){

    }
}
