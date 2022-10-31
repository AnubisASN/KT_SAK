package com.anubis.module_webRTC.demo.voip

import android.annotation.SuppressLint

import com.starrtc.starrtcsdk.core.pusher.XHCustomRecorder

import java.util.Timer
import java.util.TimerTask

class PushUVCTest(private val recorder: XHCustomRecorder) {
    private val fps = 20
    private val frameData: ByteArray? = null

    private var uploadTimer: Timer? = null

    fun startRecoder() {
        //启动XHCustomRecorder
        recorder.start()

        //初始化Camera，持续采集视频，把采集到的数据传给sdk
        uploadTask()
    }

    fun stopRecoder() {
        //停止XHCustomRecorder
        recorder.stop()

        //停止上传任务
        if (uploadTimer != null) {
            uploadTimer!!.cancel()
            uploadTimer = null
        }

        //销毁Camera
    }

    private fun uploadTask() {
        if (uploadTimer != null) {
            uploadTimer!!.cancel()
            uploadTimer = null
            uploadTimer = Timer()
        } else {
            uploadTimer = Timer()
        }
        uploadTimer!!.schedule(object : TimerTask() {
            @SuppressLint("NewApi", "LocalSuppress")
            override fun run() {
                uploadVideoData()
            }
        }, 0, (1000 / fps).toLong())
    }

    @SuppressLint("NewApi", "LocalSuppress")
    private fun uploadVideoData() {
        recorder.fillData_NV21(frameData)
    }
}
