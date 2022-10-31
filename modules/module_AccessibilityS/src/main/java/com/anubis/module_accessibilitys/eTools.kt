package com.anubis.module_accessibilitys

import android.accessibilityservice.AccessibilityService
import android.content.ComponentName
import android.content.Context
import android.graphics.Rect
import android.provider.Settings
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import com.anubis.kt_extends.eApp
import com.anubis.kt_extends.eLog
import com.anubis.kt_extends.eLogE
import com.anubis.module_accessibilitys.test.testServer


/**
 * Author  ： AnubisASN   on 21-4-10 下午3:08.
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
open class eTools internal constructor() {
    companion object {
        private   var mAccessibilityService: AccessibilityService?=null
        private val eInit by lazy(LazyThreadSafetyMode.SYNCHRONIZED) { eTools() }
        fun eInit(accessibilityService: AccessibilityService): eTools {
            mAccessibilityService = accessibilityService
            return eInit
        }
    }
    /*根据控件text点击*/
    open  fun eClickTypeText(event: AccessibilityEvent, text: String, widgetType:Class<*>?=null,actionType:Int=AccessibilityNodeInfo.ACTION_CLICK) {
        // 事件页面节点信息不为空
        if (event.source != null) {
            // 根据Text搜索所有符合条件的节点, 模糊搜索方式; 还可以通过ID来精确搜索findAccessibilityNodeInfosByViewId
            val stop_nodes = event.source.findAccessibilityNodeInfosByText(text)
            // 遍历节点
            if (stop_nodes != null && stop_nodes.isNotEmpty()) {
                var node: AccessibilityNodeInfo
                for (i in stop_nodes.indices) {
                    node = stop_nodes[i]
                    // 判断按钮类型
                    val state=widgetType?.let { node.className== widgetType.name }?:true
                    if (state) {
                        // 可用则模拟点击
                        if (node.isEnabled) {
                            node.performAction(actionType)
                        }
                    }
                }
            }
        }
    }

    /*根据 viewId 点击*/
    open    fun eClickViewId(viewId: Int,actionType:Int=AccessibilityNodeInfo.ACTION_CLICK) {
        mAccessibilityService?:return
        val root: AccessibilityNodeInfo = mAccessibilityService!!.rootInActiveWindow
        val infoList = root.findAccessibilityNodeInfosByViewId(viewId.toString())
        if (infoList != null && infoList.size > 0) {
            for (nodeInfo in infoList) {
                nodeInfo?.let {
                    it.performAction(actionType)
                }
            }
        } else {
            eLogE("$viewId = is null")
        }
    }

    /*根据文字获取节点信息*/
    open fun eGetNodeByText(text: String):List<AccessibilityNodeInfo>? {
        mAccessibilityService?:return null
        val root: AccessibilityNodeInfo = mAccessibilityService!!.rootInActiveWindow
        val infoList = root.findAccessibilityNodeInfosByText(text)
        return  infoList
    }

    /*根据ID获取节点信息*/
    open fun eGetNodeById(viewId: Int):List<AccessibilityNodeInfo>? {
        mAccessibilityService?:return null
        val root: AccessibilityNodeInfo = mAccessibilityService!!.rootInActiveWindow
        val infoList = root.findAccessibilityNodeInfosByViewId(viewId.toString())
        return  infoList
    }
    /*获取组件节点区域坐标*/
    open fun eGetNodeRect(nodeInfo: AccessibilityNodeInfo):Rect {
        val rect = Rect()
        nodeInfo.getBoundsInScreen(rect) //相对于屏幕的位置
        return rect
    }


    /*--------------------------------*/

    /**
     * 设置ListView列表逐行往下滚动（GridView也类似）
     */
    open   fun eListViewScrollDown(viewId: Int,position:Int?=null,onclick:((String)->Unit)?=null) {
        mAccessibilityService?:return
        val root = mAccessibilityService!!.rootInActiveWindow ?: return
        val infoList = root.findAccessibilityNodeInfosByViewId(viewId.toString())
        if (infoList != null && infoList.size > 0) {
            infoList.size.eLog("infoList size")
            val nodeInfo = infoList[0]
            if (nodeInfo != null&& nodeInfo.className .contains("ListView") ) {
                for (k in 0 until (position?:nodeInfo.childCount)) {
                    val child = nodeInfo.getChild(k)
                    if (child != null) {
                        //逐行滚动。
                        child.performAction(AccessibilityNodeInfo.ACTION_ACCESSIBILITY_FOCUS)
                        child.performAction(AccessibilityNodeInfo.ACTION_SELECT)
                        onclick?.let { it(child.text.toString()) }
                        child.viewIdResourceName.eLog("viewIdResourceName")
                    }
                }
            }
        }
    }


    /**
     * 设置ListView滚动到顶部（GridView也类似）
     * @param viewId
     */
    open   fun eListViewScrollTop(viewId: Int) {
        mAccessibilityService?:return
        val root = mAccessibilityService!!.rootInActiveWindow ?: return
        val infoList = root.findAccessibilityNodeInfosByViewId(viewId.toString())
        if (infoList != null && infoList.size > 0) {
            val nodeInfo = infoList[0]
            if (nodeInfo != null && nodeInfo.className.contains("ListView")) {
                nodeInfo.performAction(AccessibilityNodeInfo.ACTION_SCROLL_BACKWARD)
            }
        }
    }

    /**
     * 设置选中列表指定item并触发点击事件，（GridView也类似）
     * infoList的大小为当前可见item数量，position的值为当前列表item的位置
     * @param viewId
     * @param position
     */
    open  fun eSelectedListViewItem(viewId: Int, position: Int) {
        mAccessibilityService?:return
        val root = mAccessibilityService!!.rootInActiveWindow ?: return
        val infoList = root.findAccessibilityNodeInfosByViewId(viewId.toString())
        if (infoList != null && infoList.size > 0) {
            val nodeInfo = infoList[0]
            if (nodeInfo != null && nodeInfo.className.contains("ListView")) {
                val childCount = nodeInfo.childCount
                if (position >= 0 && position <= childCount - 1) {
                    val child = nodeInfo.getChild(position)
                    if (child != null) {
                        child.performAction(AccessibilityNodeInfo.ACTION_ACCESSIBILITY_FOCUS)
                        child.performAction(AccessibilityNodeInfo.ACTION_SELECT)
                        child.performAction(AccessibilityNodeInfo.ACTION_CLICK)
                    }
                } else {
                    val child = nodeInfo.getChild(0)
                    child.performAction(AccessibilityNodeInfo.ACTION_CLICK)
                }
            }
        }
    }
}
