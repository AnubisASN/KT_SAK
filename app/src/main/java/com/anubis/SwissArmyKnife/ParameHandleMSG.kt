package com.anubis.SwissArmyKnife

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Handler
import android.os.Message
import com.anubis.SwissArmyKnife.MainActivity.Companion.mainActivity
import com.anubis.kt_extends.eLog
import com.anubis.kt_extends.eShowTip
import com.anubis.module_asrw.eASRW
import com.anubis.module_asrw.recognization.IStatus
import com.anubis.module_asrw.recognization.PidBuilder
import com.anubis.module_tcp.eTCP
import com.baidu.speech.asr.SpeechConstant
import com.huashi.otg.sdk.HandlerMsg
import java.util.LinkedHashMap

/**
 * Author  ： AnubisASN   on 19-9-26 下午2:48.
 * E-mail  ： anubisasn@gmail.com ( anubisasn@qq.com )
 *  Q Q： 773506352
 *命名规则定义：
 *Module :  module_'ModuleName'
 *Library :  lib_'LibraryName'
 *Package :  'PackageName'_'Module'
 *Class :  'Mark'_'Function'_'Tier'
 *Layout :  'Module'_'Function'
 *Resource :  'Module'_'ResourceName'_'Mark'
 *Layout Id :  'LoayoutName'_'Widget'_'FunctionName'
 *Class Id :  'LoayoutName'_'Widget'+'FunctionName'
 *Router :  /'Module'/'Function'
 *说明：
 */
@SuppressLint("StaticFieldLeak")
object ParameHandleMSG {
    var mainActivity: MainActivity? = null
    var state = true
    val handleMsg = @SuppressLint("HandlerLeak")
    object : Handler() {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            handleMsg(msg)
            handleOtg(msg)
        }
    }
    val handlePort = @SuppressLint("HandlerLeak")
    object : Handler() {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            handleProt(msg)
        }
    }

    val handleTTS = @SuppressLint("HandlerLeak")
    object : Handler() {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            handleTTS(msg)
        }
    }

    val handleTCP = @SuppressLint("HandlerLeak")
    object : Handler() {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            handleTCP(msg)
        }
    }

    val handleWeb = @SuppressLint("HandlerLeak")
    object : Handler() {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            handleWeb(msg)
        }
    }

    val uHandler = @SuppressLint("HandlerLeak")
    object : Handler() {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            handleUSB(msg)
        }
    }
    /**
     * -----------------------------------------回调结果模块——————————————————————————————————————————
     */


    //    0 唤醒成功         3    引擎就绪 开始说话            4 监测到说话      9001  监测到结束说话        5  临时识别      6  识别结束        2 识别引擎空闲
