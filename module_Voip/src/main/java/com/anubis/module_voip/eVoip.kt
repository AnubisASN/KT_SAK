package com.anubis.module_voip

import android.content.Context
import android.media.AudioManager
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.os.Build
import android.os.Vibrator
import android.text.TextUtils
import androidx.annotation.RequiresApi
import com.anubis.kt_extends.eClean
import com.anubis.kt_extends.eLog
import com.anubis.kt_extends.eMediaPlayer
import com.anubis.kt_extends.ePlayVoice
import com.yzx.api.CallType
import com.yzx.api.UCSCall
import com.yzx.api.UCSCameraType
import com.yzx.api.UCSService
import com.yzx.listenerInterface.CallStateListener
import com.yzxtcp.UCSManager
import com.yzxtcp.data.UcsErrorCode
import com.yzxtcp.data.UcsReason
import org.jetbrains.anko.runOnUiThread
import java.util.*

@RequiresApi(Build.VERSION_CODES.N)
class eVoip internal constructor() {
    private var mMediaPlayer: MediaPlayer? = null
    private var mTimer: Timer? = null
    private var times: Long = 0
    private var vibrator: Vibrator? = null
    private val mToken="eyJBbGciOiJIUzI1NiIsIkFjY2lkIjoiMGExNTEzZDU0MWNmNGM4NjlkOTM0Yzk3MDNlMGQwOWIiLCJBcHBpZCI6IjVkNzZiZmE4YTEzMDQ2ZTliYmEwMTlhNTc3YjM1MTgzIiwiVXNlcmlkIjoidGVzdDEifQ==.mH7a9TMJMPm12g1CFyyMVpRJ9lM4gwSclvvRLPjSuus="

