package com.anubis.SwissArmyKnife

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle


/**
 * Author  ： AnubisASN   on 2018-07-26 14:27.
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
class  Excel: AppCompatActivity() {
    private var mRunnable: Runnable? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera)
//        val da=dataTest("s","ss")
//        ExportExcel(this, arrayOf(""), mutableListOf(),dataTest)
    }
}
