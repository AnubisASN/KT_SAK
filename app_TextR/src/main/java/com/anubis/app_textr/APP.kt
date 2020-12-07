package com.anubis.app_textr

import android.app.Application
import com.anubis.uuzuche.lib_zxing.activity.ZXingLibrary

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
class picekerAPP: Application() {
    override fun onCreate() {
        super.onCreate()
    }
}
