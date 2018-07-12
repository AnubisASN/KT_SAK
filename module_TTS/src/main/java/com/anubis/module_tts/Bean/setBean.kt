package com.anubis.module_tts.Bean

/**
 * Author  ： AnubisASN   on 18-7-12 上午12:04.
 * E-mail  ： anubisasn@gmail.com ( anubisasn@qq.com )
 * HomePage： www.anubisasn.me
 *命名规则定义：
 *Module :  module_'ModuleName'
 *Library :  lib_'LibraryName'
 *Package :  'PackageName'_'Module'
 *Class :  'Mark'_'Function'_'Tier'
 *Layout :  'Module'_'Function'
 *Resource :  'Module'_'ResourceName'_'Mark'
 * /+Id :  'LoayoutName'_'Widget'+FunctionName
 *Router :  /'Module'/'Function'
 *类说明：
 */
  enum class  voiceModel{
    FEMALE,MALE,EMOTIONAL_MALE,CHILDREN
//    map["离线女声"] = OfflineResource.VOICE_FEMALE
//    map["离线男声"] = OfflineResource.VOICE_MALE
//    map["离线度逍遥"] = OfflineResource.VOICE_DUXY
//    map["离线度丫丫"] = OfflineResource.VOICE_DUYY
}
