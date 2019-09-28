package com.anubis.SwissArmyKnife

import com.anubis.kt_extends.eString
import org.junit.Test
import java.security.interfaces.RSAPublicKey
import java.util.*
import javax.crypto.Cipher
import java.io.ByteArrayOutputStream
import java.net.ServerSocket
import java.nio.charset.Charset
import java.security.KeyFactory
import java.security.PublicKey
import java.security.spec.X509EncodedKeySpec
import kotlin.collections.HashMap


/**
 * Author  ： AnubisASN   on 18-8-30 下午2:38.
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
public class Test {

    @Test
    fun testMSG() {

        val MSG_SIG = "88c638032c4e489899b8bca77d5a3a30b1c873fb57a149afa3ea898d476cc770"
        val str = eString.eGetEncodeMD5(MSG_SIG + "20181214144314")

        println(str)
    }

    @Test
    fun test() {
        val hashMap:HashMap<Int,Int>?=HashMap()
        hashMap!![1]=111
        println(hashMap[0].toString())
    }
    @Test
    fun serversocketTest(){
        val serverSocket=ServerSocket(3335)
        println("ip:${serverSocket.inetAddress.hostAddress}")
    }
}

