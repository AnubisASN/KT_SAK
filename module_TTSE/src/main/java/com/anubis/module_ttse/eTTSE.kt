/**
 * 第三方引擎合成模式语音合成封装开发库
 */
package com.anubis.module_ttse

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Environment
import android.speech.tts.TextToSpeech
import android.speech.tts.Voice
import android.view.View
import androidx.annotation.RequiresApi
import com.anubis.kt_extends.*
import com.anubis.module_dialog.eDiaAlert
import com.anubis.module_extends.eRvAdapter
import com.lzy.okgo.OkGo
import com.lzy.okgo.callback.FileCallback
import com.lzy.okgo.model.Progress
import com.lzy.okgo.model.Response
import kotlinx.android.synthetic.main.dia_download.*
import kotlinx.android.synthetic.main.dia_download.view.*
import kotlinx.android.synthetic.main.item_dia.*
import kotlinx.android.synthetic.main.item_dia.view.*
import kotlinx.coroutines.*
import org.jetbrains.anko.onClick
import java.io.File
import java.util.*
import kotlin.collections.ArrayList

/**
 * 说明： 初始化调用
 * @初始化方法：eInit()
 * @param context: Context；上下文
 * @param engine: String? = null；引擎包名
 * @param listener: TextToSpeech.OnInitListener? = null；状态监听
 * @return: eTTSE
 */

@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
class eTTSE private constructor() {
    /*引擎列表*/
    val eEngines: List<TextToSpeech.EngineInfo>?
        get() = try {
            eTtsEngine.engines
        } catch (e: Exception) {
            e.eLogE("eEngines")
            null
        }

    /*可用语言列表 无效API*/
    val eAvailableLanguages: Set<Locale>?
        get() = try {
            eTtsEngine.availableLanguages
        } catch (e: Exception) {
            e.eLogE("eAvailableLanguages")
            null
        }

    /*可用声音列表  无效API*/
    val eVoices: Set<Voice>?
        get() = try {
            eTtsEngine.voices
        } catch (e: Exception) {
            e.eLogE("eVoices")
            null
        }

    /**
     * 说明： 引擎自定义信息
     * @初始化方法：DataEngineInfo
     * @param name:String ; 引擎名
     * @param downLoadUrl: String；资源下载地址
     * @param clz: String；包名/设置类名
     */
    data class DataEngineInfo(var name: String, var downLoadUrl: String, var packName: String, var setClz: String? = null)


    lateinit var eTtsEngine: TextToSpeech

    companion object {
        /*主动检索*/
        fun eSearchEngine(context: Context): ArrayList<String> {
            val arrayList = arrayListOf<String>()
            eDownListEngine?.forEach {
                with(it.packName) {
                    if (eApp.eInit.eIsAppInstall(context, this))
                        arrayList.add(this)
                }
            }
            return arrayList
        }

        val eDownListEngine: ArrayList<DataEngineInfo>? = arrayListOf(
                DataEngineInfo("科大讯飞TTS引擎3.0  推荐", "http://119.23.77.41:8081/info/科大讯飞TTS引擎3.0.apk", "com.iflytek.speechcloud", ".activity.DownloadSpeaker"),
                DataEngineInfo("GoogleTTS引擎", "http://119.23.77.41:8081/info/GoogleTTS引擎.apk", "com.google.android.tts", ".local.voicepack.ui.VoiceDataInstallActivity"),
                DataEngineInfo("度秘TTS引擎", "http://119.23.77.41:8081/info/度秘TTS引擎.apk", "com.baidu.duersdk.opensdk"))
        private lateinit var mContext: Context
        private var mListener: TextToSpeech.OnInitListener? = null
        private var mEngine: String? = null
        fun eInit(context: Context, engine: String? = null, listener: TextToSpeech.OnInitListener? = null): eTTSE {
            mContext = context
            listener?.let { mListener = it }
            engine?.let { mEngine = it }
            return eInit
        }

        private val eInit by lazy(LazyThreadSafetyMode.SYNCHRONIZED) { eTTSE() }
    }

    init {
        eLog("类初始化")
        eInitEngine()
    }


