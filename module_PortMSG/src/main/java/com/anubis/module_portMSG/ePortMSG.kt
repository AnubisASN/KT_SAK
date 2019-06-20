package com.anubis.module_portMSG

import android.annotation.SuppressLint
import android.app.Activity
import android.media.MediaPlayer
import android.os.Handler
import android.os.Message
import android.service.autofill.Validators.or
import android.util.Log
import com.anubis.kt_extends.eLog
import com.anubis.kt_extends.eString
import com.anubis.module_portMSG.Utils.LockerPortInterface
import com.anubis.module_portMSG.Utils.LockerSerialportUtil
import java.io.OutputStream
import kotlin.experimental.and
import kotlin.experimental.or


@SuppressLint("StaticFieldLeak")
/**
 * Author  ： AnubisASN   on 18-7-16 上午8:37.
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
 *类说明：串口通讯封装开发库
 *  @初始化方法：getInit()
 * @param mAcitvity: Activity；活动
 * @param mPATH: String；串口名
 * @param BAUDRATE：Int；波动
 * @return: ePortMSG
 * @通讯方法：MSG()
 * @param msg: String；消息
 * @return: Boolean
 */
object ePortMSG : LockerPortInterface {
    private var PATH: String? = null //串口名称         RS485开门方式
    private var BAUDRATE: Int? = null            //波特率
    private var outputStream: OutputStream? = null     //发送串口的输出流
    //    private var activity: Activity? = null
    internal var Result: Boolean = true
    private var mHandle: Handler? = null
//    private companion object {
//        var init: ePortMSG? = null
//    }
//
//    init {
//        init = this@ePortMSG
//    }

    @Throws(Exception::class)
    fun sendMSG(activity: Activity, msg: Any = "A", mPATH: String = "/dev/ttyS3", BAUDRATE: Int = 9600): Boolean {
        PATH = mPATH
        this.BAUDRATE = BAUDRATE
        openPort(activity)
        when (msg) {
            is String -> sendParams(msg)
            is ByteArray -> sendParams(msg)
            else -> {
                sendParams(msg.toString())
            }
        }
//            } catch (e: Exception) {
//                eLogE("MyException：$e")
//            }
//        }).start()
        return Result
    }

    @Throws(Exception::class)
    fun getMSG(activity: Activity, mHandle: Handler, mPATH: String = "/dev/ttyS3", BAUDRATE: Int = 9600): Boolean {
        PATH = mPATH
        this.BAUDRATE = BAUDRATE
        this.mHandle = mHandle
        openPort(activity)
//            } catch (e: Exception) {
//                eLogE("MyException：$e")
//            }
//        }).start()
        return Result
    }

    /**
     * 关闭串口
     */
    fun closeMSG() {
        LockerSerialportUtil.instance!!.closeSerialPort()
    }

    /**
     * 打开串口
     */
    fun openPort(activity: Activity) {
        println("打开串口")
        LockerSerialportUtil.init(activity, PATH!!, BAUDRATE!!, this)
        //        LockerSerialportUtil.init(this,PATH1,BAUDRATE,this);
    }

    /**
     * 发送指令"DAAD064400DBBD"
     * @param msg
     */
    private fun sendParams(msg: String) {

        if (outputStream == null) {
            return
        }
        outputStream!!.write(eString.eGetHexStringToBytes(msg))
    }

    private fun sendParams(msg: ByteArray) {

        if (outputStream == null) {
            return
        }
        outputStream!!.write(msg)

    }


    /**
     * @param buffer 返回的字节数据
     * @param size   返回的字节长度
     * @param path   串口名，如果有多个串口需要识别是哪个串口返回的数据（传或不传可以根据自己的编码习惯）
     */
    private var data = ""

    override fun onLockerDataReceived(buffer: ByteArray, size: Int, path: String) {
        val temporaryData = String(buffer, 0, size)
        eLog("串口监听：$temporaryData---${temporaryData.isNotBlank()}")
        if (temporaryData.isNotBlank()) {
            data += temporaryData
        } else {
            if (data.isNotBlank()|| data.isNotEmpty()) {
                val message = Message()
                eLog("发送：$path---$data")
                message.obj = "$path---$data"
                mHandle?.sendMessage(message)
                data=""
            }

        }
    }

    fun convertTwoUnSignInt(byteArray: ByteArray): Int =
            (byteArray[3].toInt() shl 24) or (byteArray[2].toInt() and 0xFF) or (byteArray[1].toInt() shl 8) or (byteArray[0].toInt() and 0xFF)

    override fun onLockerOutputStream(outputStream: OutputStream) {
        this.outputStream = outputStream
    }

    fun bytesToInt(src: ByteArray, offset: Int): Int {
        val value: Int
        value = (src[offset].toInt() and 0x02
                or (src[offset + 1].toInt() and 0x09 shl 8)
                or (src[offset + 2].toInt() and 0x12 shl 16)
                or (src[offset + 3].toInt() and 0x02 shl 24)
                or (src[offset + 4].toInt() and 0x11 shl 32)
                or (src[offset + 5].toInt() and 0x03 shl 40)
                or (src[offset + 6].toInt() and 0x10 shl 48)
                or (src[offset + 7].toInt() and 0x20 shl 56)
                or (src[offset + 8].toInt() and 0x30 shl 53)
                or (src[offset + 9].toInt() and 0xff shl 61)
                or (src[offset + 10].toInt() and 0x03 shl 64))
        return value
    }
}





