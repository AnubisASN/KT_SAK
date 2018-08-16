package com.anubis.module_tts.listener

import android.os.Handler
import android.os.Message
import android.util.Log
import com.anubis.kt_extends.eLog
import com.anubis.module_tts.control.MainHandlerConstant.Companion.PRINT
import com.anubis.module_tts.control.MainHandlerConstant.Companion.UI_CHANGE_INPUT_TEXT_SELECTION
import com.anubis.module_tts.control.MainHandlerConstant.Companion.UI_CHANGE_SYNTHES_TEXT_SELECTION

/**
 * 在 MessageListener的基础上，和UI配合。
 * Created by fujiayi on 2017/9/14.
 */

open class UiMessageListener(private val mainHandler: Handler?) : MessageListener() {

    /**
     * 合成数据和进度的回调接口，分多次回调。
     * 注意：progress表示进度，与播放到哪个字无关
     * @param utteranceId
     * @param data 合成的音频数据。该音频数据是采样率为16K，2字节精度，单声道的pcm数据。
     * @param progress 文本按字符划分的进度，比如:你好啊 进度是0-3
     */
    override fun onSynthesizeDataArrived(utteranceId: String, data: ByteArray, progress: Int) {
        // sendMessage("onSynthesizeDataArrived");
        mainHandler!!.sendMessage(mainHandler.obtainMessage(UI_CHANGE_SYNTHES_TEXT_SELECTION, progress, 0))
    }

    /**
     * 播放进度回调接口，分多次回调
     * 注意：progress表示进度，与播放到哪个字无关
     *
     * @param utteranceId
     * @param progress 文本按字符划分的进度，比如:你好啊 进度是0-3
     */
    override fun onSpeechProgressChanged(utteranceId: String, progress: Int) {
        // sendMessage("onSpeechProgressChanged");
        mainHandler!!.sendMessage(mainHandler.obtainMessage(UI_CHANGE_INPUT_TEXT_SELECTION, progress, 0))
    }

    protected fun sendMessage(message: String) {
        sendMessage(message, false)
    }

//
    protected override fun sendMessage(message: String,type:Int, action: Int) {
        if (mainHandler != null) {
            val msg = Message.obtain()
            msg.arg1 = type
            msg.what = action
            msg.obj = message + "\n"
            mainHandler.sendMessage(msg)
            eLog("MY SendMSG")
        }
    }


    override fun sendMessage(message: String, isError: Boolean) {
        sendMessage(message, isError, PRINT)
    }


    protected fun sendMessage(message: String, isError: Boolean, action: Int) {
        super.sendMessage(message, isError)
        if (mainHandler != null) {
            val msg = Message.obtain()
            msg.arg1 = 100100
            msg.what = action
            msg.obj = message + "\n"
            mainHandler.sendMessage(msg)
            eLog("PR SendMSG")
            Log.i(TAG, message)
        }
    }

    companion object {

        private val TAG = "UiMessageListener"
    }
}
