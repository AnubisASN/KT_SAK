package com.anubis.SwissArmyKnife

import android.app.Activity
import android.util.Base64
import com.anubis.kt_extends.eEncryption
import com.anubis.kt_extends.eEncryption.Companion.eIEncryption
import com.anubis.kt_extends.eJson
import com.anubis.kt_extends.eString
import com.google.gson.Gson
import com.tencent.bugly.crashreport.common.info.b.t
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
        val ss=   Base64.encodeToString("sss123S".toByteArray(Charsets.UTF_8), Base64.DEFAULT)
//       eIEncryption.eBase64Decode("SSS123")
        println("加密："+ss)
//        println("解密："+eIEncryption.eBase64Decode(ss))
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

