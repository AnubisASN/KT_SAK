package com.anubis.module_wakeup

import android.content.Context
import android.os.Handler
import com.anubis.module_wakeup.control.MyRecognizer
import com.anubis.module_wakeup.control.MyWakeup
import com.anubis.module_wakeup.recognization.IStatus
import com.anubis.module_wakeup.recognization.MessageStatusRecogListener
import com.anubis.module_wakeup.recognization.RecogWakeupListener
import com.anubis.module_wakeup.util.Logger
import com.baidu.speech.asr.SpeechConstant

//
//                       _oo0oo_
//                      o8888888o
//                      88" . "88
//                      (| -_- |)
//                      0\  =  /0
//                    ___/`---'\___
//                  .' \\|     |// '.
//                 / \\|||  :  |||// \
//                / _||||| -:- |||||- \
//               |   | \\\  -  /// |   |
//               | \_|  ''\---/''  |_/ |
//               \  .-\__  '-'  ___/-. /
//             ___'. .'  /--.--\  `. .'___
//          ."" '<  `.___\_<|>_/___.' >' "".
//         | | :  `- \`.;`\ _ /`;.`/ - ` : | |
//         \  \ `_.   \_ __\ /__ _/   .-` /  /
//     =====`-.____`.___ \_____/___.-`___.-'=====
//                       `=---='
//
//
//     ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
//
//               佛祖保佑         永无BUG
/**
 * 唤醒后识别 本例可与ActivityWakeUp 及对比作为集成识别代码的参考
 */
object eWakeUp : IStatus {

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
     fun start(context: Context, handler: Handler,AID_AKY_SKY: Array<String> = arrayOf("13612239", "yfXyxUQXxDO7Vcp6h7LtH3RC", "UdKuiwWqIeFlzr3aGUNEutCkA0avXE3o"),params:HashMap<String,Any> = HashMap()): eWakeUp {
        Logger.setHandler(handler)
        // 初始化识别引擎
        val recogListener = MessageStatusRecogListener(handler)
        myRecognizer = MyRecognizer(context, recogListener)
        val listener = RecogWakeupListener(handler)
        myWakeup = MyWakeup(context, listener)
         params[SpeechConstant.WP_WORDS_FILE] = "assets:///WakeUp.bin"
         params[SpeechConstant.APP_ID]=AID_AKY_SKY[0]
         params[SpeechConstant.APP_KEY]=AID_AKY_SKY[1]
         params[SpeechConstant.SECRET]=AID_AKY_SKY[2]
         myWakeup!!.start(params)
        return this@eWakeUp
    }



     fun wakeStop() {
        myRecognizer!!.stop()
    }

     fun wakeDestroy() {
        myRecognizer!!.release()
        myWakeup!!.release()
    }
}
