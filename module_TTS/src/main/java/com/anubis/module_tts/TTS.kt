/**
 *  * TTS调用与设置 即插即用接口封装适配
 */
package com.anubis.module_tts

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Handler
import android.util.Log
import android.util.Pair
import com.anubis.kt_extends.eGetSystemSharedPreferences
import com.anubis.kt_extends.eLog
import com.anubis.kt_extends.eLogE
import com.anubis.kt_extends.eSetSystemSharedPreferences
import com.anubis.module_tts.Bean.voiceModel

import com.anubis.module_tts.control.InitConfig
import com.anubis.module_tts.control.MySyntherizer
import com.anubis.module_tts.control.NonBlockSyntherizer
import com.anubis.module_tts.listener.UiMessageListener
import com.baidu.tts.chainofresponsibility.logger.LoggerProxy
import com.baidu.tts.client.SpeechSynthesizer
import com.baidu.tts.client.TtsMode

import java.io.IOException
import com.anubis.module_tts.util.OfflineResource
import java.util.*


/**
 * 合成demo。含在线和离线，没有纯离线功能。
 * 根据网络状况优先走在线，在线时访问服务器失败后转为离线。
 */
//(val mActivity: Context, val mHandler: Handler)
object TTS {
    // ================== 初始化参数设置开始 ==========================
    private var mActivity: Activity? = null
    private  var mHandler:Handler?=null
    // TtsMode.MIX; 离在线融合，在线优先； TtsMode.ONLINE 纯在线； 没有纯离线
    private var ttsMode = TtsMode.MIX
    // 离线发音选择，VOICE_FEMALE即为离线女声发音。
    // assets目录下bd_etts_common_speech_m15_mand_eng_high_am-mix_v3.0.0_20170505.dat为离线男声模型；
    // assets目录下bd_etts_common_speech_f7_mand_eng_high_am-mix_v3.0.0_20170512.dat为离线女声模型
    private var offlineVoice = OfflineResource.VOICE_DUYY

    /**
     * ====================================TTS设置====================================
     */
    // ===============初始化参数设置完毕，更多合成参数请至getParams()方法中设置 =================
    // 主控制类，所有合成控制方法从这个类开始
    private var synthesizer: MySyntherizer? = null


    /**
     * 合成的参数，可以初始化时填写，也可以在合成前设置。
     *
     * @return
     */
    // 以下参数均为选填
    // 该参数设置为TtsMode.MIX生效。即纯在线模式不生效。
    // MIX_MODE_DEFAULT 默认 ，wifi状态下使用在线，非wifi离线。在线状态下，请求超时6s自动转离线
    // MIX_MODE_HIGH_SPEED_SYNTHESIZE_WIFI wifi状态下使用在线，非wifi离线。在线状态下， 请求超时1.2s自动转离线
    // MIX_MODE_HIGH_SPEED_NETWORK ， 3G 4G wifi状态下使用在线，其它状态离线。在线状态下，请求超时1.2s自动转离线
    // MIX_MODE_HIGH_SPEED_SYNTHESIZE, 2G 3G 4G wifi状态下使用在线，其它状态离线。在线状态下，请求超时1.2s自动转离线
    // 离线资源文件， 从assets目录中复制到临时目录，需要在initTTs方法前完成
    // 声学模型文件路径 (离线引擎使用), 请确认下面两个文件存在
    private val params: HashMap<String, String> get() {
            val params = HashMap<String, String>()
            eLog("设置在线发声音人:" + mActivity!!.eGetSystemSharedPreferences("set_tts_load_model"))
//设置本地发音人
            params[SpeechSynthesizer.PARAM_SPEAKER] = when (mActivity!!.eGetSystemSharedPreferences("set_tts_load_model")) {
                "F" -> "0"
                "M" -> "1"
                "X" -> "3"
                "Y" -> "4"
                else -> {
                    "4"
                }
            }

            //            设置合成的音量，0-9 ，默认 9
            params[SpeechSynthesizer.PARAM_VOLUME] = mActivity!!.eGetSystemSharedPreferences("set_PARAM_VOLUME").toString() ?: "9"
            // 设置合成的语速，0-9 ，默认 5
            params[SpeechSynthesizer.PARAM_SPEED] = mActivity!!.eGetSystemSharedPreferences("set_PARAM_SPEED").toString() ?: "5"
            // 设置合成的语调，0-9 ，默认 5
            params[SpeechSynthesizer.PARAM_PITCH] = mActivity!!.eGetSystemSharedPreferences("set_PARAM_PITCH").toString() ?: "5"
            params[SpeechSynthesizer.PARAM_MIX_MODE] = SpeechSynthesizer.MIX_MODE_HIGH_SPEED_SYNTHESIZE_WIFI
            val offlineResource = createOfflineResource(offlineVoice)
            if (offlineResource == null) {
                eLogE("offlineResource==null")
            }
            params[SpeechSynthesizer.PARAM_TTS_TEXT_MODEL_FILE] = offlineResource?.textFilename.toString()
            params[SpeechSynthesizer.PARAM_TTS_SPEECH_MODEL_FILE] = offlineResource?.modelFilename.toString()
            return params
        }

