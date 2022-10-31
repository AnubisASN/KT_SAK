package com.anubis.module_asrw

import android.content.Context
import android.os.Handler
import com.anubis.kt_extends.eLog
import com.anubis.module_asrw.control.MyRecognizer
import com.anubis.module_asrw.control.MyWakeup
import com.anubis.module_asrw.recognization.IStatus
import com.anubis.module_asrw.recognization.MessageStatusRecogListener
import com.anubis.module_asrw.recognization.PidBuilder
import com.anubis.module_asrw.recognization.RecogWakeupListener
import com.anubis.module_asrw.util.Logger
import com.baidu.speech.asr.SpeechConstant
import java.util.LinkedHashMap
import kotlin.collections.HashMap
import kotlin.collections.set

/**
*说明：语音唤醒与识别集成调用
*@调用方法：开始（）
*@param context：Context;上下文
*@param handler：Handler;语音唤醒与识别消息回调
*@param params：HashMap <String,Any>;参数设置（参考文档，百度语音识别输出参数）
*@return：eASRW
*说明：语音识别调用
*@调用方法：ASR（）
*@param context：Context;上下文
*@param handler：Handler;语音识别消息回调
*@param backTrackInMs：Int;语音回溯时间
*@param AID_AKY_SKY：数组; APP_ID，APP_KEY，SECRET
**/
/**
 * 唤醒后识别 本例可与ActivityWakeUp 及对比作为集成识别代码的参考
 */
object eASRW : IStatus {

    /**
     * 识别控制器，使用MyRecognizer控制识别的流程
     */
    var myRecognizer: MyRecognizer? = null
    /**
     * 0: 方案1， 唤醒词说完后，直接接句子，中间没有停顿。
     * >0 : 方案2： 唤醒词说完后，中间有停顿，然后接句子。推荐4个字 1500ms
     *
     *
     * backTrackInMs 最大 15000，即15s
     */

    private var myWakeup: MyWakeup? = null
    private val status = IStatus.STATUS_NONE
    fun start(context: Context, handler: Handler,AID_AKY_SKY: Array<String> = arrayOf("13612239", "yfXyxUQXxDO7Vcp6h7LtH3RC", "UdKuiwWqIeFlzr3aGUNEutCkA0avXE3o"), params: HashMap<String, Any> = HashMap()):eASRW{
        asrStart(context,handler)
        wakeStart(context,handler, AID_AKY_SKY,params)
        return this@eASRW
    }

   private fun asrStart(context: Context, handler: Handler ) {
        Logger.setHandler(handler)
        // 初始化识别引擎
        val recogListener = MessageStatusRecogListener(handler)
        myRecognizer = myRecognizer ?: MyRecognizer(context, recogListener)

    }

   private fun wakeStart(context: Context, handler: Handler,AID_AKY_SKY:Array<String>, params: HashMap<String, Any> = HashMap()){
        val listener = RecogWakeupListener(handler)
        myWakeup = myWakeup ?: MyWakeup(context, listener)
        params[SpeechConstant.WP_WORDS_FILE] = "assets:///WakeUp.bin"
       params[SpeechConstant.APP_ID]=AID_AKY_SKY[0]
       params[SpeechConstant.APP_KEY]=AID_AKY_SKY[1]
       params[SpeechConstant.SECRET]=AID_AKY_SKY[2]
        myWakeup!!.start(params)
    }
    fun ASR(context: Context, handler: Handler ,backTrackInMs: Int = 1500,AID_AKY_SKY: Array<String> = arrayOf("13612239", "yfXyxUQXxDO7Vcp6h7LtH3RC", "UdKuiwWqIeFlzr3aGUNEutCkA0avXE3o")) {
        eLog("语音开始识别")
//                  此处 开始正常识别流程
        val params = LinkedHashMap<String, Any>()
        params[SpeechConstant.ACCEPT_AUDIO_VOLUME] = false
        params[SpeechConstant.VAD] = SpeechConstant.VAD_DNN
        // 如识别短句，不需要需要逗号，将PidBuilder.INPUT改为搜索模型PidBuilder.SEARCH
        params[SpeechConstant.PID] = PidBuilder.create().model(PidBuilder.INPUT).toPId()
        params[SpeechConstant.APP_ID]=AID_AKY_SKY[0]
        params[SpeechConstant.APP_KEY]=AID_AKY_SKY[1]
        params[SpeechConstant.SECRET]=AID_AKY_SKY[2]
        if (backTrackInMs > 0) { // 方案1， 唤醒词说完后，直接接句子，中间没有停顿。
            params[SpeechConstant.AUDIO_MILLS] = System.currentTimeMillis() - backTrackInMs
        }
        myRecognizer?.cancel()?: asrStart(context,handler)
        myRecognizer?.start(params)
    }
    fun asrwStop() {
        myRecognizer?.stop()
    }

    fun asrwDestroy() {
        myRecognizer?.release()
        myWakeup?.release()
    }
}
