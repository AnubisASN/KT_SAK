package com.anubis.module_tts.listener

import android.util.Log
import com.anubis.kt_extends.eLog

import com.anubis.module_tts.control.MainHandlerConstant
import com.anubis.module_tts.eTTS
import com.baidu.tts.client.SpeechError
import com.baidu.tts.client.SpeechSynthesizerListener

/**
 * SpeechSynthesizerListener 简单地实现，仅仅记录日志
 * Created by fujiayi on 2017/5/19.
 */

open class MessageListener : SpeechSynthesizerListener, MainHandlerConstant {

    /**
     * 播放开始，每句播放开始都会回调
     *
     * @param utteranceId
     */
    private  val MSG_STATE_TTS_SPEAK_OVER=0
    private  val MSG_STATE_TTS_SPEAK_START=1
   protected  val MSG_TYPE_TTS = 33
    override fun onSynthesizeStart(utteranceId: String) {
//        sendMessage("准备开始合成,序列号:" + utteranceId)
    }

    /**
     * 语音流 16K采样率 16bits编码 单声道 。
     *
     * @param utteranceId
     * @param bytes       二进制语音 ，注意可能有空data的情况，可以忽略
     * @param progress    如合成“百度语音问题”这6个字， progress肯定是从0开始，到6结束。 但progress无法和合成到第几个字对应。
     */
    override fun onSynthesizeDataArrived(utteranceId: String, bytes: ByteArray, progress: Int) {
        //  Log.i(TAG, "合成进度回调, progress：" + progress + ";序列号:" + utteranceId );
    }

    /**
     * 合成正常结束，每句合成正常结束都会回调，如果过程中出错，则回调onError，不再回调此接口
     *
     * @param utteranceId
     */
    override fun onSynthesizeFinish(utteranceId: String) {
//        sendMessage("合成结束回调, 序列号:" + utteranceId)
    }

    override fun onSpeechStart(utteranceId: String) {
        sendMessage("播放开始回调, 序列号:" + utteranceId,MSG_TYPE_TTS,MSG_STATE_TTS_SPEAK_START)
    }

    /**
     * 播放进度回调接口，分多次回调
     *
     * @param utteranceId
     * @param progress    如合成“百度语音问题”这6个字， progress肯定是从0开始，到6结束。 但progress无法保证和合成到第几个字对应。
     */
    override fun onSpeechProgressChanged(utteranceId: String, progress: Int) {
          eLog( "播放进度回调, progress：" + progress + ";序列号:" + utteranceId );
    }

    /**
     * 播放正常结束，每句播放正常结束都会回调，如果过程中出错，则回调onError,不再回调此接口
     *
     * @param utteranceId
     */
    override fun onSpeechFinish(utteranceId: String) {
        sendMessage("播放结束回调, 序列号:" + utteranceId,MSG_TYPE_TTS,MSG_STATE_TTS_SPEAK_OVER)
        eLog("播放结束")
    }

    /**
     * 当合成或者播放过程中出错时回调此接口
     *
     * @param utteranceId
     * @param speechError 包含错误码和错误信息
     */
    override fun onError(utteranceId: String, speechError: SpeechError) {
        sendErrorMessage("错误发生：" + speechError.description + "，错误编码："
                + speechError.code + "，序列号:" + utteranceId)
    }

    private fun sendErrorMessage(message: String) {
        sendMessage(message, true)
    }


    private fun sendMessage(message: String) {
        sendMessage(message, false)
    }
    protected open fun sendMessage(message: String, type:Int,action:Int) {
    }
    protected open fun sendMessage(message: String, isError: Boolean) {
        if (isError) {
            Log.e(TAG, message)
        } else {
            Log.i(TAG, message)
        }

    }

    companion object {
        private val TAG = "MessageListener"
    }
}