    /**
     * 切换离线发音。注意需要添加额外的判断：引擎在合成时该方法不能调用
    map["离线女声"] = OfflineResource.VOICE_FEMALE
    map["离线男声"] = OfflineResource.VOICE_MALE
    map["离线度逍遥"] = OfflineResource.VOICE_DUXY
    map["离线度丫丫"] = OfflineResource.VOICE_DUYY
     */
    private fun voiceModel(mode: String) {
        offlineVoice = mode
        val offlineResource = createOfflineResource(offlineVoice)
//        toPrint("切换离线语音：" + offlineResource!!.modelFilename!!)
//        val result = synthesizer!!.loadModel(offlineResource!!.modelFilename.toString(), offlineResource!!.textFilename.toString())
//        checkResult(result, "loadModel")
    }


    fun initTTS(activity: Activity, mHandler: Handler) {
        this.mActivity=activity
        this.mHandler=mHandler
        initialTts()
        var mode = mActivity?.eGetSystemSharedPreferences("set_tts_load_model").toString() ?: "Y"
        eLog("mode:" + mode)
        voiceModel(mode)

    }
fun setParams(voiceMode: voiceModel=voiceModel.CHILDREN, volume:Int=9, speed:Int=5, pitch:Int=5){
    val mode=when(voiceMode){
        voiceModel.FEMALE->"F"
        voiceModel.MALE->"M"
        voiceModel.EMOTIONAL_MALE->"X"
        voiceModel.CHILDREN->"Y"
    }
    mActivity!!.eSetSystemSharedPreferences("set_tts_load_model", mode)
    mActivity!!.eSetSystemSharedPreferences("set_PARAM_VOLUME",volume.toString())
    mActivity!!.eSetSystemSharedPreferences("set_PARAM_SPEED",speed.toString())
    mActivity!!.eSetSystemSharedPreferences("set_PARAM_PITCH",pitch.toString())
    eLog("sss:"+ mActivity!!.eGetSystemSharedPreferences("set_tts_load_model", mode)+
            mActivity!!.eGetSystemSharedPreferences("set_PARAM_VOLUME",volume.toString())+
    mActivity!!.eGetSystemSharedPreferences("set_PARAM_SPEED",speed.toString())+
    mActivity!!.eGetSystemSharedPreferences("set_PARAM_PITCH",pitch.toString()))
}


    /**
     * 初始化引擎，需要的参数均在InitConfig类里
     *
     *
     * DEMO中提供了3个SpeechSynthesizerListener的实现
     * MessageListener 仅仅用log.i记录日志，在logcat中可以看见
     * UiMessageListener 在MessageListener的基础上，对handler发送消息，实现UI的文字更新
     * FileSaveListener 在UiMessageListener的基础上，使用 onSynthesizeDataArrived回调，获取音频流
     */
    private fun initialTts() {
        LoggerProxy.printable(true) // 日志打印在logcat中
        // 设置初始化参数
        // 此处可以改为 含有您业务逻辑的SpeechSynthesizerListener的实现类

        val listener = UiMessageListener(mHandler)

        val params = params

        /**
         * 初始化设置模块
         */
        val initConfig = InitConfig(ttsMode, params, listener)

        synthesizer = NonBlockSyntherizer(mActivity!!, initConfig, mHandler!!) // 此处可以改为MySyntherizer 了解调用过程
    }


    private fun createOfflineResource(voiceType: String): OfflineResource? {
        var offlineResource: OfflineResource? = null
        try {
            offlineResource = OfflineResource(mActivity!!, voiceType)
        } catch (e: IOException) {
            // IO 错误自行处理
            e.printStackTrace()
            toPrint("【error】:copy files from assets failed." + e.message)
        }

        return offlineResource
    }

    private fun toPrint(s: String) {
        Log.i("TAG", s)
    }
    // ================== 调用方法 ==========================
    /**
     * speak 实际上是调用 synthesize后，获取音频流，然后播放。
     * 获取音频流的方式见SaveFileActivity及FileSaveListener
     * 需要合成的文本text的长度不能超过1024个GBK字节。
     */
    public fun speak(text: String) {
        val result = synthesizer?.speak(text)
        if (result != null) {
            checkResult(result, "speak")
        }
    }

    /**
     * 批量播放
     */
    public fun batchSpeak(texts: ArrayList<Pair<String, String>>) {
        val result = synthesizer!!.batchSpeak(texts)
        checkResult(result, "batchSpeak")
    }

    /**
     * 暂停播放。仅调用speak后生效
     */
    public fun pause() {
        val result = synthesizer!!.pause()
        checkResult(result, "pause")
    }

    /**
     * 继续播放。仅调用speak后生效，调用pause生效
     */
    public fun resume() {
        val result = synthesizer!!.resume()
        checkResult(result, "resume")
    }

    /**
     * 停止合成引擎。即停止播放，合成，清空内部合成队列。
     */
    public fun stop() {
        val result = synthesizer!!.stop()
        checkResult(result, "stop")
    }

    // ================== ============= ==========================
    //检查回调方法
    private fun checkResult(result: Int, method: String) {
        if (result != 0) {
            toPrint("error code :$result method:$method, 错误码文档:http://yuyin.baidu.com/docs/tts/122 ")
        }
    }

    fun ttsDestroy() {
        synthesizer!!.release()
        eLog("TTS释放资源成功")
    }

//    companion object {
//        private val TAG = "TTS"
//        private var initTTS: TTS? = null
//        fun getTTS() = initTTS
//    }

}