    companion object {
        private lateinit var mContext: Context
        private var boolean:Boolean=true
        private  val eInit by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
                  UCSService.init(mContext, boolean)
              eVoip()
          }
        fun eInit(context: Context,b:Boolean=true):eVoip{
            mContext=context
            boolean=b
        return    eInit
        }
    }

    /*默认回调*/
     var eCallListener :CallStateListener= object : CallStateListener {
        //呼叫失败
        override fun onDialFailed(p0: String?, p1: UcsReason?) {
            "呼叫失败:${p1?.reason}-$p0".eLog("onDialFailed")
        }

        // 来电时
        override fun onIncomingCall(
            p0: String?,
            p1: String?,
            p2: String?,
            p3: String?,
            p4: String?
        ) {
            "来电时".eLog("onIncomingCall")
        }

        //呼叫被释放
        override fun onHangUp(p0: String?, p1: UcsReason?) {
            "呼叫被释放".eLog("onHangUp")
        }


        //正在拨号
        override fun onConnecting(p0: String?) {
            "关于连接".eLog("onConnecting")
        }


        //对方振铃中回调

        override fun onAlerting(p0: String?) {
            ePlayVoice(mContext, RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE))
            "对方振铃中回调".eLog("onAlerting")
        }


        //接通时回调
        override fun onAnswer(p0: String?, p1: String?) {
            eMediaPlayer?.eClean()
            "已接通".eLog("onAnswer")
        }


        //通话中网络状态上报
        override fun onNetWorkState(p0: Int, p1: String?) {
            "通话中网络状态上报".eLog("onNetWorkState")
        }


        //dtmf 回调
        override fun onDTMF(p0: Int) {
            "onDTMF".eLog("onDTMF-：$p0")
        }


        //视频会话截屏回调
        override fun onCameraCapture(p0: String?) {
            "视频会话截屏回调".eLog("onCameraCapture")
        }


        //单通发生时回调
        override fun singlePass(p0: Int) {
            "来电时".eLog("onIncomingCall")
        }


        //对端视频模式回调
        override fun onRemoteCameraMode(p0: UCSCameraType?) {
            "对端视频模式回调".eLog("onRemoteCameraMode")
        }

        //关于加密
        override fun onEncryptStream(p0: ByteArray?, p1: ByteArray?, p2: Int, p3: IntArray?) {
            "关于加密".eLog("onEncryptStream")
        }


        //关于解密
        override fun onDecryptStream(p0: ByteArray?, p1: ByteArray?, p2: Int, p3: IntArray?) {
            "关于解密".eLog("onDecryptStream")
        }


        //初始化播放
        override fun initPlayout(p0: Int, p1: Int, p2: Int) {
            "初始化播放".eLog("initPlayout")
        }


        //有新的来电
        override fun initRecording(p0: Int, p1: Int, p2: Int) {
            "有新的来电".eLog("initRecording")
        }


        //写入播放数据
        override fun writePlayoutData(p0: ByteArray?, p1: Int): Int {
            "写入播放数据".eLog("writePlayoutData")
            return 0
        }

        //读取录音数据
        override fun readRecordingData(p0: ByteArray?, p1: Int): Int {
            "读取录音数据".eLog("readRecordingData")
            return 0
        }

        //预览
        override fun onTransPreviewImg(p0: String?, p1: ByteArray?, p2: Int) {
            "预览".eLog("onTransPreviewImg")
        }


    }

    /*服务连接*/
    fun eConnect(token: String? = mToken, block:( (Boolean) -> Unit)?=null) {
        UCSManager.connect(token?:mToken) { p0 ->
            with(p0?.reason == UcsErrorCode.NET_ERROR_CONNECTOK) {
                block?.let { it(this) } ?: eLog(if (this) "登录成功" else "登录失败:${p0?.reason}")
            }
        }
    }

    /*拨打电话*/
    fun eCall(iphone: String) {
        UCSCall.dial(CallType.DIRECT, iphone, "")
    }

    /*添加回调*/
    fun eCallListener(call: CallStateListener=eCallListener ) {
        UCSCall.removeCallStateListener(eCallListener)
        eCallListener=call
        UCSCall.addCallStateListener(eCallListener)
    }

    /*开启扬声器*/
    fun eSetSpeaker(isOpen: Boolean = false) {
        var model = Build.MODEL
        if (!TextUtils.isEmpty(model)) {
            model = model.toUpperCase().replace(" ", "")
        }
        val audioManager = mContext.getSystemService(Context.AUDIO_SERVICE) as AudioManager //MI2
        if ("MINOTEPRO" == model) {
            audioManager.mode = AudioManager.MODE_NORMAL
        } else if ("NX513J" == model || "ZTEB2015" == model || "HTCM8SW" == model || Build.VERSION.SDK_INT > 20) {
            audioManager.mode = AudioManager.MODE_IN_COMMUNICATION
        } else {
            audioManager.mode = AudioManager.MODE_IN_CALL
        }
        audioManager.isSpeakerphoneOn = isOpen
    }

    /*开始记时*/
    fun eStartTime(block: (String) -> Unit) {
        eStopTime()
        times = 0
        mTimer = Timer()
        mTimer!!.schedule(object : TimerTask() {
            override fun run() {
                var timeText = ""
                timeText = if (times < 10) {
                    "00:0$times"
                } else {
                    val tmp = times / 60
                    if (tmp == 0L) {
                        "00:$times"
                    } else {
                        val second = times - 60 * tmp
                        val secondStr = if (second > 9) "" + second else "0$second"
                        if (tmp < 10) {
                            "0$tmp:$secondStr"
                        } else {
                            "$tmp:$secondStr"
                        }
                    }
                }
                mContext.runOnUiThread { block(timeText) }
                times++
            }
        }, 0, 1000)
    }

    /*结束计时*/
    fun eStopTime() {
        if (mTimer != null) {
            mTimer!!.cancel()
            mTimer = null
        }
    }

    /*挂断*/
    fun eHangUp(callId:String=""){
        eLog("自己挂断")
        eStopTime()
        UCSCall.hangUp(callId)
    }
/*释放*/
    fun eDestroy(){
    eLog("释放")
        eMediaPlayer?.eClean()
         UCSCall.removeCallStateListener(eCallListener)
    }
}
