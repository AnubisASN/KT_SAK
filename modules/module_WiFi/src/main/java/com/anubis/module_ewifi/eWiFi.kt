package com.anubis.module_ewifi

import android.content.Context
import android.net.wifi.ScanResult
import android.net.wifi.WifiConfiguration
import android.net.wifi.WifiInfo
import android.net.wifi.WifiManager
import com.anubis.kt_extends.eLog
import com.anubis.kt_extends.eLogE
import com.anubis.kt_extends.eShowTip
import java.lang.reflect.InvocationTargetException

/**
 * Author  ： AnubisASN   on 18-9-10 下午3:42.
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
 *说明：eWiFi热点
 * @初始化方法：eCreateWifiHotspot()
 * @param Gcontext: Context；意图
 * @param SSID: String；WiFi名
 * @param PSW: String; WiFi密码
 * @param HiddenSSID: Boolean； 是否隐藏
 * @return Hint:String; 返回提示
 * @设置方法：eCloseWifiHotspot()
 * @param context:Context; 意图
 * @return result:Boolean; 返回提示
 * @wifi扫描方法：eGetScanWiFi（）
 * @param context:Context; 意图
 * @return mWifiList:MutableList<ScanResult>;信息列表
 */
/**
 * 创建Wifi热点-------------------------------------------------------------------------------------
 */
open class eWiFi internal  constructor(){
    companion object{
        val eIWiFi by lazy(LazyThreadSafetyMode.SYNCHRONIZED) { eWiFi() }
    }
    private var mWifiManager: WifiManager? = null
    private var mWifiInfo: WifiInfo? = null
    //创建热点
    open fun eCreateWifiHotspot(context: Context, SSID: String = "AnubisASN", PSW: String = "anubisasn", HiddenSSID: Boolean = true): String {
        mWifiManager = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        mWifiInfo = mWifiManager!!.connectionInfo
        if (mWifiManager!!.isWifiEnabled) {
            //如果wifi处于打开状态，则关闭wifi,
            mWifiManager!!.isWifiEnabled = false
        }
        val config = WifiConfiguration()
        config.SSID = SSID
        config.preSharedKey = PSW
        config.hiddenSSID = HiddenSSID
        config.allowedAuthAlgorithms
                .set(WifiConfiguration.AuthAlgorithm.OPEN)//开放系统认证
        config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP)
        config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK)
        config.allowedPairwiseCiphers
                .set(WifiConfiguration.PairwiseCipher.TKIP)
        config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP)
        config.allowedPairwiseCiphers
                .set(WifiConfiguration.PairwiseCipher.CCMP)
        config.status = WifiConfiguration.Status.ENABLED
        //通过反射调用设置热点
        val method = mWifiManager!!::class.java.getMethod(
                "setWifiApEnabled", WifiConfiguration::class.java, java.lang.Boolean.TYPE)
        val enable = method.invoke(mWifiManager, config, true) as Boolean
        return if (enable) {
            "热点已开启\nSSID:$SSID \nPassword:$PSW"
        } else {
            "创建热点失败"
        }
    }


    //关闭热点
    open fun eCloseWifiHotspot(context: Context): Boolean {
        try {
            val mWifiManager = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
            val method = mWifiManager::class.java.getMethod("getWifiApConfiguration")
            method.isAccessible = true
            val config = method.invoke(mWifiManager) as WifiConfiguration
            val method2 = mWifiManager::class.java.getMethod("setWifiApEnabled", WifiConfiguration::class.java, Boolean::class.javaPrimitiveType)
            method2.invoke(mWifiManager, config, false)
            return true
        } catch (e: NoSuchMethodException) {
            e.eLogE("关闭热点错误 ")
            return false
        } catch (e: IllegalArgumentException) {
            e.  eLogE("关闭热点错误 ")
            return false
        } catch (e: IllegalAccessException) {
            e. eLogE("关闭热点错误 ")
            return false
        } catch (e: InvocationTargetException) {
            e. eLogE("关闭热点错误 ")
            return false
        }

    }



    //Wifi扫描结果
    open fun eGetScanWiFi(context: Context): MutableList<ScanResult>? {
        mWifiManager = context.getApplicationContext().getSystemService(Context.WIFI_SERVICE) as WifiManager
        mWifiInfo = mWifiManager!!.connectionInfo
        if (!mWifiManager!!.isWifiEnabled) {
            //如果wifi处于打开状态，则关闭wifi,
            mWifiManager!!.isWifiEnabled = true
        }
        mWifiManager!!.startScan()
        // 得到扫描结果
        val mWifiList = mWifiManager!!.scanResults
        eLog("scan:"+mWifiList.toString())
        // 得到配置好的网络连接
        return if (mWifiList == null) {
            when {
                mWifiManager!!.wifiState == 3 -> context.eShowTip("此区域无WIFI网络可连接")
                mWifiManager!!.wifiState == 2 -> context.eShowTip("WIFI正在开启，请稍后重新点击扫描")
                else -> context.eShowTip("WIFI没有开启，无法扫描")
            }
            null
        } else {
            mWifiList
        }
    }




    // 得到MAC地址
    fun eGetMacAddress() = mWifiInfo?.macAddress ?: "NULL"

    // 得到接入点的BSSID
    fun eGetBSSID() = mWifiInfo?.ssid ?: "NULL"

    // 得到IP地址
    fun eGetIPAddress() = mWifiInfo?.ipAddress ?: 0

    // 得到连接的ID
    fun eGetNetworkId() = mWifiInfo?.networkId ?: 0

    // 得到WifiInfo的所有信息包
    fun eGetWifiInfo() = mWifiInfo?.toString()
}
