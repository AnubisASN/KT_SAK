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
class ePortMSG private constructor() : LockerPortInterface {
    //波特率
    private var outputStream: OutputStream? = null     //发送串口的输出流
    private lateinit var mLockerSerialportUtil: LockerSerialportUtil

    companion object {
        private lateinit var mActivity: Activity
        private var mPATH: String = "/dev/ttyS3" //串口名称
        private var mBAUDRATE: Int = 9600
        private var mCallback: ICallBack? = null
        fun eInit(activity: Activity, PATH: String = mPATH, BAUDRATE: Int = mBAUDRATE, callback: ICallBack? = null): ePortMSG {
            mActivity = activity
            mPATH = PATH
            mBAUDRATE = BAUDRATE
            mCallback = callback
            return eInit
        }

        private val eInit by lazy(LazyThreadSafetyMode.SYNCHRONIZED) { ePortMSG() }
    }


    interface ICallBack {
        @Throws(Exception::class)
        fun IonLockerDataReceived(buffer: ByteArray, size: Int, path: String)
    }


    /**
     * 方法说明：串口数据发送
     * @调用方法：eSendMSG()
     * @param mAcitvity: Activity；活动
     * @param msg: Any ;String or ByteArray 信息
     * @param mPATH: String；串口名
     * @param BAUDRATE：Int；波动
     * @param callback：: ICallBack?=null;接收回调
     * @return: Boolean
     */
    @Throws(Exception::class)
    fun eOpenSendMSG(msg: Any, PATH: String = mPATH, BAUDRATE: Int = mBAUDRATE, callback: ICallBack? = mCallback): Boolean {
        mPATH = PATH
        mBAUDRATE = BAUDRATE
        mCallback = callback
        openPort()
        when (msg) {
            is String -> eSendMSG(msg)
            is ByteArray -> eSendMSG(msg)
            else -> eSendMSG(msg.toString())
        }
        return LockerSerialportUtil.instance?.mResult ?: false
    }

    /**
     * 方法说明：串口数据监听
     * @调用方法：eGetMSG()
     * @param mAcitvity: Activity；活动
     * @param  callback: ICallBack；消息回调
     * @param mPATH: String；串口名
     * @param BAUDRATE：Int；波动
     * @return: Boolean
     */
    @Throws(Exception::class)
    fun eGetMSG(PATH: String = mPATH, BAUDRATE: Int = mBAUDRATE, callback: ICallBack? = mCallback): Boolean {
        mPATH = PATH
        mBAUDRATE = BAUDRATE
        mCallback = callback
        openPort()
        return LockerSerialportUtil.instance?.mResult ?: false
    }

    /**
     * 关闭串口
     */
    fun eClosePort() =LockerSerialportUtil.instance?.closeSerialPort()

    /**
     * 打开串口
     */
    private fun openPort() {
        LockerSerialportUtil.init(mActivity, mPATH, mBAUDRATE, this)
    }

    /**
     * 发送指令
     * @param msg
     */
    fun eSendMSG(msg: String)= msg.let {
            try {
                outputStream!!.write(eString.eInit.eGetHexStringToBytes(msg))
                true
            } catch (e: Exception) {
                return@let false
            }

        }


    fun eSendMSG(msg: ByteArray)= msg.let {
        try {
            outputStream!!.write(msg)
            true
        } catch (e: Exception) {
            return@let  false
        }
    }



    /**
     * @param buffer 返回的字节数据
     * @param size   返回的字节长度
     * @param path   串口名，如果有多个串口需要识别是哪个串口返回的数据（传或不传可以根据自己的编码习惯）
     */
    override fun onLockerDataReceived(buffer: ByteArray, size: Int, path: String) {
        mCallback?.IonLockerDataReceived(buffer, size, path)
    }


    override fun onLockerOutputStream(outputStream: OutputStream) {
        this.outputStream = outputStream
    }

}





