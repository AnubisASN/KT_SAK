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
enum class VoiceModel {
    FEMALE, MALE, EMOTIONAL_MALE, CHILDREN
//    map["离线女声"] = OfflineResource.FEMALE
//    map["离线男声"] = OfflineResource.MALE
//    map["离线度逍遥"] = OfflineResource.EMOTIONAL_MALE
//    map["离线度丫丫"] = OfflineResource.CHILDREN
}

enum class ParamMixMode {
    MIX_MODE_DEFAULT, MIX_MODE_HIGH_SPEED_SYNTHESIZE_WIFI, MIX_MODE_HIGH_SPEED_NETWORK, MIX_MODE_HIGH_SPEED_SYNTHESIZE
// MIX_MODE_DEFAULT 默认 ，wifi状态下使用在线，非wifi离线。在线状态下，请求超时6s自动转离线
// MIX_MODE_HIGH_SPEED_SYNTHESIZE_WIFI wifi状态下使用在线，非wifi离线。在线状态下， 请求超时1.2s自动转离线
// MIX_MODE_HIGH_SPEED_NETWORK ， 3G 4G wifi状态下使用在线，其它状态离线。在线状态下，请求超时1.2s自动转离线
// MIX_MODE_HIGH_SPEED_SYNTHESIZE, 2G 3G 4G wifi状态下使用在线，其它状态离线。在线状态下，请求超时1.2s自动转离线
}

enum class TTSMode {
    MIX, ONLINE
}

