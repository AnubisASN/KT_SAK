package com.anubis.SwissArmyKnife

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ScrollView
import android.widget.Spinner
import com.alibaba.android.arouter.launcher.ARouter
import com.anubis.SwissArmyKnife.GreenDao.Data
import com.anubis.SwissArmyKnife.R.id.sv_Hint
import com.anubis.SwissArmyKnife.R.id.tv_Hint
import com.anubis.kt_extends.*
import com.anubis.kt_extends.eKeyEvent.eSetKeyDownExit
import com.anubis.kt_extends.eShell.eExecShell
import com.anubis.kt_extends.eTime.eGetCurrentTime
import com.anubis.module_asrw.eASRW
import com.anubis.module_asrw.recognization.IStatus
import com.anubis.module_asrw.recognization.PidBuilder
import com.anubis.module_ewifi.eWiFi
import com.anubis.module_ftp.FsService
import com.anubis.module_ftp.GUI.eFTPUI
import com.anubis.module_greendao.eGreenDao
import com.anubis.module_portMSG.ePortMSG
import com.anubis.module_tts.Bean.TTSMode
import com.anubis.module_tts.Bean.VoiceModel
import com.anubis.module_tts.eTTS
import com.anubis.module_videochat.eVideoChat
import com.anubis.module_vncs.eVNC
import com.anubis.utils.util.eToastUtils
import com.baidu.speech.asr.SpeechConstant
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.list_edit_item.view.*
import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.util.LinkedHashMap
import kotlin.collections.ArrayList
import kotlin.collections.set

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
//     ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
//               佛祖保佑         永无BUG
class MainActivity : Activity() {
    private var APP: app? = null
    private var TTS: eTTS? = null
    private var filePath = ""
    private var file: File? = null
    private var datas: Array<String>? = null
    private var Time: Long = 0
    private var asrw: eASRW? = null
    private var hashMap1 = HashMap<String, Boolean>()
    private var hashMap2 = HashMap<String, Boolean>()
    private val handleMsg = object : Handler() {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            handleMsg(msg)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        ePermissions.eSetPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO))
        app.mActivityList.add(this)
        TTS = eTTS.ttsInit(app().get()!!, Handler(), TTSMode.MIX, VoiceModel.MALE)
        datas = arrayOf("sp_bt切换化发音调用_bt语音唤醒识别_bt语音识别", "et_bt串口通信1", "et_bt串口通信3", "et_bt串口通信4", "et_btString数据保存_btInt数据保存_bt数据读取", "bt后台启动_bt后台杀死_bt吐司改变", "btVNC二进制文件执行", "et_bt数据库插入_bt数据库查询_bt数据库删除", "btCPU架构", "btAecFaceFT人脸跟踪模块（路由转发跳转）_bt活体检测(路由转发)", "bt音视频通话", "bt开启FTP服务_bt关闭FTP服务", "bt系统设置权限检测_bt搜索WIFI", "bt创建WIFI热点0_bt创建WIFI热点_bt关闭WIFI热点", "btAPP重启", "et_btROOT权限检测_btShell执行_bt修改为系统APP", "et_bt正则匹配", "bt清除记录")
        init()

    }

    private fun getDigit(str: String): Int {
        var Digit = 0
        for (data in datas!!) {
            if (data.indexOf(str) != -1) {
                Digit = datas!!.indexOf(data)
            }
        }
        return Digit
    }

    var i = 0
    private fun init() {
        filePath = this.filesDir.path + "SAK_Record.txt"
        file = File(filePath)
        if (file!!.exists()) {
            Handler().post {
                val buf = BufferedReader(FileReader(filePath))
                Hint(buf.readText())
            }
        } else {
            file!!.createNewFile()
        }
        rvList.layoutManager = LinearLayoutManager(this)
        val voiceModel = arrayOf(VoiceModel.FEMALE, VoiceModel.MALE, VoiceModel.EMOTIONAL_MALE, VoiceModel.CHILDREN)
        var spID = 0
        val adapter = ArrayAdapter<VoiceModel>(this@MainActivity, android.R.layout.simple_spinner_item, voiceModel)
        val callback = object : ICallBack {
            override fun CallResult(view: View?, itmeID: Int, MSG: String, spinner: Spinner) {
                when (itmeID) {
                    getDigit("初始化发音") -> {
                        spinner.adapter = adapter
                        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                            override fun onNothingSelected(parent: AdapterView<*>?) {
                            }

                            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                                spID = position
                            }
                        }
                        when (view?.id) {

                            R.id.bt_item1 -> {
                                TTS = TTS!!.setParams(voiceModel[spID])
                                Handler().postDelayed({
                                    val state = TTS!!.speak("发音人切换发音调用")
                                    Hint("发音人切换发音调用:$state")
                                }, 800)

                            }
                            R.id.bt_item2 -> {
                                asrw = eASRW.start(this@MainActivity, handleMsg)
                                Hint("语音唤醒激活")
                            }
                            R.id.bt_item3 -> {
                                eASRW.ASR(this@MainActivity, handleMsg)
                                Hint("语音识别启动")
                            }
                        }
                    }
                    getDigit("VNC") -> when (view?.id) {
                        R.id.bt_item1 -> Hint("VNC二进制文件执行:${eVNC.startVNCs(this@MainActivity)}")
                    }
                    getDigit("音视频") ->
                        ARouter.getInstance().build("/module_videochat/eVideoChat")
//                                .withBundle("init1",b1)
                                .withString("init1", "00")
//                                .withBundle("init2",b2)
                                .navigation()

                    getDigit("CPU") -> when (view?.id) {
                        R.id.bt_item1 -> Hint("CPU架构:${android.os.Build.CPU_ABI}")
                    }
                    getDigit("FTP") -> when (view?.id) {
                        R.id.bt_item1 -> Hint("FTP服务启动:${startActivity(Intent(this@MainActivity, eFTPUI::class.java))}")
                        R.id.bt_item2 -> Hint("FTP服务关闭:${sendBroadcast(Intent(FsService.ACTION_STOP_FTPSERVER))}")
                    }
                    getDigit("后台杀死") -> when (view?.id) {
                        R.id.bt_item1 -> {
                            Hint("后台服务状态：${eApp.eIsServiceRunning(this@MainActivity, MyService::class.java.name)}")
                            Hint("后台启动状态：${startService(Intent(this@MainActivity, MyService::class.java))}")
                        }
                        R.id.bt_item2 -> Hint("后台杀死状态：${eApp.eKillBackgroundProcesses(this@MainActivity, MyService::class.java.name)}")
                        R.id.bt_item3 -> {
                            eToastUtils.setMsgColor(Color.GREEN)
                            eToastUtils.showShort("Toast测试")
                        }
                    }
                    getDigit("APP重启") -> Hint("APP重启:${eApp.eAppRestart(this@MainActivity)}")
                    getDigit("串口通信1") -> Hint("串口通讯状态：" + ePortMSG.MSG(this@MainActivity, if (MSG.isEmpty()) "0" else MSG, "/dev/ttyS1"))
                    getDigit("串口通信3") -> Hint("串口通讯状态：" + ePortMSG.MSG(this@MainActivity, if (MSG.isEmpty()) "0" else MSG, "/dev/ttyS3"))
                    getDigit("串口通信4") -> Hint("串口通讯状态：" + ePortMSG.MSG(this@MainActivity, if (MSG.isEmpty()) "0" else MSG, "/dev/ttyS4"))
//                        Hint("串口通讯状态：" + ePortMSG().getInit(this@MainActivity, MSG ?: "").MSG())
                    getDigit("数据库") -> when (view?.id) {
                            R.id.bt_item1 -> Hint("数据库插入：${i++}--${eGreenDao(this@MainActivity).insertUser(Data(i++.toString(), MSG))}")
                            R.id.bt_item2 -> Hint("数据库查询:" + if (MSG.isEmpty()) {
                                var info = ""
                                for (data in eGreenDao(this@MainActivity).queryAllUser(Data())) {
                                    info += (data as Data).name + "--"
                                }
                                info
                            } else {
                                var info = ""
                                val msg=MSG.split("-")
                                for (data in eGreenDao(this@MainActivity).queryUserByNativeSql(Data(), " where name>? and name<?", arrayOf(msg[0],msg[1]))) {
                                    info += (data as Data).name + "--"
                                }
                                info
                            })

                    R.id.bt_item3 -> Hint("数据库删除：${eGreenDao(this@MainActivity).deleteAll(Data("", ""))}")
                }

                getDigit("系统设置权限检测") -> when (view?.id) {
                    R.id.bt_item1 -> if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    Hint("系统设置权限检测：${ePermissions.eSetSystemPermissions(this@MainActivity)}")
                } else {
                    Hint("安卓版本低于6.0--${Build.VERSION.SDK_INT}")
                }
                    R.id.bt_item2 -> {
                    Hint("搜索WIFI:")
                    for (wifi in eWiFi.eGetScanWiFi(this@MainActivity)!!) {
                        Hint("SSID:${wifi.SSID}")
                    }
                }
                }
                getDigit("热点") -> when (view?.id) {
                    R.id.bt_item1 -> Hint("创建WIFI热点0:${eWiFi.eCreateWifiHotspot(this@MainActivity)}")
                    R.id.bt_item2 -> Hint("创建WIFI热点:${eWiFi.eCreateWifiHotspot(this@MainActivity)}")
                    R.id.bt_item3 -> Hint("关闭WIFI热点：${eWiFi.eCloseWifiHotspot(this@MainActivity)}")
                }
                getDigit("AecFaceFT人脸跟踪模块（路由转发跳转）") -> when(view?.id){
                    R.id.bt_item1->ARouter.getInstance().build("/face/arcFaceFT").navigation()
                    R.id.bt_item2->startActivity(Intent(this@MainActivity, Face0::class.java))
                }
                getDigit("Shell执行") -> when (view?.id) {
                    R.id.bt_item1 -> Hint("ROOT权限检测:${eShell.eHaveRoot()}")
                    R.id.bt_item2 -> Hint("Shell执行：${eShell.eExecShell(MSG)}")
                    R.id.bt_item3 -> {
                    if (MSG.isNotEmpty()) {
                        val shell = "cp -r /datas/app/$MSG* /system/priv*"
                        Hint("自定义修改为系统APP:" + eExecShell(shell))
                        Hint("执行Shell:$shell")
                    } else {
                        var shell = " cp -r /datas/app/$packageName* /system/priv*"
                        Hint("修改为系统APP:" + eExecShell(shell))
                        Hint("执行Shell:$shell")
                        if (File("/datas/app-lib/$packageName-1").exists()) {
                            shell = "mv /datas/app-lib/$packageName*/ /system/lib/"
                            Hint("文件夹存在，修改lib数据:" + eExecShell(shell))
                            Hint("执行Shell:$shell")
                        }
                    }
                    Handler().postDelayed({
                        val shell = "chmod -R 755   /system/priv*/$MSG*"
                        Hint("修改文件权限:" + eExecShell(shell))
                        Hint("执行Shell:$shell")
                    }, 2000)
                    Handler().postDelayed({
                        val shell = " rm -rf /datas/app/$MSG*"
                        Hint("删除数据遗留:" + eExecShell(shell))
                        Hint("执行Shell:$shell")
                        eShowTip("请重启设备")
                    }, 3500)
                }
                }
                getDigit("数据保存") -> when (view?.id) {
                    R.id.bt_item1 -> Hint("Stirng数据保存：${eSetSystemSharedPreferences("test", MSG.toString())}")
                    R.id.bt_item2 -> Hint("Int数据保存：${eSetSystemSharedPreferences("test", MSG.toInt())}")
                    R.id.bt_item3 -> Hint("数据读取：${eGetSystemSharedPreferences("test", MSG)}")
                }
                getDigit("正则匹配") -> Hint("正则匹配：/datas/app/$packageName$MSG")
                getDigit("清除记录") -> {
                    if (System.currentTimeMillis() - Time > 1000) {
                        Time = System.currentTimeMillis()
                    } else {
                        tv_Hint.text = ""
                        file!!.writeText("")
                        eShowTip("记录已清除")
                    }
                }
            }
        }
    }

    val myAdapter = MyAdapter(this, datas!!, callback)
    rvList.adapter = myAdapter
    rvList.setItemViewCacheSize(datas!!.size)
    eExecShell("mount -o remount,rw rootfs /system/ ")
}