//    arg1 类型   arg2 最终状态   what  引擎状态   obj String消息
    private val backTrackInMs = 2000
    private val MSG_TYPE_WUR = 11
    private val MSG_TYPE_ASR = 22
    private val MSG_TYPE_TTS = 33
    private val MSG_STATE_TTS_SPEAK_OVER = 0
    private val MSG_STATE_TTS_SPEAK_START = 1
    var asrw: eASRW? = null
    private fun handleMsg(msg: Message) {
        if (msg.what == 5000) {
            if (msg.arg1 == 5000) {
                state = false
            }
//            onUiThread { progressDialog!!.incrementProgressBy(1) }// 增加进度条的进度  }
            eLog("msg:${msg.arg1}")
            return
        }


        if (msg.what == IStatus.STATUS_WAKEUP_SUCCESS) {
            mainActivity!!.Hint("语音唤醒成功:--arg1:${msg.arg1}--arg2:${msg.arg2}--what:${msg.what}--obj:${msg.obj}")
            eLog("语音唤醒成功:--arg1:${msg.arg1}--arg2:${msg.arg2}--what:${msg.what}--obj:${msg.obj}")
            mainActivity!!.eShowTip("语音唤醒成功")
//                  此处 开始正常识别流程
            val params = LinkedHashMap<String, Any>()
            params[SpeechConstant.ACCEPT_AUDIO_VOLUME] = false
            params[SpeechConstant.VAD] = SpeechConstant.VAD_DNN
            // 如识别短句，不需要需要逗号，将PidBuilder.INPUT改为搜索模型PidBuilder.SEARCH
            params[SpeechConstant.PID] = PidBuilder.create().model(PidBuilder.INPUT).toPId()
            if (backTrackInMs > 0) { // 方案1， 唤醒词说完后，直接接句子，中间没有停顿。
                params[SpeechConstant.AUDIO_MILLS] = System.currentTimeMillis() - backTrackInMs
            }
            asrw?.myRecognizer?.cancel()
            asrw?.myRecognizer?.start(params)
        }
        when (msg.what) {
            0 -> {
                //唤醒成功
                mainActivity!!.Hint("唤醒成功:--arg1:${msg.arg1}--arg2:${msg.arg2}--what:${msg.what}--obj:${msg.obj}")
                eLog("唤醒成功:--arg1:${msg.arg1}--arg2:${msg.arg2}--what:${msg.what}--obj:${msg.obj}")
            }
            IStatus.STATUS_NONE -> {
//                识别引擎空闲
                mainActivity!!.Hint("识别引擎空闲:--arg1:${msg.arg1}--arg2:${msg.arg2}--what:${msg.what}--obj:${msg.obj}")
                eLog("识别引擎空闲:--arg1:${msg.arg1}--arg2:${msg.arg2}--what:${msg.what}--obj:${msg.obj}")
            }
            IStatus.STATUS_READY -> {
//                引擎就绪 开始说话
                mainActivity!!.Hint("引擎就绪 开始说话:--arg1:${msg.arg1}--arg2:${msg.arg2}--what:${msg.what}--obj:${msg.obj}")
                eLog("引擎就绪 开始说话:--arg1:${msg.arg1}--arg2:${msg.arg2}--what:${msg.what}--obj:${msg.obj}")
            }
            IStatus.STATUS_SPEAKING -> {
//                监测到说话
                mainActivity!!.Hint("监测到说话:--arg1:${msg.arg1}--arg2:${msg.arg2}--what:${msg.what}--obj:${msg.obj}")
                eLog("监测到说话:--arg1:${msg.arg1}--arg2:${msg.arg2}--what:${msg.what}--obj:${msg.obj}")
            }
            IStatus.STATUS_RECOGNITION -> {
//                临时识别
                mainActivity!!.Hint("临时识别:--arg1:${msg.arg1}--arg2:${msg.arg2}--what:${msg.what}--obj:${msg.obj}")
                eLog("临时识别:--arg1:${msg.arg1}--arg2:${msg.arg2}--what:${msg.what}--obj:${msg.obj}")
            }
            IStatus.STATUS_FINISHED -> {//识别结束
                eLog("识别结束:--arg1:${msg.arg1}--arg2:${msg.arg2}--what:${msg.what}--obj:${msg.obj}")
                eLog("识别结束:--arg1:${msg.arg1}--arg2:${msg.arg2}--what:${msg.what}--obj:${msg.obj}")
                if (msg.arg2 == 1) {
                    mainActivity!!.Hint("最终识别：" + msg.obj.toString())
                    eLog("最终识别：" + msg.obj.toString())
                    mainActivity!!.eShowTip("最终识别：" + msg.obj.toString())
                }

            }
        }
    }

    private fun handleOtg(msg: Message) {
        if (msg.what == 99 || msg.what == 100) {
            eLog(msg.obj)
        }
        //第一次授权时候的判断是利用handler判断，授权过后就不用这个判断了
        if (msg.what == HandlerMsg.CONNECT_SUCCESS) {
            eLog("msg连接成功---${msg.obj}")
        }
        if (msg.what == HandlerMsg.CONNECT_ERROR) {
            eLog("msg连接失败")
        }
        if (msg.what == HandlerMsg.READ_ERROR) {
            //cz();
            //statu.setText("卡认证失败");
//            eLog("msg请放卡...")
        }
        if (msg.what == HandlerMsg.READ_SUCCESS) {
            eLog("msg读卡成功")
            eLog("读卡成功：" + msg.obj)
        }
    }

    private fun handleProt(msg: Message) {
        val data = msg.obj.toString()
        val datas = data.split("---")
        eLog("${datas[0]}---接收到：" + datas[1])

    }

    private fun handleTTS(msg: Message) {
        eLog("what:${msg.what}---obj:${msg.obj}---arg1:${msg.arg1}---arg2:${msg.arg2}")
    }

    private fun handleTCP(msg: Message) {
        mainActivity?.Hint("TCP:${msg.obj}")
        val reMsg = msg.obj as eTCP.receiveMSG
        when (reMsg.code) {
//                val HANDLER_FAILURE_CODE = -1  //连接失败
            eTCP.eInit.HANDLER_FAILURE_CODE -> mainActivity!!.Hint("TCP客户端-${reMsg.address}：${reMsg.msg}  连接失败")
//                val HANDLER_ERROR_CODE = -2   //连接错误
            eTCP.eInit.HANDLER_ERROR_CODE -> mainActivity!!.Hint("TCP客户端-${reMsg.address}：${reMsg.msg}  连接错误")
//                val HANDLER_CLOSE_CODE = 0     //关闭连接
            eTCP.eInit.HANDLER_CLOSE_CODE -> mainActivity!!.Hint("TCP客户端-${reMsg.address}：${reMsg.msg}  连接关闭")
//                val HANDLER_CONNECT_CODE = 1  //连接成功
            eTCP.eInit.HANDLER_CONNECT_CODE -> mainActivity!!.Hint("TCP客户端-${reMsg.address}：${reMsg.msg}  连接成功")
//                val HANDLER_MSG_CODE = 2    //接收消息
            eTCP.eInit.HANDLER_MSG_CODE -> mainActivity!!.Hint("TCP客户端-${reMsg.address}：接收到 ${reMsg.msg}")

//                val SHANDLER_FAILURE_CODE = -11  //创建失败
            eTCP.eInit.SHANDLER_FAILURE_CODE -> mainActivity!!.Hint("TCP服务端-${reMsg.address}：${reMsg.msg}  创建失败")
//                val SHANDLER_ERROR_CODE = -22   //创建错误
            eTCP.eInit.SHANDLER_ERROR_CODE -> mainActivity!!.Hint("TCP服务端-${reMsg.address}：${reMsg.msg}  创建错误")
//                val SHANDLER_CLOSE_CODE = -33     //连接关闭
            eTCP.eInit.SHANDLER_CLOSE_CODE -> mainActivity!!.Hint("TCP服务端-${reMsg.address}：${reMsg.msg}  连接关闭")
//                val SHANDLER_SUCCEED_CODE = 33     //创建成功
            eTCP.eInit.SHANDLER_SUCCEED_CODE -> mainActivity!!.Hint("TCP服务端-${reMsg.address}：${reMsg.msg}  创建成功")
//                val SHANDLER_CONNECT_CODE = 11  //连接成功
            eTCP.eInit.SHANDLER_CONNECT_CODE -> mainActivity!!.Hint("TCP服务端-${reMsg.address}：${reMsg.msg}  连接成功")
//                val SHANDLER_MSG_CODE = 22    //接收消息
            eTCP.eInit.SHANDLER_MSG_CODE -> mainActivity!!.Hint("TCP服务端-${reMsg.address}：接收到${reMsg.msg}")
        }
    }

    private fun handleWeb(msg: Message) {
        mainActivity?.Hint("Web:${msg.obj}")
    }

    private fun handleUSB(msg: Message) {
        when (msg.what) {
            1 -> {
                mainActivity!!.eShowTip("USB连接")
            }
            0 -> mainActivity!!.eShowTip("USB断开")
        }
    }
}
