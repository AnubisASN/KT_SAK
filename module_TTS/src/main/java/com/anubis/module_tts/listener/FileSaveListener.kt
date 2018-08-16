package com.anubis.module_tts.listener

import android.os.Handler
import android.util.Log

import com.baidu.tts.client.SpeechError

import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

/**
 * 保存回调音频流到文件。您也可以直接处理音频流
 * FileSaveListener 在UiMessageListener的基础上，使用 onSynthesizeDataArrived回调，获取音频流
 * Created by fujiayi on 2017/9/15.
 */

class FileSaveListener(mainHandler: Handler,
                       /**
                        * 保存文件的目录
                        */
                       private val destDir: String) : UiMessageListener(mainHandler) {

    /**
     * 保存的文件名 baseName + utteranceId， 通常是 output-0.pcm
     */
    private val baseName = "output-"

    /**
     * 文件
     */
    private var ttsFile: File? = null


    /**
     * ttsFile 文件流
     */
    private var ttsFileOutputStream: FileOutputStream? = null

    /**
     * ttsFile 文件buffer流
     */
    private var ttsFileBufferedOutputStream: BufferedOutputStream? = null

    override fun onSynthesizeStart(utteranceId: String) {
        val filename = baseName + utteranceId + ".pcm"
        // 保存的语音文件是 16K采样率 16bits编码 单声道 pcm文件。
        ttsFile = File(destDir, filename)
        Log.i(TAG, "try to write audio file to " + ttsFile!!.absolutePath)
        try {
            if (ttsFile!!.exists()) {
                ttsFile!!.delete()
            }
            ttsFile!!.createNewFile()
            // 创建FileOutputStream对象
            val ttsFileOutputStream = FileOutputStream(ttsFile!!)
            // 创建BufferedOutputStream对象
            ttsFileBufferedOutputStream = BufferedOutputStream(ttsFileOutputStream)
        } catch (e: IOException) {
            // 请自行做错误处理
            e.printStackTrace()
            sendMessage("创建文件失败:$destDir/$filename")
            throw RuntimeException(e)
        }

        sendMessage("创建文件成功:$destDir/$filename")

    }

    /**
     * 语音流 16K采样率 16bits编码 单声道 。
     *
     * @param utteranceId
     * @param data        二进制语音 ，注意可能有空data的情况，可以忽略
     * @param progress    如合成“百度语音问题”这6个字， progress肯定是从0开始，到6结束。 但progress无法保证和合成到第几个字对应。
     */
    override fun onSynthesizeDataArrived(utteranceId: String, data: ByteArray, progress: Int) {
        super.onSynthesizeDataArrived(utteranceId, data, progress)
        Log.i(TAG, "合成进度回调, progress：$progress;序列号:$utteranceId")
        try {
            ttsFileBufferedOutputStream!!.write(data)
        } catch (e: IOException) {
            e.printStackTrace()
        }

    }

    override fun onSynthesizeFinish(utteranceId: String) {
        super.onSynthesizeFinish(utteranceId)
        close()
    }

    /**
     * 当合成或者播放过程中出错时回调此接口
     *
     * @param utteranceId
     * @param speechError 包含错误码和错误信息
     */
    override fun onError(utteranceId: String, speechError: SpeechError) {
        close()
        super.onError(utteranceId, speechError)
    }

    /**
     * 关闭流，注意可能stop导致该方法没有被调用
     */
    private fun close() {
        if (ttsFileBufferedOutputStream != null) {
            try {
                ttsFileBufferedOutputStream!!.flush()
                ttsFileBufferedOutputStream!!.close()
                ttsFileBufferedOutputStream = null
            } catch (e2: Exception) {
                e2.printStackTrace()
            }

        }
        if (ttsFileOutputStream != null) {
            try {
                ttsFileOutputStream!!.close()
                ttsFileOutputStream = null
            } catch (e: IOException) {
                e.printStackTrace()
            }

        }
        sendMessage("关闭文件成功")
    }

    companion object {

        private val TAG = "FileSaveListener"
    }
}
