package com.anubis.module_xpath

import android.content.Context
import com.anubis.kt_extends.eFile
import com.anubis.kt_extends.eFile.Companion.eIFile
import com.anubis.kt_extends.eGetExternalCacheDir
import com.anubis.kt_extends.eLogE
import com.anubis.module_xpath.support.SkinCompatManager
import java.io.File

/**
 * Author  ： AnubisASN   on 21-1-12 上午11:29.
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
 *说明： Application Skin 初始化 SkinCompatManager.init(this).loadSkin()
 */
class eSkin private constructor() {
    companion object {
        val eISkin by lazy(LazyThreadSafetyMode.SYNCHRONIZED) { eSkin() }
    }

    /**
     * eLoadSkin 说明：指定皮肤插件, 并且监听加载状态
     * @param skinName: String;皮肤包名 xx.skin or 文件路径 xx/xx/x.skin
     * @param listener: SkinCompatManager.SkinLoaderListener? = null; 状态监听器
     */
    fun eLoadSkin(skinName: String, listener: SkinCompatManager.SkinLoaderListener? = null) = SkinCompatManager.getInstance().loadSkin(skinName, listener)


    /*加载外部皮肤资源*/
    fun eLoadSkinExtend(context: Context,filePath: String,listener: SkinCompatManager.SkinLoaderListener? = null) {
        if (filePath.isBlank())
            return Unit.apply { eLogE("filePath==\"\"") }
        if (!File(filePath).exists())
            return Unit.apply { eLogE("not exists") }
        if (filePath.toLowerCase().indexOf(".skin") == -1)
            return Unit.apply { eLogE("非皮肤包") }
          eGetExternalCacheDir(context)?.let {
              eIFile.eCopyFile(filePath,it+"/skins")
          }
        eLoadSkin(filePath.split("/").last(),listener)
    }

    /*恢复应用默认皮肤*/
    fun eDefaultTheme() = SkinCompatManager.getInstance().restoreDefaultTheme()

    /*获取所有皮肤*/
    fun eGetSkins(context: Context):Array<File>?=File(context.externalCacheDir?.path+"/skins").listFiles()
}