    /**
     * 说明： 默认插件下载弹窗
     * @初始化方法：eDiaDownload
     * @param context: Context；上下文
     * @param style: Int = R.style.dialog；样式风格
     */
    fun eDiaDownload(context: Context = mContext, style: Int = R.style.dialog) {
        val filePath = Environment.getExternalStorageDirectory().path + "/"
        eDiaAlert.eInit(context, style).eDIYShow(R.layout.dia_download) { dialog: Dialog, view: View, idiyCallBackClick: eDiaAlert.IDIYCallBackClick? ->
            view.eTTSE_rv.adapter = eRvAdapter(context, view.eTTSE_rv, R.layout.item_dia, eDownListEngine, { itemView: View, data: DataEngineInfo?, i: Int ->
                data?.let {
                    with(itemView) {
                        if (eApp.eInit.eIsAppInstall(context, data.packName)) {
                            //已安装
                            eTTSE_item_bt.text = "已安装"
                            eTTSE_item_pb.visibility = View.INVISIBLE
                            eTTSE_item_bt.isEnabled = false
                        }
                        eTTSE_item_tvTitle.text = data.name
                        eTTSE_item_bt.tag = data.downLoadUrl
                        eTTSE_item_bt.onClick {
                            val name = "${data.packName}.apk"
                            eTTSE_item_bt.isEnabled = false
                            File(filePath + name).delete()
                            OkGo.post<File>(it?.tag.toString())
                                    .tag(it?.id)
                                    .execute(object : FileCallback(filePath, name) {
                                        override fun onSuccess(response: Response<File>?) {
                                            with(eShell.eInit) {
                                                if (eHaveRoot())
                                                    eExecShell(eShell.install + filePath + name)
                                                else
                                                    eApp.eInit.eInstallApkFile(context, filePath + name)
                                            }
                                            eTTSE_item_bt.text = "安装中"
                                            GlobalScope.launch {
                                                delay(5000)
                                                val result = withTimeoutOrNull(10000) {
                                                    while (!eApp.eInit.eIsAppInstall(context, data.packName)) {
                                                        delay(1000)
                                                    }
                                                    eTTSE_item_pb.post { eTTSE_item_pb.visibility = View.INVISIBLE }
                                                    "已安装"
                                                }
                                                eTTSE_item_bt.post {
                                                    eTTSE_item_bt.text = result ?: "请刷新"
                                                }
                                            }
                                        }

                                        override fun downloadProgress(progress: Progress?) {
                                            super.downloadProgress(progress)
                                            eTTSE_item_pb.progress = ((progress?.fraction
                                                    ?: 0f) * 100).toInt()
                                        }

                                        override fun onError(response: Response<File>?) {
                                            super.onError(response)
                                            if (response?.rawCall?.isCanceled ?: false)
                                                context.eShowTip("已取消")
                                            else
                                                context.eShowTip("网络异常：${response?.code()}")
                                        }
                                    })
                        }
                    }
                }
            })
            dialog.setOnDismissListener {
                OkGo.getInstance().cancelAll()
            }
            view.eTTSE_btClose.onClick {
                dialog.dismiss()
            }
        }
    }


    /**
     * 说明： 设置语言  待定API
     * @初始化方法：eSetLanguage
     * @param locale: Locale = Locale.getDefault() ；语言
     * @return: Boolean
     */
    fun eSetLanguage(locale: Locale = Locale.getDefault()): Boolean {
        val supported = eTtsEngine.setLanguage(locale)
        return if (supported == TextToSpeech.LANG_AVAILABLE) {
//            eLog("语言：${locale.displayName}支持")
            false
        } else {
//            eLogE("语言：${locale.displayName}不支持")
            true
        }
    }

    /**
     * 说明： 初始化引擎
     * @初始化方法：eInitEngine
     * @param engine: String?；引擎包名
     * @param listener: TextToSpeech.OnInitListener? = mListener；状态监听
     * @param editBlock: ((TextToSpeech) -> Unit)? = null；扩展块
     * @return: TextToSpeech；引擎实例
     */
    fun eInitEngine(engine: String? = mEngine, listener: TextToSpeech.OnInitListener? = mListener, editBlock: ((TextToSpeech) -> Unit)? = null): TextToSpeech {
        eClean()
        eTtsEngine = TextToSpeech(mContext, listener, engine)
        eSetLanguage()
        mEngine = engine ?: eTtsEngine.defaultEngine
        editBlock?.let { it(eTtsEngine) }
        return eTtsEngine
    }

    /**
     * 说明： 播放
     * @初始化方法：eSpeak
     * @param str: String；语音内容
     * @param queueMode: Int = TextToSpeech.QUEUE_ADD；  QUEUE_ADD-排队,QUEUE_FLUSH-清除加载
     * @return: TextToSpeech；引擎实例
     */
    fun eSpeak(str: String, queueMode: Int = TextToSpeech.QUEUE_ADD)=  eTtsEngine.speak(str, queueMode, null)

    /*暂停*/
    fun eStop() =eTtsEngine.stop()


    /*释放*/
    fun eClean() {
        try {
            eStop()
            eTtsEngine.shutdown()
        } catch (e: Exception) {
        }
    }



    /**
     * 说明： 设置参数
     * @初始化方法：eSetParam
     * @param locale: Locale；语言
     * @param speechRate: Float; 语速  1f
     * @param pitch: Float；语调 1f
     * @param extendBlock: ((TextToSpeech) -> Unit；扩展块
     */
    open fun eSetParam(speechRate: Float? = null, pitch: Float? = null, locale: Locale? = null, extendBlock: ((TextToSpeech) -> Unit)? = null) {
        locale?.let { eSetLanguage(it) }
        speechRate?.let { eTtsEngine.setSpeechRate(it) }
        pitch?.let { eTtsEngine.setPitch(it) }
        extendBlock?.let { it(eTtsEngine) }

    }

    /*跳转至TTS设置*/
    open fun eToTtsSet(activity: Activity, action: String = "com.android.settings.TTS_SETTINGS") {
        val intent = Intent()
        intent.action = action
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        activity.startActivity(intent)
    }

    /*跳转语音数据下载/设置*/
    open fun eToTtsData(activity: Activity) {
        eDownListEngine?.forEach {
            with(it.packName) {
                if (indexOf(mEngine!!) != -1 && it.setClz !== null) {
                    val intent = Intent()
                    intent.setClassName(this, this + it.setClz)
                    activity.startActivity(intent)
                    return
                }
            }
        }
        eToTtsSet(activity)
    }
}
