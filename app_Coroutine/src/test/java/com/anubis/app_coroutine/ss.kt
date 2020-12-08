package com.anubis.app_coroutine
import com.google.gson.annotations.SerializedName


/**
 * Author  ： AnubisASN   on 20-12-8 下午5:11.
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
data class ss(
    @SerializedName("deviceName")
    val deviceName: String="deviceName",
    @SerializedName("deviceNum")
    val deviceNum: String?="deviceNum",
    @SerializedName("executeId")
    val executeId: String?="executeId",
    @SerializedName("msgType")
    val msgType: String?="msgType",
    @SerializedName("status")
    val status: Boolean?=true
)