//    0 唤醒成功         3    引擎就绪 开始说话            4 监测到说话      9001  监测到结束说话        5  临时识别      6  识别结束        2 识别引擎空闲
//    arg1 类型   arg2 最终状态   what  引擎状态   obj String消息
private val backTrackInMs = 2000
private val MSG_TYPE_WUR = 11
private val MSG_TYPE_ASR = 22
private val MSG_TYPE_TTS = 33
private val MSG_STATE_TTS_SPEAK_OVER = 0
private val MSG_STATE_TTS_SPEAK_START = 1
private fun handleMsg(msg: Message) {
    if (msg.what == IStatus.STATUS_WAKEUP_SUCCESS) {
        Hint("语音唤醒成功:--arg1:${msg.arg1}--arg2:${msg.arg2}--what:${msg.what}--obj:${msg.obj}")
        eLog("语音唤醒成功:--arg1:${msg.arg1}--arg2:${msg.arg2}--what:${msg.what}--obj:${msg.obj}")
        eShowTip("语音唤醒成功")
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
            Hint("唤醒成功:--arg1:${msg.arg1}--arg2:${msg.arg2}--what:${msg.what}--obj:${msg.obj}")
            eLog("唤醒成功:--arg1:${msg.arg1}--arg2:${msg.arg2}--what:${msg.what}--obj:${msg.obj}")
        }
        IStatus.STATUS_NONE -> {
//                识别引擎空闲
            Hint("识别引擎空闲:--arg1:${msg.arg1}--arg2:${msg.arg2}--what:${msg.what}--obj:${msg.obj}")
            eLog("识别引擎空闲:--arg1:${msg.arg1}--arg2:${msg.arg2}--what:${msg.what}--obj:${msg.obj}")
        }
        IStatus.STATUS_READY -> {
//                引擎就绪 开始说话
            Hint("引擎就绪 开始说话:--arg1:${msg.arg1}--arg2:${msg.arg2}--what:${msg.what}--obj:${msg.obj}")
            eLog("引擎就绪 开始说话:--arg1:${msg.arg1}--arg2:${msg.arg2}--what:${msg.what}--obj:${msg.obj}")
        }
        IStatus.STATUS_SPEAKING -> {
//                监测到说话
            Hint("监测到说话:--arg1:${msg.arg1}--arg2:${msg.arg2}--what:${msg.what}--obj:${msg.obj}")
            eLog("监测到说话:--arg1:${msg.arg1}--arg2:${msg.arg2}--what:${msg.what}--obj:${msg.obj}")
        }
        IStatus.STATUS_RECOGNITION -> {
//                临时识别
            Hint("临时识别:--arg1:${msg.arg1}--arg2:${msg.arg2}--what:${msg.what}--obj:${msg.obj}")
            eLog("临时识别:--arg1:${msg.arg1}--arg2:${msg.arg2}--what:${msg.what}--obj:${msg.obj}")
        }
        IStatus.STATUS_FINISHED -> {//识别结束
            eLog("识别结束:--arg1:${msg.arg1}--arg2:${msg.arg2}--what:${msg.what}--obj:${msg.obj}")
            eLog("识别结束:--arg1:${msg.arg1}--arg2:${msg.arg2}--what:${msg.what}--obj:${msg.obj}")
            if (msg.arg2 == 1) {
                Hint("最终识别：" + msg.obj.toString())
                eLog("最终识别：" + msg.obj.toString())
                eShowTip("最终识别：" + msg.obj.toString())
            }

        }

    }
}

