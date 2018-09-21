package com.anubis.SwissArmyKnife

import com.anubis.SwissArmyKnife.Utils.Base64Utils
import com.anubis.SwissArmyKnife.Utils.RSA
import com.anubis.SwissArmyKnife.Utils.RSA.KEY_ALGORITHM
import com.anubis.kt_extends.eString
import org.junit.Test
import java.security.interfaces.RSAPublicKey
import java.util.*
import javax.crypto.Cipher
import java.io.ByteArrayOutputStream
import java.nio.charset.Charset
import java.security.KeyFactory
import java.security.PublicKey
import java.security.spec.X509EncodedKeySpec
import javax.crypto.BadPaddingException


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
    val publicKey = "MFwwDQYJKoZIhvcNAQEBBQADSwAwSAJBAIC9NtnSMnBKa3r9wQYWtQLLxBdDn8O+gUynfQk466URiB8ECaD9gSObIKWskYWTIMbucAWI1JK7iSwWReRzOMcCAwEAAQ=="
    val wcData = "AQAHAHpoaXhpYW8CAEAAlL2qB3/TjptS285cX1bDmPzA8kbHx2yx/T3Da+QiIvaUpbdD8pJlYhTL5YwykWzNOPBvj/Y4JVxfN8sHr86BBg=="
    val mwData = "94BDAA077FD38E9B52DBCE5C5F56C398FCC0F246C7C76CB1FD3DC36BE42222F694A5B743F292656214CBE58C32916CCD38F06F8FF638255C5F37CB07AFCE8106"
    val CHARSET = "GBK"
    val RSA_ALGORITHM = "RSA"
    var i = 1
    @Test
    fun Main() {
        val bData = Base64.getDecoder().decode(wcData)
        print("bData：" + eString.eGetToHexString(bData))

//        try {
//            print("data:"+RSA.decryptByPublicKey(bData, publicKey,i))
//        } catch (e: BadPaddingException) {
//            i+=i
//            print(i.toString()+"解码错误："+e)
//            Main()
//        }
        print("data:" + publicDecrypt(bData, publicKey))
    }

    /**
     * 公钥解密
     * @param data
     * @param publicKey
     * @return
     */
    var   cipher = Cipher.getInstance(RSA_ALGORITHM)
    fun publicDecrypt(data: ByteArray, publicKey: String): String {
        try {
            val keyBytes = Base64Utils.decode(publicKey)
            val x509KeySpec = X509EncodedKeySpec(keyBytes)
            val keyFactory = KeyFactory.getInstance(KEY_ALGORITHM)
            val publicK: PublicKey = keyFactory.generatePublic(x509KeySpec)


            cipher!!.init(Cipher.DECRYPT_MODE, publicK)
            val key = getPublicKey(publicKey)
            return String(rsaSplitCodec(cipher, Cipher.DECRYPT_MODE, data,  key.modulus.bitLength()), Charset.forName("GBK"))
        } catch (e: Exception) {
            throw RuntimeException("解密字符串[$data]时遇到异常", e)
        }
    }

    fun getPublicKey(publicKey: String): RSAPublicKey {
        //通过X509编码的Key指令获得公钥对象
        val keyFactory = KeyFactory.getInstance(RSA_ALGORITHM)
        val x509KeySpec = X509EncodedKeySpec(Base64.getDecoder().decode(publicKey))
        return keyFactory.generatePublic(x509KeySpec) as RSAPublicKey
    }

    private fun rsaSplitCodec(cipher: Cipher, opmode: Int, datas: ByteArray, keySize: Int): ByteArray {
        var maxBlock = 0
        if (opmode == Cipher.DECRYPT_MODE) {
            maxBlock = keySize / 8
//            maxBlock = keySize
        } else {
            maxBlock = keySize / 8 - 11
        }
        val out = ByteArrayOutputStream()
        var offSet = 0
        var buff: ByteArray
        var i = 0
        try {
            while (datas.size > maxBlock) {
                if (datas.size - offSet > maxBlock) {
                    buff = cipher.doFinal(datas, offSet, maxBlock)
                } else {
                    buff = cipher.doFinal(datas, offSet, datas.size - offSet)
                }
                out.write(buff, 0, buff.size)
                i++
                offSet = i * maxBlock
            }
        } catch (e: Exception) {
            throw RuntimeException("加解密阀值为[$maxBlock]的数据时发生异常", e)

        }

        val resultDatas = out.toByteArray()
        return resultDatas
    }


}

