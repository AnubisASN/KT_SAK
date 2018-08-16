package com.anubis.module_tts.control


import com.baidu.tts.client.SpeechSynthesizerListener
import com.baidu.tts.client.TtsMode

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
    val appId = "11010053"
    val appKey = "Yv1fNRycpH0SqaIvqVhKkd9k"
    val secretKey = "rOf0ISjaTHDAzzK3q8VP5lrfCsYEntTK"

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
