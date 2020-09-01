package com.anubis.module_cardotg

import android.annotation.SuppressLint
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.text.SimpleDateFormat


import com.huashi.otg.sdk.HSIDCardInfo
import com.huashi.otg.sdk.HandlerMsg
import com.huashi.otg.sdk.HsOtgApi
import com.huashi.otg.sdk.HsSerialPortSDK
import com.huashi.otg.sdk.Test

import androidx.appcompat.app.AppCompatActivity
import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.Message
import android.os.SystemClock
import android.util.Log
import android.view.View
import android.view.View.OnClickListener
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.anubis.kt_extends.eAssets
import com.anubis.kt_extends.eLog
import com.anubis.kt_extends.eLogE
import com.anubis.kt_extends.eShowTip
import com.anubis.module_cardotg.R.id.iv_photo
import com.anubis.module_cardotg.R.id.tv_info
import com.tencent.bugly.Bugly.applicationContext
import kotlinx.coroutines.*

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
 *类说明：身份证阅读器
 *  @初始化连接方法：otgInit()
 * @param app: Application; APP
 * @param appHandler: Handler；消息回调
 * @return: null
 */
@SuppressLint("StaticFieldLeak")
open class eCardOTG internal constructor() {
    private var autoJob: Job? = null
    private var filepath = Environment.getExternalStorageDirectory().absolutePath + "/wltlib"

    companion object {
        private lateinit var api: HsOtgApi
        private lateinit var mContext: Context
        private lateinit var mHandler: Handler
        private lateinit var iResult: IResult
        fun eInit(context: Context, handler: Handler, iResult: IResult): eCardOTG? {
            try {
                mContext = context
                mHandler = handler
                this.iResult = iResult
                return eInit
            } catch (e: Exception) {
                eLogE("eCardOTG 初始化失败", e)
                return null
            }
        }
        private val eInit by lazy(LazyThreadSafetyMode.SYNCHRONIZED) { eCardOTG() }
    }
    init {
        eAssets.eInit.eAssetsToFile(mContext, "base.dat", "$filepath/base.dat")
        eAssets.eInit.eAssetsToFile(mContext, "license.lic", "$filepath/license.lic")
        eOTGConn().eLog("eOTGConn")
    }

    //连接
    private fun eOTGConn(): Boolean {
        api = HsOtgApi(mHandler, mContext)
        val ret = api.init()// 因为第一次需要点击授权，所以第一次点击时候的返回是-1所以我利用了广播接受到授权后用handler发送消息
        return if (ret == 1) {
            iResult.CONNECT_SUCCESS()
            true
        } else {
            iResult.CONNECT_ERROR()
            false
        }
    }

    //读卡
    open   fun eOTGRead(): Boolean {
        try {
            if (eOTGConn()) {
                api.Authenticate(200, 200)
                val ici = HSIDCardInfo()
                return if (api.ReadCard(ici, 200, 1300) == 1) {
                    iResult.READ_SUCCESS(ici, eGetFingerprint(ici), eGetPicture(ici) ?: "")
                    true
                } else {
                    iResult.READ_ERROR()
                    false
                }
            } else {
                return false
            }
        }catch (e:RuntimeException){
            eLogE("eOTGRead 硬件断开连接")
            return false
        } catch (e: Exception) {
            eLogE("eOTGRead",e)
            return false
        }
    }

    //自动读卡
    open   fun eOTGAutoRead(delayTime: Long = 3000L) {
        autoJob?.cancel()
        autoJob = GlobalScope.launch {
            while (isActive) {
                eOTGRead()
                delay(delayTime)
            }
        }
    }


    open   fun eOTGStop(): Boolean {
        return try {
            autoJob?.cancel()
            api.let { api.unInit() }
            true
        } catch (e: Exception) {
            false
        }
    }

    open  fun eGetPicture(ici: HSIDCardInfo): String? {
        try {
            val ret = api.Unpack(filepath, ici.getwltdata())
            return if (ret == 0) {// 读卡失败
                "$filepath/zp.bmp"
            } else null
        } catch (e: FileNotFoundException) {
            return "头像不存在!"
        } catch (e: IOException) {
            return "头像读取错误!"
        } catch (e: Exception) {
            return "头像解码失败!"
        }
    }

    /**
     * 指纹
     */
    open  fun eGetFingerprint(ici: HSIDCardInfo): String {
        var fp = ByteArray(1024)
        fp = ici.fpDate
        val m_FristPFInfo = if (fp[4] == 0x01.toByte()) {
            String.format("指纹  信息：第一枚指纹注册成功。指位：%s。指纹质量：%d \n", GetFPcode(fp[5].toInt()), fp[6])
        } else {
            "身份证无指纹 \n"
        }
        val m_SecondPFInfo = if (fp[512 + 4] == 0x01.toByte()) {
            String.format("指纹  信息：第二枚指纹注册成功。指位：%s。指纹质量：%d \n", GetFPcode(fp[512 + 5].toInt()),
                    fp[512 + 6])
        } else {
            "身份证无指纹 \n"
        }
        return m_FristPFInfo
    }

    private fun GetFPcode(FPcode: Int): String {
        when (FPcode) {
            11 -> return "右手拇指"
            12 -> return "右手食指"
            13 -> return "右手中指"
            14 -> return "右手环指"
            15 -> return "右手小指"
            16 -> return "左手拇指"
            17 -> return "左手食指"
            18 -> return "左手中指"
            19 -> return "左手环指"
            20 -> return "左手小指"
            97 -> return "右手不确定指位"
            98 -> return "左手不确定指位"
            99 -> return "其他不确定指位"
            else -> return "未知"
        }
    }

    interface IResult {
        //硬件连接成功
        fun CONNECT_SUCCESS(successMsg: String = "硬件连接成功", SAMID: String = api.GetSAMID())

        //硬件连接错误
        fun CONNECT_ERROR(errorMsg: String = "硬件连接失败", SAMID: String = api.GetSAMID())

        //身份证读取成功
        fun READ_SUCCESS(cardInfo: HSIDCardInfo, fingerprintStr: String, imgPath: String, SAMID: String = api.GetSAMID())

        //身份证读取失败
        fun READ_ERROR(msg: String = "证件读取失败", SAMID: String = api.GetSAMID())
    }

}
