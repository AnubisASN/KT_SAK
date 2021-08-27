package com.anubis.module_voip

import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.anubis.kt_extends.*
import com.anubis.kt_extends.eShell.Companion.eIShell
import com.anubis.module_eventbus.eEventBus
import com.anubis.module_eventbus.observe.eObserveEvent
import com.anubis.module_eventbus.post.ePostSpan
import com.anubis.module_eventbus.post.eRemoveStickyEvent
import com.anubis.module_voip.testAPP.Companion.mAPP
import com.anubis.module_voip.testAPP.Companion.mVoip
import com.yzx.api.UCSCall
import com.yzx.api.UCSCameraType
import com.yzx.listenerInterface.CallStateListener
import com.yzxtcp.data.UcsReason
import kotlinx.android.synthetic.main.activity_phone.*
import org.jetbrains.anko.onClick
import java.lang.System.exit
import kotlin.system.exitProcess

@RequiresApi(Build.VERSION_CODES.N)
open class PhoneActivity : AppCompatActivity() {
    private val maxTime = "00:30"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_phone)
        val token=intent.getStringExtra("TOKEN" ).eLog("TOKEN")
            mVoip.eConnect(token?.ifBlank { "null" }.eLog("token")){
                if (it){
                    eLog("登录成功:$token")
                    eObserveEvent<String>(isSticky = true) {
                        mVoip?.eCall(it)
                    }
                }else{
                    tvHint.post { tvHint.text= "登录失败-Token错误"}
                    tvHint.postDelayed( { ibtHang.callOnClick() },1500)
                }
            }
        ibtHang.onClick {
            mVoip?.eHangUp()
            finish()
        }
        mVoip?.eCallListener(object : CallStateListener {
            //呼叫失败
            override fun onDialFailed(p0: String?, p1: UcsReason?) {
                tvHint.post { tvHint.text= "呼叫失败:${p1?.reason}".eLog("onDialFailed0")}
                tvHint.postDelayed( { ibtHang.callOnClick() },1500)
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

            //挂断电话
            override fun onHangUp(p0: String?, p1: UcsReason?) {
                tvHint.post { tvHint.text= "挂断电话".eLog("onHangUp")}
               tvHint.postDelayed({ ibtHang.callOnClick()},1500)
            }

            //正在拨号
            override fun onConnecting(p0: String?) {
                tvHint.post { tvHint.text= "正在拨号...".eLog("onConnecting：$p0")}
            }

            //对方振铃中回调
            override fun onAlerting(p0: String?) {
                tvHint.post { tvHint.text=  "对方响铃中...".eLog("onAlerting")}
                ePlayVoice(this@PhoneActivity, RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE))

            }

            //接通时回调
            override fun onAnswer(p0: String?, p1: String?) {
                tvHint.post { tvHint.text= "已接通".eLog("onIncomingCall")}
              eMediaPlayer?.eClean()
                mVoip?.eStartTime {
                    if (it == maxTime) {
                        ibtHang.callOnClick()
                    } else
                        tvHint.text = it
                }
            }

            //通话中网络状态上报
            override fun onNetWorkState(p0: Int, p1: String?) {
                "通话中网络状态上报".eLog("onNetWorkState")
            }

            //dtmf 回调
            override fun onDTMF(p0: Int) {
                "onDTMF".eLog("onDTMF-：$p0")
                if (p0 == 11) {
                    val tIntent = Intent()
                    tIntent.putExtra("msg", "电话开门成功")
                    ePostSpan("IPHONE_OPEN", tIntent)
                }
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

        })

    }
    private var status = false
    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return true
        }
        if (event.action == KeyEvent.ACTION_DOWN) {
            if (event.keyCode == KeyEvent.KEYCODE_SHIFT_LEFT) {
                status = true
                return true
            }
            if (event.keyCode == KeyEvent.KEYCODE_0) {
                ibtHang.callOnClick()
            }
            if (status) {
                when (event.keyCode) {
                    KeyEvent.KEYCODE_8 -> ibtHang.callOnClick()
                }
                return true
            }
        }
        return super.onKeyDown(keyCode, event)
    }

    override fun onDestroy() {
      mVoip.eDestroy()
        eRemoveStickyEvent("")
        ePostSpan("IPHONE_CLOSE")
        eIShell.eAppClose(application)
        super.onDestroy()
    }
}
