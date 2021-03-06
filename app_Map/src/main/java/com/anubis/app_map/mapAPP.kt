package com.anubis.app_map
import android.app.Application

/**
 * Author  ： AnubisASN   on 19-6-29 下午3:14.
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
class mapAPP : Application() {
    companion object {
        var mAPP: mapAPP? = null
    }

    override fun onCreate() {
        mAPP = this
        super.onCreate()
    }
}
