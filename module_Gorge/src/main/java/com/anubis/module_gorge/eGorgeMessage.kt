package com.anubis.module_gorge

import android.app.Activity
import android.media.MediaPlayer
import android.os.Handler
import android.util.Log
import com.anubis.kt_extends.eGetHexStringToBytes
import com.anubis.kt_extends.eLogE
import com.anubis.module_gorge.Utils.LockerPortInterface
import com.anubis.module_gorge.Utils.LockerSerialportUtil
import java.io.OutputStream
import java.util.concurrent.TimeoutException

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
 *类说明：
 */
class eGorgeMessage : LockerPortInterface {
    private var PATH2: String? = null //串口名称         RS485开门方式
    private var BAUDRATE: Int? = null            //波特率
    private var outputStream: OutputStream? = null     //发送串口的输出流
    private var mp: MediaPlayer? = null
    private var activity: Activity? = null
    var Result: Boolean = false

    private companion object {
        var init: eGorgeMessage? = null
    }

    init {
        init = this@eGorgeMessage
    }

    @Throws(TimeoutException::class)
    fun getInit(mAcitvity: Activity, mPATH2: String = "/dev/ttyUSB0", BAUDRATE: Int = 9600): eGorgeMessage {
        activity = mAcitvity
        PATH2 = mPATH2
        this.BAUDRATE = BAUDRATE
        return init!!
    }
    @Throws(Exception::class)
    fun MSG(msg: String = "DAAD014400DBBD"):Boolean {
//        Thread(Runnable {
//            try {
                openPort2()
                sendParams2(msg)
                closePort()
//            } catch (e: Exception) {
//                eLogE("MyException：$e")
//            }
//        }).start()
        return  Result
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
    private fun openPort2() {
        println("打开串口")
        LockerSerialportUtil.init(activity!!, PATH2!!, BAUDRATE!!, this)
        //        LockerSerialportUtil.init(this,PATH1,BAUDRATE,this);
    }

    /**
     * 发送指令"DAAD064400DBBD"
     * @param msg
     */
    private fun sendParams2(msg: String) {

        if (outputStream == null) {
            return
        }
        outputStream!!.write(eGetHexStringToBytes(msg))

        if (msg.startsWith("daad") || msg.startsWith("DAAD") || msg.startsWith("0105000") && msg !== "010500000000CDCA") {
//            playVoice(R.raw.open_success, false)
            Result=true
        }

    }


    /**
     * 播放音乐
     * @param music 资源文件
     */
    private fun playVoice(music: Int, isLoop: Boolean) {
        try {
            if (mp != null)
                mp!!.reset()
            mp = MediaPlayer.create(activity, music)//重新设置要播放的音频
            mp!!.isLooping = isLoop
            mp!!.start()//开始播放
        } catch (e: Exception) {
            e.printStackTrace()//输出异常信息
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

