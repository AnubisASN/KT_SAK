package com.anubis.module_portMSG

import android.annotation.SuppressLint
import android.app.Activity
import android.media.MediaPlayer
import android.util.Log
import com.anubis.kt_extends.eString
import com.anubis.module_portMSG.Utils.LockerPortInterface
import com.anubis.module_portMSG.Utils.LockerSerialportUtil
import java.io.OutputStream

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

//    private companion object {
//        var init: ePortMSG? = null
//    }
//
//    init {
//        init = this@ePortMSG
//    }

    @Throws(Exception::class)
    fun MSG(activity: Activity,msg: String = "DAAD014400DBBD", mPATH: String = "/dev/ttyUSB0", BAUDRATE: Int = 9600): Boolean {
        PATH = mPATH
        this.BAUDRATE = BAUDRATE
        openPort(activity)
        sendParams(msg)
        closePort()
//            } catch (e: Exception) {
//                eLogE("MyException：$e")
//            }
//        }).start()
        return Result
    }

//            bt_daad.id -> {
//                val openToken = "DAAD014400DBBD"
//                Thread(Runnable {
//                    openPort2()
//                    sendParams2(openToken)
//                    closePort()
//                }).start()

//            bt_0105.id -> {
//                val openToken = "01050000FF008C3A"
//                Thread(Runnable {
//                    openPort2()
//                    sendParams2(openToken)
//                    closePort()
//                }).start()
//            }

    /**
     * 关闭串口
     */
    private fun closePort() {

        LockerSerialportUtil.instance!!.closeSerialPort()
    }

    /**
     * 打开串口
     */
    private fun openPort(activity:Activity) {
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

        if (msg.startsWith("daad") || msg.startsWith("DAAD") || msg.startsWith("0105000") && msg !== "010500000000CDCA") {
//            playVoice(R.raw.open_success, false)
        }

    }



    /**
     * @param buffer 返回的字节数据
     * @param size   返回的字节长度
     * @param path   串口名，如果有多个串口需要识别是哪个串口返回的数据（传或不传可以根据自己的编码习惯）
     */
    override fun onLockerDataReceived(buffer: ByteArray, size: Int, path: String) {
        val result = String(buffer, 0, size)
        Log.e("收到", "onLockerDataReceived====$result")
    }

    override fun onLockerOutputStream(outputStream: OutputStream) {
        this.outputStream = outputStream
    }


}

