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
import kotlinx.coroutines.*
import java.io.OutputStream
import java.util.*
import javax.security.auth.callback.Callback
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
 */
object ePortMSG : LockerPortInterface {
    private var PATH: String? = null //串口名称         RS485开门方式
    private var BAUDRATE: Int? = null            //波特率
    private var outputStream: OutputStream? = null     //发送串口的输出流
    internal var Result: Boolean = true
    private var mCallback: ICallBack? = null
    /**
     * 方法说明：串口数据发送
     * @调用方法：sendMSG()
     * @param mAcitvity: Activity；活动
     * @param msg: Any ;String or ByteArray 信息
     * @param mPATH: String；串口名
     * @param BAUDRATE：Int；波动
     * @param callback：: ICallBack?=null;接收回调
     * @return: Boolean
     */

    interface ICallBack {
        @Throws(Exception::class)
        fun IonLockerDataReceived(buffer: ByteArray, size: Int, path: String)
    }

    @Throws(Exception::class)
    fun sendMSG(activity: Activity,  msg: Any = "A", mPATH: String = "/dev/ttyS3", BAUDRATE: Int = 9600,callback: ICallBack?=null): Boolean {
        PATH = mPATH
        this.BAUDRATE = BAUDRATE
        this.mCallback = callback
        openPort(activity)
        when (msg) {
            is String -> sendParams(msg)
            is ByteArray -> sendParams(msg)
            else -> {
                sendParams(msg.toString())
            }
        }
        return Result
    }

    /**
     * 方法说明：串口数据监听
     * @调用方法：getMSG()
     * @param mAcitvity: Activity；活动
     * @param  callback: ICallBack；消息回调
     * @param mPATH: String；串口名
     * @param BAUDRATE：Int；波动
     * @return: Boolean
     */
    @Throws(Exception::class)
    fun getMSG(activity: Activity,callback: ICallBack, mPATH: String = "/dev/ttyS3", BAUDRATE: Int = 9600): Boolean {
        PATH = mPATH
        this.BAUDRATE = BAUDRATE
        this.mCallback = callback
        openPort(activity)
        return Result
    }

    /**
     * 关闭串口
     */
    fun closeMSG() {
        LockerSerialportUtil.instance?.closeSerialPort()
    }

    /**
     * 打开串口
     */
  private  fun openPort(activity: Activity) {
        println("打开串口")
        LockerSerialportUtil.init(activity, PATH!!, BAUDRATE!!, this)
        //        LockerSerialportUtil.init(this,PATH1,BAUDRATE,this);
    }

    /**
     * 发送指令
     * @param msg
     */
    fun sendParams(msg: String) {

        if (outputStream == null) {
            return
        }
        outputStream!!.write(eString.eInit.eGetHexStringToBytes(msg))
    }

    fun sendParams(msg: ByteArray) {

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
    override fun onLockerDataReceived(buffer: ByteArray, size: Int, path: String) {
        mCallback?.IonLockerDataReceived(buffer,size,path)
    }



    override fun onLockerOutputStream(outputStream: OutputStream) {
        this.outputStream = outputStream
    }

}





