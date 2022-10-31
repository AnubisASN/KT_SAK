package com.anubis.module_accessibilitys.test

import android.view.accessibility.AccessibilityEvent
import android.widget.Button
import com.anubis.kt_extends.eLog
import com.anubis.module_accessibilitys.R
import com.anubis.module_accessibilitys.eAccessibilityService
import com.anubis.module_accessibilitys.eTools


/**
 * Author  ： AnubisASN   on 21-4-10 下午3:31.
 * E-mail  ： anubisasn@gmail.com ( anubisasn@qq.com )
 * Q Q： 773506352
 * 命名规则定义：
 * Module :  module_'ModuleName'
 * Library :  lib_'LibraryName'
 * Package :  'PackageName'_'Module'
 * Class :  'Mark'_'Function'_'Tier'
 * Layout :  'Module'_'Function'
 * Resource :  'Module'_'ResourceName'_'Mark'
 * Layout Id :  'LoayoutName'_'Widget'_'FunctionName'
 * Class Id :  'LoayoutName'_'Widget'+'FunctionName'
 * Router :  /'Module'/'Function'
 * 说明：
 */
class testServer : eAccessibilityService() {
    override fun eOnServiceConnected() {
    }

    override fun eOnAccessibilityEvent(event: AccessibilityEvent, packageName: String) {
        when (packageName) {
            this.packageName -> {
                with(eTools.eInit(this)) {
                    eClickTypeText(event, "测试", Button::class.java)
//                    if (  eGetNodeById(R.id.button4.toString())?.get(0)?.text.eLog("text")=="ID点击"){
//                        eClickViewId(R.id.button4.toString())
//                    }

//                    eGetNodeByText("ID点击")?.let {
//                        it.size.eLog("数量")
//                        it[0].getChild()
//                        it[0].getChild(0).viewIdResourceName.eLog("ID")
////                        eClickViewId()
//                    }

                    eGetNodeRect( eGetNodeByText("ID点击")!![0])
                }
            }
        }
    }
}
