package com.anubis.module_webRTC.listener

import android.content.Intent
import com.anubis.kt_extends.*
import com.anubis.module_webRTC.demo.MLOC
import com.anubis.module_webRTC.database.CoreDB
import com.anubis.module_webRTC.database.HistoryBean
import com.anubis.module_webRTC.demo.service.KeepLiveService
import com.anubis.module_webRTC.utils.AEvent
import com.starrtc.starrtcsdk.apiInterface.IXHVoipManagerListener
import com.starrtc.starrtcsdk.socket.StarErrorCode

import java.text.SimpleDateFormat

class XHVoipManagerListener : IXHVoipManagerListener {
    override fun onCalling(fromID: String) {
        this.eLog<Any>("XHVoipManagerListener-onCalling", "TAG")
        val historyBean = HistoryBean()
        historyBean.type = CoreDB.HISTORY_TYPE_VOIP
        historyBean.lastTime = SimpleDateFormat("MM-dd HH:mm").format(java.util.Date())
        historyBean.conversationId = fromID
        historyBean.newMsgCount = 1
        MLOC.addHistory(historyBean, false)
        AEvent.notifyListener(AEvent.AEVENT_VOIP_REV_CALLING, true, fromID)
    }

    override fun onAudioCalling(fromID: String) {
        this.eLog<Any>("XHVoipManagerListener-onAudioCalling", "TAG")
        val historyBean = HistoryBean()
        historyBean.type = CoreDB.HISTORY_TYPE_VOIP
        historyBean.lastTime = SimpleDateFormat("MM-dd HH:mm").format(java.util.Date())
        historyBean.conversationId = fromID
        historyBean.newMsgCount = 1
        MLOC.addHistory(historyBean, false)
        AEvent.notifyListener(AEvent.AEVENT_VOIP_REV_CALLING_AUDIO, true, fromID)
    }

    override fun onCancled(fromID: String) {
        this.eLog<Any>("XHVoipManagerListener-onCancled", "TAG")
    }

    override fun onRefused(fromID: String) {
        this.eLog<Any>("XHVoipManagerListener-onRefused", "TAG")
        AEvent.notifyListener(AEvent.AEVENT_VOIP_REV_REFUSED, true, fromID)
    }

    override fun onBusy(fromID: String) {
        this.eLog<Any>("XHVoipManagerListener-onBusy", "TAG")
        AEvent.notifyListener(AEvent.AEVENT_VOIP_REV_BUSY, true, fromID)
    }

    override fun onMiss(fromID: String) {
        this.eLog<Any>("XHVoipManagerListener-onMiss", "TAG")
        val historyBean = HistoryBean()
        historyBean.type = CoreDB.HISTORY_TYPE_VOIP
        historyBean.lastTime = SimpleDateFormat("MM-dd HH:mm").format(java.util.Date())
        historyBean.conversationId = fromID
        historyBean.newMsgCount = 1
        MLOC.addHistory(historyBean, false)

        AEvent.notifyListener(AEvent.AEVENT_VOIP_REV_MISS, true, fromID)
    }

    override fun onConnected(fromID: String) {
        this.eLog<Any>("XHVoipManagerListener-onConnected", "TAG")
        AEvent.notifyListener(AEvent.AEVENT_VOIP_REV_CONNECT, true, fromID)
    }

    override fun onHangup(fromID: String) {
        this.eLog<Any>("XHVoipManagerListener-onHangup:$fromID")
        AEvent.notifyListener(AEvent.AEVENT_VOIP_REV_HANGUP, true, fromID)
    }

    override fun onError(errorCode: String) {
        this.eLog<Any>("XHVoipManagerListener-onError:$errorCode")
        AEvent.notifyListener(AEvent.AEVENT_VOIP_REV_ERROR, true, StarErrorCode.getErrorCode(errorCode))
    }

    override fun onReceiveRealtimeData(data: ByteArray) {
        this.eLog<Any>("XHVoipManagerListener-onReceiveRealtimeData:${String(data)}", "TAG")
    }

    override fun onTransStateChanged(state: Int) {
        this.eLog<Any>("XHVoipManagerListener-onTransStateChanged:$state", "TAG")
        AEvent.notifyListener(AEvent.AEVENT_VOIP_TRANS_STATE_CHANGED, true, state)
    }
}
