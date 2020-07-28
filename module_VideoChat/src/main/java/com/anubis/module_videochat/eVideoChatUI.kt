package com.anubis.module_videochat

import android.Manifest
import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.view.View
import android.widget.ImageView
import com.anubis.kt_extends.eLog
import com.anubis.kt_extends.ePermissions
import io.agora.rtc.IRtcEngineEventHandler
import kotlinx.android.synthetic.main.activity_videochat.*

class eVideoChatUI : AppCompatActivity() {
    private val REQUESTED_PERMISSIONS = arrayOf(Manifest.permission.RECORD_AUDIO, Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
    private val mHandler: Handler = @SuppressLint("HandlerLeak")
    object : Handler() {
        override fun handleMessage(msg: Message?) {
            eLog("${msg?.what} -- ${msg?.obj.toString()}")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_videochat)
        if (ePermissions.eInit.eSetPermissions(this, REQUESTED_PERMISSIONS)) {
            eVideoChat.init(this, local_video_view_container, remote_video_view_container,handler = mHandler,appID = intent.getStringExtra("appID"),channelName =intent.getStringExtra("channelName") )
        }
    }

    fun onClick(v: View) {
        when (v.id) {
            onVideo.id -> eVideoChat.onVideoPause(v as ImageView)
            onAudio.id -> eVideoChat.onAudioPause(v as ImageView)
            onWitchCamera.id -> eVideoChat.onSwitchCamera()
            onClose.id -> eVideoChat.onVideoClose()
        }
    }


    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        ePermissions.eInit.eSetOnRequestPermissionsResult(this, requestCode, permissions, grantResults)
        if (requestCode != 1) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }
}
