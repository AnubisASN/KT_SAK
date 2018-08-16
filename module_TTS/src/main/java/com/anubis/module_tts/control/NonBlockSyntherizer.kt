package com.anubis.module_tts.control

import android.content.Context
import android.os.Build
import android.os.Handler
import android.os.HandlerThread
import android.os.Message

/**
 * 在新线程中调用initTTs方法。防止UI柱塞
 *
 *
 * Created by fujiayi on 2017/5/24.
 */

class NonBlockSyntherizer(context: Context, initConfig: InitConfig, mainHandler: Handler) : MySyntherizer(context, mainHandler) {
    private var hThread: HandlerThread? = null
    private var tHandler: Handler? = null


    init {
        initThread()
        runInHandlerThread(INIT, initConfig)
    }


    protected fun initThread() {
        hThread = HandlerThread("NonBlockSyntherizer-thread")
        hThread!!.start()
        tHandler = object : Handler(hThread!!.looper) {
            override fun handleMessage(msg: Message) {
                super.handleMessage(msg)
                when (msg.what) {
                    INIT -> {
                        val config = msg.obj as InitConfig
                        val isSuccess = init(config)
                        if (isSuccess) {
                            // speak("初始化成功");
                            sendToUiThread("NonBlockSyntherizer 初始化成功")
                        } else {
                            sendToUiThread("合成引擎初始化失败, 请查看日志")
                        }
                    }
                    RELEASE -> {
                        super@NonBlockSyntherizer.release()
                        if (Build.VERSION.SDK_INT < 18) {
                            looper.quit()
                        }
                    }
                    else -> {
                    }
                }

            }
        }
    }

    override fun release() {
        runInHandlerThread(RELEASE)
        if (Build.VERSION.SDK_INT >= 18) {
            hThread!!.quitSafely()
        }
    }

    private fun runInHandlerThread(action: Int, obj: Any? = null) {
        val msg = Message.obtain()
        msg.what = action
        msg.obj = obj
        tHandler!!.sendMessage(msg)
    }

    companion object {

        private val INIT = 1

        private val RELEASE = 11


        private val TAG = "NonBlockSyntherizer"
    }

}
