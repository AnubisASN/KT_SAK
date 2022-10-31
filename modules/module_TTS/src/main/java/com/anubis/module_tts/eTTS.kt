/**
 *  * TTS调用与设置 即插即用接口封装适配
 */
package com.anubis.module_tts

import android.annotation.TargetApi
import android.app.Application
import android.os.Build
import android.os.Handler
import android.speech.tts.TextToSpeech
import android.util.Pair
import androidx.annotation.RequiresApi
import com.anubis.kt_extends.*
import com.anubis.module_tts.Bean.ParamMixMode
import com.anubis.module_tts.Bean.TTSMode
import com.anubis.module_tts.Bean.VoiceModel
import com.anubis.module_tts.control.InitConfig
import com.anubis.module_tts.control.MySyntherizer
import com.anubis.module_tts.control.NonBlockSyntherizer
import com.anubis.module_tts.listener.UiMessageListener
import com.anubis.module_tts.util.OfflineResource
import com.anubis.module_ttse.eTTSE
import com.baidu.tts.chainofresponsibility.logger.LoggerProxy
import com.baidu.tts.client.SpeechSynthesizer
import com.baidu.tts.client.SpeechSynthesizerListener
import com.baidu.tts.client.TtsMode
import java.util.*

/**
 * 说明：百度离在线语音合成封装开发库（PS：智能硬件无Intent网接入网线有2.5s延迟，）
 * @初始化方法：initTTS()
 * @param activity: Application；应用程序
 * @param mHandler: Handler；语音合成消息回调
 * @param ttsMode: TTSMode；语音合成模式（在线离线）
 * @param voiceMode: VoiceModel;语音合成模型
 * @Param MixMode: ParamMixMode;语音合成流程
 * @Param AID_AKY_SKY: Array<Stirng>;百度语音秘钥（离在混合模式需要）
 * @param extendEngineBlock: ((eTTSE?) -> eTTSE?)? = null ;第三方引擎扩展
 * @return: eTTS
 * */

/**
 * 合成demo。含在线和离线，没有纯离线功能。
 * 根据网络状况优先走在线，在线时访问服务器失败后转为离线。
 */
//(val mActivity: Context, val mHandler: Handler)
@TargetApi(Build.VERSION_CODES.LOLLIPOP)
class eTTS private constructor() {
    // ================== 初始化参数设置开始 ==========================
    // TtsMode.MIX; 离在线融合，在线优先； TtsMode.ONLINE 纯在线； 没有纯离线
    // 离线发音选择，VOICE_FEMALE即为离线女声发音。
    // assets目录下bd_etts_common_speech_m15_mand_eng_high_am-mix_v3.0.0_20170505.dat为离线男声模型；
    // assets目录下bd_etts_common_speech_f7_mand_eng_high_am-mix_v3.0.0_20170512.dat为离线女声模型
    private var offlineVoice = OfflineResource.VOICE_DUYY
    private var synthesizer: MySyntherizer? = null

