package com.anubis.module_tts.util

import android.content.Context
import android.content.res.AssetManager
import android.util.Log

import java.io.IOException
import java.util.HashMap

import android.content.ContentValues.TAG


/**
 * Created by fujiayi on 2017/5/19.
 */

class OfflineResource @Throws(IOException::class)
constructor(context: Context, voiceType: String) {

    private val assets: AssetManager
    private val destPath: String

    var textFilename: String? = null
        private set
    var modelFilename: String? = null
        private set

    init {
        var context = context
        context = context.applicationContext
        this.assets = context.applicationContext.assets
        this.destPath = FileUtil.createTmpDir(context)
        setOfflineVoiceType(voiceType)
    }

    @Throws(IOException::class)
    fun setOfflineVoiceType(voiceType: String) {
        val text = "bd_etts_text.dat"
        val model: String
        if (VOICE_MALE == voiceType) {
            model = "bd_etts_common_speech_m15_mand_eng_high_am-mix_v3.0.0_20170505.dat"
        } else if (VOICE_FEMALE == voiceType) {
            model = "bd_etts_common_speech_f7_mand_eng_high_am-mix_v3.0.0_20170512.dat"
        } else if (VOICE_DUXY == voiceType) {
            model = "bd_etts_common_speech_yyjw_mand_eng_high_am-mix_v3.0.0_20170512.dat"
        } else if (VOICE_DUYY == voiceType) {
            model = "bd_etts_common_speech_as_mand_eng_high_am_v3.0.0_20170516.dat"
        } else {
            model = "bd_etts_common_speech_as_mand_eng_high_am_v3.0.0_20170516.dat"
        }
        textFilename = copyAssetsFile(text)
        modelFilename = copyAssetsFile(model)

    }


    @Throws(IOException::class)
    private fun copyAssetsFile(sourceFilename: String): String {
        val destFilename = destPath + "/" + sourceFilename
        var recover = false
        val existed = mapInitied[sourceFilename] // 启动时完全覆盖一次
        if (existed == null || !existed) {
            recover = true
        }
        FileUtil.copyFromAssets(assets, sourceFilename, destFilename, recover)
        Log.i(TAG, "文件复制成功：" + destFilename)
        return destFilename
    }

    companion object {

        val VOICE_FEMALE = "F"

        val VOICE_MALE = "M"


        val VOICE_DUYY = "Y"

        val VOICE_DUXY = "X"

        private val SAMPLE_DIR = "baiduTTS"

        private val mapInitied = HashMap<String, Boolean>()
    }


}
