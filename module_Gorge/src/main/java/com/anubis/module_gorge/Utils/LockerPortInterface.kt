package com.anubis.module_gorge.Utils

import java.io.OutputStream

/**
 * Author  ： AnubisASN   on 18-7-16 上午8:41.
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
public interface LockerPortInterface {

    /**
     *
     * @param buffer  返回的字节数据
     * @param size    返回的字节长度
     * @param path    串口名，如果有多个串口需要识别是哪个串口返回的数据（传或不传可以根据自己的编码习惯）
     */
   fun  onLockerDataReceived(buffer: ByteArray, size:Int, path:String );

    /**
     * 串口输出流，通过该输出流向串口发送指令
     * @param outputStream
     */


    fun onLockerOutputStream(outputStream: OutputStream)
}