private fun Hint(str: String) {
    val Str = "${eGetCurrentTime("MM-dd HH:mm:ss")}： $str\n\n\n"
    eLog(Str, "SAK")
    tv_Hint.append(Str)
    sv_Hint.fullScroll(ScrollView.FOCUS_DOWN)
}

class MyAdapter(val mContext: Context, val mDatas: Array<String>, val mCallbacks: ICallBack) : RecyclerView.Adapter<MyAdapter.MyHolder>() {
    var mPosition: Int? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyHolder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.list_edit_item, parent, false)
        return MyHolder(view)
    }

    override fun getItemCount(): Int {
        return mDatas.size
    }

    //方法执行
    override fun onBindViewHolder(holder: MyHolder, position: Int) {
        mPosition = position
        holder.setData(mDatas[position], position)
        holder.itemView.bt_item1.setOnClickListener {
            val editContext = holder.itemView.et_item1.text.toString()
            mCallbacks.CallResult(it, position, editContext, holder.itemView.sp_item1)
        }
        holder.itemView.bt_item2.setOnClickListener {
            val editContext = holder.itemView.et_item1.text.toString()
            mCallbacks.CallResult(it, position, editContext, holder.itemView.sp_item1)
        }
        holder.itemView.bt_item3.setOnClickListener {
            val editContext = holder.itemView.et_item1.text.toString()
            mCallbacks.CallResult(it, position, editContext, holder.itemView.sp_item1)
        }

    }

    //界面设置 ed_    sp_  bt_x3
    inner class MyHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun setData(datas: String, position: Int) {
            try {
                var datas = datas.split("_")
//                    datas=datas.reversed()
                var btList = ArrayList<String>()
                for (str in datas) {
                    eLog("split:$str")
                    when (str.substring(0, 2)) {
                        "et" -> itemView.et_item1.visibility = View.VISIBLE
                        "sp" -> {
                            mCallbacks.CallResult(null, position, "", itemView.sp_item1)
                            itemView.sp_item1.visibility = View.VISIBLE
                        }
                        "bt" -> btList.add(str)
                    }
                }
                //启动并设置button
                for (str in btList) {
                    when (btList.indexOf(str)) {
                        0 -> {
                            itemView.bt_item1.visibility = View.VISIBLE
                            itemView.bt_item1.text = eString.eGetNumberPeriod(str, 2, "MAX")
                        }
                        1 -> {
                            itemView.bt_item2.visibility = View.VISIBLE
                            itemView.bt_item2.text = eString.eGetNumberPeriod(str, 2, "MAX")
                        }
                        2 -> {
                            itemView.bt_item3.visibility = View.VISIBLE
                            itemView.bt_item3.text = eString.eGetNumberPeriod(str, 2, "MAX")
                        }
                    }
                }
            } catch (e: Exception) {
                if (datas.substring(0, 2) == "bt") {
                    itemView.bt_item1.visibility = View.VISIBLE
                    itemView.bt_item1.text = eString.eGetNumberPeriod(datas, 2, "MAX")
                }
            }
        }
    }
}


override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
    ePermissions.eSetOnRequestPermissionsResult(this, requestCode, permissions, grantResults)
    if (requestCode != 1) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
}


override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
    Hint("keyCode:$keyCode")
    eLog("size" + app.mAPP.mActivityList!!.size)
    return eSetKeyDownExit(this, keyCode, app.mAPP.mActivityList!!, false, exitHint = "完成退出")
}

override fun onDestroy() {
    if (tv_Hint.text.isNotEmpty()) {
        file!!.writeText(tv_Hint.text.toString())
    }
    super.onDestroy()
}

interface ICallBack {
    fun CallResult(view: View?, numID: Int, MSG: String, spinner: Spinner)
}
}



