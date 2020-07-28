package com.anubis.app_webrtc

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.anubis.app_webrtc.APP.Companion.mAPP
import com.anubis.app_webrtc.APP.Companion.onlyVoipActivity
import com.anubis.kt_extends.*
import com.anubis.module_webRTC.demo.MLOC
import com.anubis.module_webRTC.demo.MLOC.VOIP_SERVER_URL
import com.anubis.module_webRTC.demo.MLOC.init
import com.anubis.module_webRTC.demo.MLOC.saveImServerUrl
import com.anubis.module_webRTC.demo.MLOC.saveMaxTima
import com.anubis.module_webRTC.demo.MLOC.saveServerUrl
import com.anubis.module_webRTC.demo.MLOC.saveUserId
import com.anubis.module_webRTC.demo.SplashActivity
import com.anubis.module_webRTC.demo.service.FloatWindowsService
import com.anubis.module_webRTC.demo.service.KeepLiveService
import com.anubis.module_webRTC.demo.setting.SettingActivity
import com.anubis.module_webRTC.demo.voip.VoipActivity
import com.anubis.module_webRTC.eDataRTC
import com.starrtc.starrtcsdk.api.XHClient

class WebRtcActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_web_rtc)
    }

    override fun onResume() {
        super.onResume()
        initRRT()
        init()
    }

    companion object {
        val CALL = "CALL"  //拨打
        val HANG = "HANG"  //挂断
        val SETUI = "SETUI"   //高级设置
        val DEMO = "DEMO"  //Ddemo界面
        val SET = "SET" //参数设置
    }

    fun init() {
        eLog("初始化")
        eDataRTC.mAPP = mAPP
        val uri = intent.data ?: return eShowTip("RTC扩展组件,规则错误无法运行").apply { finish() }
        when (uri.getQueryParameter("type").eLog("type:")) {
            SET -> {
                val url = uri.getQueryParameter("url").eLog("url")
                if (url != null && url.isNotBlank() && url.isNotEmpty()) {
                    saveServerUrl(url)
                }
                val localId = uri.getQueryParameter("localId").eLog("localId")
                if (localId != null && localId.isNotBlank() && localId.isNotEmpty())
                    saveUserId(localId)

                val maxTimae = uri.getQueryParameter("maxTime").eLog("maxTime")
                if (maxTimae != null && maxTimae.isNotBlank() && maxTimae.isNotEmpty())
                    saveMaxTima(localId)
                val isAutoAnswer = uri.getBooleanQueryParameter("autoAnswer",false).eLog("autoAnswer")!!
                if (isAutoAnswer) {
                     eSetSystemSharedPreferences("isAutoAnswer",isAutoAnswer)
                }
                try {
                    initRRT()
                }catch (e:Exception){
                    eLogE("initRRT",e)
                }
                finish()
            }
            CALL -> {
                val targetId = uri.getQueryParameter("targetId").eLog("targetId")
                val outTime = (uri.getQueryParameter("maxTime").eLog("maxTime")
                        ?: 60L).toString().toLong()
                val cameraId = (uri.getQueryParameter("cameraId").eLog("cameraId")
                        ?: 0).toString().toInt()
                if (targetId != null || targetId!!.isNotBlank()) {
                    startVoip(targetId, outTime, cameraId)
                } else {
                    eShowTip("targetId为空")
                }
            }
            HANG -> {
                eLog("挂断")
                eLog("VoipActivity==null:${VoipActivity.mVoipActivity == null}")
                VoipActivity.mVoipActivity?.onClick(null)
                finish()
            }
            DEMO -> {
                val intent = Intent(this, SplashActivity::class.java)
                startActivity(intent)
                finish()
            }
            SETUI -> {
                val intent = Intent(this, SettingActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
    }

    fun initRRT() {
        if (!eApp.eInit.eIsServiceRunning(this, KeepLiveService::class.java.name)) {
            val intent = Intent(this, KeepLiveService::class.java)
            startService(intent)
        }
        val isOnline = XHClient.getInstance().isOnline.eLog("isOnline")

    }

    fun startVoip(targetId: String, outTime: Long, cameraId: Int = 0) {
        val intent = Intent(this, VoipActivity::class.java)
        intent.putExtra("targetId", targetId)
        intent.putExtra("outTime", outTime)
        intent.putExtra("cameraId", cameraId)
        intent.putExtra(VoipActivity.ACTION, VoipActivity.CALLING)
        this.finish()
        startActivity(intent)
    }

}
