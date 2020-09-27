package com.anubis.module_extends

/**
 * Author  ： AnubisASN   on 20-9-16 上午11:06.
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
data class DataItemInfo(var id:Long?=null,var isCb:Boolean?=null, var str1:String?=null, var str2:String?=null, var ico: Any?=null, var str3:String?=null, var str4:String?=null, var isShowLine:Boolean=false,var color: Int?=null)
