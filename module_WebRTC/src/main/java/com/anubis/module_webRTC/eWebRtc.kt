package com.anubis.module_webRTC

import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import com.anubis.kt_extends.eApp
import com.anubis.kt_extends.eApp.Companion.eIApp
import com.anubis.kt_extends.eLog
import com.anubis.kt_extends.eSetSystemSharedPreferences
import com.anubis.kt_extends.eShowTip
import com.anubis.module_eventbus.post.ePostSpan
import com.anubis.module_webRTC.demo.MLOC
import com.anubis.module_webRTC.demo.MLOC.saveMaxTima
import com.anubis.module_webRTC.demo.MLOC.saveServerUrl
import com.anubis.module_webRTC.demo.MLOC.saveUserId
import com.anubis.module_webRTC.demo.SplashActivity
import com.anubis.module_webRTC.demo.service.KeepLiveService
import com.anubis.module_webRTC.demo.setting.SettingActivity
import com.anubis.module_webRTC.demo.voip.VoipActivity
import com.anubis.module_webRTC.eDataRTC.mAPP
import com.anubis.module_webRTC.utils.AEvent
import com.starrtc.starrtcsdk.api.XHClient

class eWebRtc private constructor() {
    val eIsOnline get() = XHClient.getInstance().isOnline
    companion object {
        val eIWebRtc by lazy(LazyThreadSafetyMode.SYNCHRONIZED) { eWebRtc() }
    }

    init {
        initRTC()
    }

    fun eSetRTC(url: String, localId: String, maxTimae: String, isAutoAnswer: Boolean) {
        if (url != null && url.isNotBlank() && url.isNotEmpty()) {
            saveServerUrl(url)
        }
        if (localId != null && localId.isNotBlank() && localId.isNotEmpty())
            saveUserId(localId)

        if (maxTimae != null && maxTimae.isNotBlank() && maxTimae.isNotEmpty())
            saveMaxTima(localId)
        if (isAutoAnswer) {
            mAPP?.eSetSystemSharedPreferences("isAutoAnswer", isAutoAnswer)
        }
    }

    fun eCallRTC(targetId: String, maxTime: Long = 60, cameraId: Int = 0) {
        if (targetId != null || targetId.isNotBlank()) {
            val intent = Intent(mAPP, VoipActivity::class.java)
            intent.putExtra("targetId", targetId)
            intent.putExtra("outTime", maxTime)
            intent.putExtra("cameraId", cameraId)
            intent.putExtra(VoipActivity.ACTION, VoipActivity.CALLING)
            intent.flags = FLAG_ACTIVITY_NEW_TASK
            mAPP?.startActivity(intent)
        } else {
            mAPP?.eShowTip("targetId为空")
        }
    }

    fun eHangRTC() {
        VoipActivity.mVoipActivity?.onClick(null)
    }

    fun eDemoRTC() {
        val intent = Intent(mAPP, SplashActivity::class.java)
        intent.flags = FLAG_ACTIVITY_NEW_TASK
        mAPP?.startActivity(intent)
    }

    fun eSetUIRTC() {
        val intent = Intent(mAPP, SettingActivity::class.java)
        intent.flags = FLAG_ACTIVITY_NEW_TASK
        mAPP?.startActivity(intent)
    }

    fun initRTC() {
        eLog("初始化initRTC")
        if (!eIApp.eIsServiceRunning(mAPP!!, KeepLiveService::class.java.name)) {
            val intent = Intent(mAPP, KeepLiveService::class.java)
            mAPP?.startService(intent)
        }
    }

    fun cleanRTC() {
        eLog("cleanRTC")
        XHClient.getInstance().loginManager.logout()
        AEvent.notifyListener(AEvent.AEVENT_LOGOUT, true, null)
        MLOC.hasLogout = true
    }


}
