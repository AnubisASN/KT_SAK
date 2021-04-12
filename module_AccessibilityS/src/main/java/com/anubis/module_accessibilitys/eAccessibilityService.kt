package com.anubis.module_accessibilitys

import android.accessibilityservice.AccessibilityService
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import com.anubis.kt_extends.eLog


/**
 * Author  ： AnubisASN   on 21-4-9 下午2:24.
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
abstract open class eAccessibilityService : AccessibilityService() {
    override fun onServiceConnected() {
        super.onServiceConnected()
        eOnServiceConnected()
    }

    /**
     * 中断AccessibilityService的反馈时调用
     */
    override fun onInterrupt() {
    }

    /**
     * 页面变化回调事件
     * @param event event.getEventType() 当前事件的类型;
     *              event.getClassName() 当前类的名称;
     *              event.getSource() 当前页面中的节点信息；
     *              event.getPackageName() 事件源所在的包名
     */
    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        event?.apply {
            source.let {
                eOnAccessibilityEvent(this, packageName.toString())
            }
        }
    }

    /*事件处理*/
   abstract open fun eOnAccessibilityEvent(event: AccessibilityEvent, packageName: String)

    /*初始化*/
    abstract open fun eOnServiceConnected( )


}
