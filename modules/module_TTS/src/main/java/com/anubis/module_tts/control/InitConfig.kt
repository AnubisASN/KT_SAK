package com.anubis.module_tts.control


import com.anubis.kt_extends.eLog
import com.anubis.module_tts.eTTS
import com.baidu.tts.client.SpeechSynthesizerListener
import com.baidu.tts.client.TtsMode
import org.jetbrains.anko.custom.async
import java.lang.reflect.Field

/**
 * 合成引擎的初始化参数
 *
 *
 * Created by fujiayi on 2017/9/13.
 */

class InitConfig {
    /**
     * appId appKey 和 secretKey。注意如果需要离线合成功能,请在您申请的应用中填写包名。
     */
    val KEYS=eTTS::class.java.getDeclaredField("KEYS").apply { this.isAccessible=true }
    val appId = (KEYS!!.get(eTTS) as Array<String>)[0]
    val appKey = (KEYS!!.get(eTTS) as Array<String>)[1]
    val secretKey =(KEYS!!.get(eTTS) as Array<String>)[2]

    /**
     * 纯在线或者离在线融合
     */
    var ttsMode: TtsMode?=null


    /**
     * 初始化的其它参数，用于setParam
     */
    var params: Map<String, String>?=null

    /**
     * 合成引擎的回调
     */
    var listener: SpeechSynthesizerListener?=null

    private constructor() {

    }

    constructor( ttsMode: TtsMode,
                params: Map<String, String>, listener: SpeechSynthesizerListener) {
        this.ttsMode = ttsMode
        this.params = params
        this.listener = listener
    }
}