    companion object {
        private lateinit var mActivity: Application
        private lateinit var mHandler: Handler
        private lateinit var KEYS: Array<String>
        private var mExtendEngineBlock: ((eTTSE?) -> eTTSE?)? = null
        private var mTTSE: eTTSE? = null
        private var mTTSMode = TTSMode.MIX
        private var mVoiceModel: VoiceModel = VoiceModel.CHILDREN
        private lateinit var mListener: SpeechSynthesizerListener
        fun eInit(mApplication: Application, AID_AKY_SKY: Array<String>, mHandler: Handler = Handler(), ttsMode: TTSMode = TTSMode.MIX, voiceMode: VoiceModel = VoiceModel.FEMALE, ParamMixMode: ParamMixMode = com.anubis.module_tts.Bean.ParamMixMode.MIX_MODE_HIGH_SPEED_NETWORK, listener: SpeechSynthesizerListener = UiMessageListener(mHandler), extendEngineBlock: ((eTTSE?) -> eTTSE?)? = null): eTTS {
            this.mActivity = mApplication
            this.mHandler = mHandler
            mExtendEngineBlock = extendEngineBlock
            KEYS = AID_AKY_SKY
            mVoiceModel = voiceMode

            mApplication.eSetSystemSharedPreferences("set_tts_load_model", when (mVoiceModel) {
                VoiceModel.FEMALE -> "F"
                VoiceModel.MALE -> "M"
                VoiceModel.EMOTIONAL_MALE -> "X"
                VoiceModel.CHILDREN -> "Y"
            })
            mApplication.eSetSystemSharedPreferences("set_PARAM_MIX_MODE", ParamMixMode)
            mTTSMode = ttsMode
            mListener = listener
            return eInit
        }

        private val eInit by lazy(LazyThreadSafetyMode.SYNCHRONIZED) { eTTS() }
    }

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
    private val params: HashMap<String, String>
        get() {
            val params = HashMap<String, String>()
            eLog("设置在线发声音人:" + mActivity.eGetSystemSharedPreferences("set_tts_load_model", ""))
//设置本地发音人
            params[SpeechSynthesizer.PARAM_SPEAKER] = when (mActivity.eGetSystemSharedPreferences("set_tts_load_model", "")) {
                "F" -> "0"
                "M" -> "1"
                "X" -> "3"
                "Y" -> "4"
                else -> {
                    "4"
                }
            }
            // 设置合成的音量，0-9 ，默认 9
            params[SpeechSynthesizer.PARAM_VOLUME] = mActivity.eGetSystemSharedPreferences("set_PARAM_VOLUME", "9")

            // 设置合成的语速，0-9 ，默认 5
            params[SpeechSynthesizer.PARAM_SPEED] = mActivity.eGetSystemSharedPreferences("set_PARAM_SPEED", "5")
            // 设置合成的语调，0-9 ，默认 5
            params[SpeechSynthesizer.PARAM_PITCH] = mActivity.eGetSystemSharedPreferences("set_PARAM_PITCH", "5")
//            params[SpeechSynthesizer.PARAM_MIX_MODE] = SpeechSynthesizer.MIX_MODE_HIGH_SPEED_SYNTHESIZE_WIFI
            params[SpeechSynthesizer.PARAM_MIX_MODE] = mActivity.eGetSystemSharedPreferences("set_PARAM_MIX_MODE", SpeechSynthesizer.MIX_MODE_HIGH_SPEED_SYNTHESIZE)
            val offlineResource = createOfflineResource(offlineVoice)
            if (offlineResource == null) {
                eLog("offlineResource==null")
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
    private fun VoiceModel(mode: String) {
        offlineVoice = mode
        val offlineResource = createOfflineResource(offlineVoice)
//        eLog("切换离线语音：" + offlineResource!!.modelFilename!!)
//        val result = synthesizer!!.loadModel(offlineResource!!.modelFilename.toString(), offlineResource!!.textFilename.toString())
//        checkResult(result, "loadModel")
    }

    init {
        eLog("类初始化")
        eSetParams()
    }

    /*
        * @参数设置方法：setParams()
        * @param voiceMode: VoiceModel;语音合成模型
        * @param volume: Int;设置音量
        * @param speed: Int;设置速度
        * @param pitch: Int;设置语调
        * @param extendEngineBlock: ((eTTSE?) -> eTTSE?)? = null ;第三方引擎扩展
        * @return:eTTS
        * @语音合成：speak（）
        * @param text:String;合成内容
        */
    fun eSetParams(voiceMode: VoiceModel = mVoiceModel, volume: Int = 9, speed: Int = 5, pitch: Int = 5, extendEngineBlock: ((eTTSE?) -> eTTSE?)? = mExtendEngineBlock) {
        if (extendEngineBlock == null) {
            if (synthesizer != null)
                return
            mTTSE?.eClean()
            mTTSE = null
            mVoiceModel = voiceMode
            val mode = when (mVoiceModel) {
                VoiceModel.FEMALE -> "F"
                VoiceModel.MALE -> "M"
                VoiceModel.EMOTIONAL_MALE -> "X"
                VoiceModel.CHILDREN -> "Y"
            }
            VoiceModel(mode)
            mActivity.eSetSystemSharedPreferences("set_tts_load_model", mode)
            mActivity.eSetSystemSharedPreferences("set_PARAM_VOLUME", volume.toString())
            mActivity.eSetSystemSharedPreferences("set_PARAM_SPEED", speed.toString())
            mActivity.eSetSystemSharedPreferences("set_PARAM_PITCH", pitch.toString())
            try {
                init(mListener)
            } catch (e: Exception) {
                e.eLogE("initTTS错误")
                init(mListener)
            }

        } else {
            synthesizer?.release()
            synthesizer = null
            mTTSE = extendEngineBlock(mTTSE)
        }
        mExtendEngineBlock = extendEngineBlock
    }


    /**
     * 初始化引擎，需要的参数均在InitConfig类里
     * DEMO中提供了3个SpeechSynthesizerListener的实现
     * MessageListener 仅仅用log.i记录日志，在logcat中可以看见
     * UiMessageListener 在MessageListener的基础上，对handler发送消息，实现UI的文字更新
     * FileSaveListener 在UiMessageListener的基础上，使用 onSynthesizeDataArrived回调，获取音频流
     */

    private fun init(listener: SpeechSynthesizerListener) {
        LoggerProxy.printable(true) // 日志打印在logcat中
        // 设置初始化参数
        // 此处可以改为 含有您业务逻辑的SpeechSynthesizerListener的实现类
        val params = params
        val initConfig = InitConfig(if (mTTSMode == TTSMode.MIX) TtsMode.MIX else TtsMode.ONLINE, params, listener)
        synthesizer = NonBlockSyntherizer(mActivity, initConfig, mHandler) // 此处可以改为MySyntherizer 了解调用过程
    }

    private fun createOfflineResource(voiceType: String): OfflineResource? {
        var offlineResource: OfflineResource? = null
        try {
            offlineResource = OfflineResource(mActivity, voiceType)
        } catch (e: Exception) {
            // IO 错误自行处理
            e.eLogE("createOfflineResource")
            eLog("【error】:copy files from assets failed." + e.message)
        }

        return offlineResource
    }
    // ================== 调用方法 ==========================
    /**
     * speak 实际上是调用 synthesize后，获取音频流，然后播放。
     * 获取音频流的方式见SaveFileActivity及FileSaveListener
     * 需要合成的文本text的长度不能超过1024个GBK字节。
     */
    fun eSpeak(text: String): Boolean {
        val result = synthesizer?.speak(text) ?: mTTSE?.eSpeak(text)
        if (result != null) {
            return checkResult(result, "speak")
        } else {
            eLog("result为空")
            return false
        }
    }

    /**
     * 合成
     */
    fun eSynthesize(text: String, id: String = "0"): Boolean {
        val result = synthesizer?.synthesize(text, id)
        if (result != null) {
            return checkResult(result, "synthesize")
        } else {
            eLog("result为空")
            return false
        }
    }


    /**
     * 批量播放
     */
    fun eBbatchSpeak(texts: ArrayList<Pair<String, String>>): Boolean {
        val result = synthesizer!!.batchSpeak(texts)
        return checkResult(result, "batchSpeak")
    }

    /**
     * 暂停播放。仅调用speak后生效
     */
    fun ePause(): Boolean {
        val result = synthesizer!!.pause()
        return checkResult(result, "pause")
    }

    /**
     * 继续播放。仅调用speak后生效，调用pause生效
     */
    fun eResume(): Boolean {
        val result = synthesizer!!.resume()
        return checkResult(result, "resume")
    }

    /**
     * 停止合成引擎。即停止播放，合成，清空内部合成队列。
     */
    fun stop(): Boolean {
        val result = synthesizer?.stop() ?: mTTSE?.eStop()
        return if (result != null)
            checkResult(result, "stop")
        else {
            eLog("result为空")
            false
        }
    }

    // ================== ============= ==========================
    //检查回调方法
    private fun checkResult(result: Int, method: String): Boolean {
        if (result != 0) {
            eLog("error code :$result method:$method, 错误码文档:http://yuyin.baidu.com/docs/tts/122 ")
            return false
        }
        return true
    }

    fun eTtsDestroy() {
        synthesizer?.release()
        mTTSE?.eClean()
        eLog("TTS释放资源成功")
    }
}
