package com.anubis.app_float_ball
import com.google.gson.annotations.SerializedName


/**
 * Author  ： AnubisASN   on 21-3-22 下午4:05.
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
class resultData : ArrayList<resultDataItem>()

data class resultDataItem(
    @SerializedName("desc")
    val desc: Any?,
    @SerializedName("height")
    val height: Int?,
    @SerializedName("id")
    val id: Int?,
    @SerializedName("path")
    val path: String?,
    @SerializedName("width")
    val width: Int?
)
