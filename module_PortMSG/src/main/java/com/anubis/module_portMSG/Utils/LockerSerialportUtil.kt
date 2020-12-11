package com.anubis.module_portMSG.Utils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Looper
import android.util.Log
import android.widget.Toast
import android_serialport_api.SerialPort
import com.anubis.kt_extends.eLogE
import com.anubis.kt_extends.eShowTip
import com.anubis.module_portmsg.R
import java.io.File
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.security.InvalidParameterException


/**
 * Author  ： AnubisASN   on 18-7-16 上午8:40.
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

/**
 * @author fanming
 * 串口工具类
 */
class LockerSerialportUtil private constructor(private val path: String, private val baudrate: Int, lockerPortInterface: LockerPortInterface) {
    private var sportInterface: LockerPortInterface? = null
    protected var mOutputStreamBox: OutputStream? = null
    protected var mInputStreamBox: InputStream? = null
    private var mReadThreadBox: ReadThreadBox? = null
    private var m_SerialRecBox: SerialBroadcastReceiverBox? = null
    protected var boxPort: SerialPort? = null
    internal var firstRegisterBox = true
    private var m_Receiver2: SerialBroadcastReceiverBox? = null
    internal var mResult=true
    internal var boxFlag = true

    init {
        setSerialPort(lockerPortInterface)
    }

    fun getboxPort(): SerialPort? {
        return boxPort
    }

    inner class SerialBroadcastReceiverBox(c: Context) : BroadcastReceiver() {
        internal var ct: Context? = null

        init {
            Log.i(TAG, "enter  SerialBroadcastReceiverBox ")
            ct = c
            m_Receiver2 = this
        }

        //注册  锁屏广播
        @Throws(Exception::class)
        fun registerAction() {
            Log.i(TAG, "enter  registerAction ")
            val filter = IntentFilter()
            filter.addAction(Intent.ACTION_SCREEN_ON)
            filter.addAction(Intent.ACTION_SCREEN_OFF)
            if (ct == null) {
                Log.e(TAG, "ct nulll")
            }
            if (m_Receiver2 == null) {
                Log.e(TAG, "m_Receiver2 nulll")
            }
            ct!!.registerReceiver(m_Receiver2, filter)
        }

        @Throws(Exception::class)
        override fun onReceive(context: Context, intent: Intent) {
            Log.i(TAG, "enter  onReceive")
            val action = intent.action
            if (action == Intent.ACTION_SCREEN_ON) { // 屏幕开启后打开串口
                Log.i(TAG, "recevied  ACTION_SCREEN_ON ")
                if (boxPort == null) {
                    boxPort = SerialPort(File(path), baudrate, 0)
                }
            }

            if (action == Intent.ACTION_SCREEN_OFF) {  // 锁屏后关闭串口
                Log.i(TAG, "recevied  ACTION_SCREEN_OFF ")
                if (boxPort != null) {
                    closeSerialPort()
                    mReadThreadBox!!.interrupt()
                }
            }
        }
    }

    /**
     * 串口关闭
     */
    fun closeSerialPort() :Boolean{
           try {
            if (boxPort != null) {
                boxPort!!.close()
                boxPort = null
                val filter = IntentFilter()
                filter.addAction(Intent.ACTION_SCREEN_ON)
                filter.addAction(Intent.ACTION_SCREEN_OFF)
                mContext!!.unregisterReceiver(m_Receiver2)
            }
               return true
        } catch (e: Exception) {
               e.eLogE("closeSerialPort")
           return false
        }
    }

    /**
     * 串口初始化出错的提示
     * @param context
     * @param resourceId
     */
    private fun DisplayError(context: Context?, resourceId: Int) {
        object : Thread() {
            override fun run() {
                Looper.prepare()
                Toast.makeText(context, resourceId, Toast.LENGTH_SHORT).show()
                Looper.loop()
            }
        }.start()

    }

    /**
     * 读串口数据的子线程
     * @author fanming
     */

    private inner class ReadThreadBox : Thread() {
        override fun run() {
            super.run()
            while (boxFlag) {
                val size: Int
                try {
                    val buffer = ByteArray(512)
                    if (mInputStreamBox == null) return
                    /* read会一直等待数据，如果要判断是否接受完成，只有设置结束标识，或作其他特殊的处理 */
                    //Log.i("SerialPort", mReadThreadBox.getName() + "---locker port------读取中");
                    size = mInputStreamBox!!.read(buffer)
                    //                    sendMessage();
                    if (size > 0) {
                        val bytes = ByteArray(size)
                        for (i in 0 until size) {
                            bytes[i] = buffer[i]
                        }
                        sportInterface!!.onLockerDataReceived(bytes, size, path)
                    }
                } catch (e: IOException) {
                    e.eLogE("Thread:$e")
                    return
                }

                //Log.i("SerialPort","read end");
            }
            Log.i("SerialPort", "-----locker port--- 关闭")
        }
    }


    /**
     * 初始化串口
     * @param lockerPortInterface
     */
    private fun setSerialPort(lockerPortInterface: LockerPortInterface) {
        this.sportInterface = lockerPortInterface
        try {
            /* Check parameters */
            if (path.length == 0 || baudrate == -1) {
                println("path:$path")
                println("baudrate:$baudrate")
                throw InvalidParameterException()
            }
            /* Open the serial port */
            boxPort = SerialPort(File(path), baudrate, 0)

            mOutputStreamBox = boxPort!!.outputStream
            mInputStreamBox = boxPort!!.inputStream
            /* Create a serial rec buf  thread */
            mReadThreadBox = ReadThreadBox()
            //            SerialPortState = true;
            mReadThreadBox!!.start()
            if (firstRegisterBox) {
                if (mContext == null) {
                    Log.e(TAG, "mContext nulll")
                }
                m_SerialRecBox = SerialBroadcastReceiverBox(mContext!!)
                m_SerialRecBox!!.registerAction()
                firstRegisterBox = false
                Log.i(TAG, "----locker port--- 注册完毕")
            }
            lockerPortInterface.onLockerOutputStream(mOutputStreamBox!!)
            mResult=true
        } catch (e: SecurityException) {
            e.printStackTrace()
            //            BaseUtils.setTips(mContext,path);
            DisplayError(mContext, R.string.error_security)
            mContext!!.sendBroadcast(Intent("open_fail"))
            mResult = false
        } catch (e: IOException) {
            e.printStackTrace()
            DisplayError(mContext, R.string.error_unknown)
            mContext!!.sendBroadcast(Intent("open_fail"))
            mResult = false
        } catch (e: InvalidParameterException) {
            e.printStackTrace()
            DisplayError(mContext, R.string.error_configuration)
            mContext!!.sendBroadcast(Intent("open_fail"))
            mResult = false
        } catch (e: UnsatisfiedLinkError) {
            e.eLogE("LockerSerialportUtil错误 ")
            mContext?.eShowTip("CUP框架不支持")
            mResult = false
        }
    }

    companion object {

        var instance: LockerSerialportUtil? = null
            private set
        private val TAG = "SerialPort"
        private var mContext: Context? = null

        fun init(context: Context, path: String, baudrate: Int, lockerPortInterface: LockerPortInterface) {
            mContext = context
            instance = LockerSerialportUtil(path, baudrate, lockerPortInterface)
        }
    }


}

