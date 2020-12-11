package com.anubis.module_ftp.Interface

import android.os.Handler
import com.anubis.module_ftp.GUI.eFTPUIs

/**
 * Author  ： AnubisASN   on 19-5-15 下午4:55.
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
interface IeFTPs {
    fun startFTPUI(GUI:Class<eFTPUIs>)
    fun closeFTPUI(GUI: androidx.appcompat.app.AppCompatActivity?)
    fun mHandlerMSG(handleMsg:Handler)
}
