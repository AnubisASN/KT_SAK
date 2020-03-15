package com.anubis.SwissArmyKnife

import android.Manifest
import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.Handler
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
import com.android.xhapimanager.XHApiManager
import com.anubis.SwissArmyKnife.APP.Companion.mAPP
import com.anubis.SwissArmyKnife.GreenDao.Data
import com.anubis.SwissArmyKnife.ParameHandleMSG.handleMsg
import com.anubis.SwissArmyKnife.ParameHandleMSG.handleTCP
import com.anubis.SwissArmyKnife.ParameHandleMSG.handleTTS
import com.anubis.SwissArmyKnife.ParameHandleMSG.uHandler
import com.anubis.SwissArmyKnife.R.id.sv_Hint
import com.anubis.SwissArmyKnife.R.id.tv_Hint
import com.anubis.kt_extends.*
import com.anubis.kt_extends.eKeyEvent.eSetKeyDownExit
import com.anubis.kt_extends.eShell.eExecShell
import com.anubis.kt_extends.eTime.eGetCurrentTime
import com.anubis.module_asrw.eASRW
import com.anubis.module_cardotg.eCardOTG
import com.anubis.module_ewifi.eWiFi
import com.anubis.module_ftp.FsService
import com.anubis.module_ftp.GUI.eFTPUIs
import com.anubis.module_greendao.eGreenDao
import com.anubis.module_portMSG.ePortMSG
import com.anubis.module_qrcode.eQRCode
import com.anubis.module_tcp.eTCP
import com.anubis.module_tcp.eTCP.eClientHashMap
import com.anubis.module_tcp.eTCP.eServerHashMap
import com.anubis.module_tts.Bean.TTSMode
import com.anubis.module_tts.Bean.VoiceModel
import com.anubis.module_tts.eTTS
import com.anubis.module_tts.listener.FileSaveListener
import com.anubis.module_usbdevice.eUDevice
import com.anubis.module_videochat.eVideoChatUI
import com.anubis.module_vncs.eVNC
import com.anubis.utils.util.eToastUtils
import com.lzy.okgo.OkGo
import com.lzy.okgo.callback.StringCallback
import com.lzy.okgo.model.Response
import com.lzy.okgo.utils.IOUtils.toByteArray
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.list_edit_item.view.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.jetbrains.anko.custom.async
import org.jetbrains.anko.custom.onUiThread
import org.jetbrains.anko.uiThread
import org.json.JSONException
import java.io.*
import java.net.ServerSocket
import java.net.Socket
import java.net.URLDecoder
import kotlin.collections.ArrayList
import kotlin.concurrent.thread

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
    private var TTS: eTTS? = null
    private var filePath = ""
    private var file: File? = null
    private var datas: Array<String>? = null
    private var Time: Long = 0
    var XHA: XHApiManager? = null
    private var hashMap1 = HashMap<String, Boolean>()
    private var hashMap2 = HashMap<String, Boolean>()
    var progressDialog: ProgressDialog? = null

    companion object {
        var mainActivity: MainActivity? = null
        var mHandler:Handler?=null
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mainActivity = this@MainActivity
        mHandler= Handler()
        ParameHandleMSG.mainActivity = mainActivity
        ePermissions.eSetPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO))
        APP.mActivityList.add(this)
        TTS = eTTS.ttsInit(APP.mAPP, handleTTS, TTSMode.MIX, VoiceModel.MALE, listener = FileSaveListener(handleTTS, "/sdcard/img/info"))
        datas = arrayOf("sp_bt切换化发音调用_bt语音唤醒识别_bt语音识别", "et_bt语音合成_bt播放", "et_btSTRING_btInt_btBoolean", "et_btFloat_bt获取", "bt身份证阅读器", "bt加载弹窗", "et_bt串口通信r_bt监听串口_bt关闭串口", "btHTTP测试_btHTTP循环测试", "bt后台启动_bt后台杀死_bt吐司改变", "et_bt二维码生成", "btLogCat", "btVNC二进制文件执行", "bt数据库插入_bt数据库查询_bt数据库删除", "btCPU架构", "et_btTCP连接C_bt数据发送_btTCP创建", "et_btTCP连接C关闭_btTCP连接S关闭_btTCP服务关闭", "btAecFaceFT人脸跟踪模块_bt活体跟踪检测（路由转发跳转）", "et_bt音视频通话", "et_btGPIO读取", "bt开启FTP服务_bt关闭FTP服务", "bt系统设置权限检测_bt搜索WIFI", "bt创建WIFI热点0_bt创建WIFI热点_bt关闭WIFI热点", "btAPP重启", "et_btROOT权限检测_btShell执行_bt修改为系统APP", "et_bt正则匹配", "bt清除记录")
            init()
        if (Build.MODEL == "ZK-R32A")
            XHA = XHApiManager()
        eUDevice.init(mAPP, uHandler)
    }


    /**
     * -----------------------------------------测试控制模块——————————————————————————————————————————
     */

    var i = 1

    private fun init() {
        filePath = "/sdcard/SAK_Record.txt"
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
            override fun CallResult(view: View?, itmeID: Int, MSG: String?, spinner: Spinner) {
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
                                ParameHandleMSG.asrw = eASRW.start(this@MainActivity, handleMsg)
                                Hint("语音唤醒激活")
                            }
                            R.id.bt_item3 -> {
                                eASRW.ASR(this@MainActivity, handleMsg)
                                Hint("语音识别启动")
                            }
                        }
                    }
                    getDigit("二维码") -> when (view?.id) {
                        R.id.bt_item1 -> {
                            iv_Hint.setImageBitmap(eQRCode.createQRCode(MSG ?: "请输入内容"))
                            iv_Hint.visibility = View.VISIBLE
                            Handler().postDelayed({ iv_Hint.visibility = View.GONE }, 5000)
                        }
                    }
                    getDigit("语音合成") -> when (view?.id) {
                        R.id.bt_item1 -> Hint("语音合成：${TTS!!.synthesize(MSG ?: "语音合成", "0")}")
                        R.id.bt_item2 -> Hint("语音播放：${ePlayPCM("/sdcard/img/info/output-${"0"}.pcm")}")
                    }
                    getDigit("身份证阅读器") -> {
                        eCardOTG.otgInit(mAPP, handleMsg)
                    }
                    getDigit("LogCat") ->
                        async {
                            eShell.eExecShell("logcat  *:e -v time   -s AndroidRuntime ${this@MainActivity.packageName}  ${android.os.Process.myPid()} -d > /mnt/sdcard/log.txt "
                            )
                        }
                    getDigit("TCP创建") -> when (view?.id) {
                        R.id.bt_item1 -> {
                            GlobalScope.launch {
                                eTCP.eSocketConnect(MSG?.split("-")?.get(0)
                                        ?: "192.168.1.110", MSG?.split("-")?.get(1)?.toInt()
                                        ?: 3335, handleTCP)
                            }
                        }
                        R.id.bt_item2 -> Hint("数据发送:${eTCP.eSocketSend(MSG?.split("-")?.get(0)
                                ?: "123", MSG?.split("-")?.get(1)
                                ?: "192.168.1.110", eTCP.eClientHashMap)}")
                        R.id.bt_item3 -> {
                            GlobalScope.launch {
                             eTCP.eServerSocket( handleTCP,MSG?.toInt()?:3335)
                        }
                        }
                    }
                    getDigit("服务关闭") -> when (view?.id) {
                        R.id.bt_item1 -> {
                            Hint("TCP连接C关闭:${eTCP.eCloseReceives(eClientHashMap, MSG)} ")
                        }
                        R.id.bt_item2 ->    Hint("TCP连接S关闭:${eTCP.eCloseReceives(eServerHashMap, MSG)} ")
                        R.id.bt_item3 ->   Hint("TCP服务关闭 ："+ eTCP.eCloseServer())
                    }


                    getDigit("GPIO") -> when (view?.id) {
                        R.id.bt_item1 -> Hint("GPIO读取：${XHA!!.XHReadGpioValue(MSG?.toInt() ?: 0)}")
                    }
                    getDigit("STRING") -> when (view?.id) {
                        R.id.bt_item1 -> Hint("String保存：${eSetSystemSharedPreferences("string", MSG
                                ?: "String")}")
                        R.id.bt_item2 -> Hint("Int保存：${eSetSystemSharedPreferences("int", MSG
                                ?: 123)}")
                        R.id.bt_item3 -> Hint("Boolean保存：${eSetSystemSharedPreferences("boolean", MSG == "true")}")
                    }
                    getDigit("HTTP测试") -> when (view?.id) {
                        R.id.bt_item1 -> {
//                            if (eNetWork.eIsNetworkOnline().apply { eLog("HTTP测试网络检测：$this") }) {
//                                OkGo.post<String>("http://119.23.77.41:8082/DataRelay/receiveData")
                            OkGo.post<String>("http://www.hbzayun.com/ACSystem/testPost")
                                    .tag(this)
                                    .execute(object : StringCallback() {
                                        override fun onSuccess(response: Response<String>?) {
                                            Hint("HTTP测试：成功---${response?.body()}")
                                        }

                                        override fun onError(response: Response<String>?) {
                                            super.onError(response)
                                            Hint("HTTP测试：失败---${response?.body()}")
                                        }
                                    })
//                            }
                        }
                        R.id.bt_item2 -> {
                            async {
                                while (true) {
//                                    if (eNetWork.eIsNetworkOnline().apply { eLog("HTTP测试网络检测：$this") }) {
//                                        OkGo.post<String>("http://119.23.77.41:8082/DataRelay/receiveData")
                                    OkGo.post<String>("http://www.hbzayun.com/ACSystem/testPost")
                                            .tag(this)
                                            .execute(object : StringCallback() {
                                                override fun onSuccess(response: Response<String>?) {
                                                    Hint("HTTP测试：成功---${response?.body()}")
                                                }

                                                override fun onError(response: Response<String>?) {
                                                    super.onError(response)
                                                    Hint("HTTP测试：失败---${response?.body()}")
                                                }
                                            })
                                }
                            }
//                            }
                        }
                    }
                    getDigit("获取") -> when (view?.id) {
                        R.id.bt_item1 -> Hint("Float保存：${eSetSystemSharedPreferences("float", MSG?.toFloat()
                                ?: 123f)}")
                        R.id.bt_item2 -> Hint("获取-String:${eGetSystemSharedPreferences("string", "string")
                                ?: "null"}-Int:${eGetSystemSharedPreferences("int", 123)
                                ?: 0}-Boolean:${eGetSystemSharedPreferences("boolean", true)
                                ?: true}-Float:${eGetSystemSharedPreferences("float", 0f) ?: 0f}}")
                    }

                    getDigit("VNC") -> when (view?.id) {
                        R.id.bt_item1 -> Hint("VNC二进制文件执行:${if (eVNC.startVNCs(this@MainActivity)) "成功：5901" else "失败"}")
                    }
                    getDigit("音视频") -> {
                        val intent = Intent(this@MainActivity, eVideoChatUI::class.java)
                        intent.putExtra("channelName", MSG)
                        startActivity(intent)
                    }
//                        ARouter.getInstance().build("/module_videochat/eVideoChat")
////                                .withBundle("init1",b1)
//                                .withString("init1", "00")
////                                .withBundle("init2",b2)
//                                .navigation()

                    getDigit("CPU") -> when (view?.id) {
                        R.id.bt_item1 -> Hint("CPU架构:${android.os.Build.CPU_ABI}")
                    }
                    getDigit("FTP") -> when (view?.id) {
                        R.id.bt_item1 -> Hint("FTP服务启动:${startActivity(Intent(this@MainActivity, eFTPUIs::class.java))}")
                        R.id.bt_item2 -> Hint("FTP服务关闭:${sendBroadcast(Intent(FsService.ACTION_STOP_FTPSERVER))}")
                    }
                    getDigit("加载弹窗") -> {
                        progressDialog = ProgressDialog(this@MainActivity)
                        progressDialog!!.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL)
                        // 设置ProgressDialog 标题
                        progressDialog!!.setTitle("下载提示")
                        // 设置ProgressDialog 提示信息
                        progressDialog!!.setMessage("当前下载进度:")
                        // 设置ProgressDialog 是否可以按退回按键取消
                        progressDialog!!.setCancelable(false)
                        progressDialog!!.show()
                        progressDialog!!.max = 5000
                        progressDialog!!.progress = 0
//                        for (i in 0..5000){
                        async {
                            for (i in 0..5000) {
                                mHandler?.post { progressDialog!!.incrementProgressBy(1) }
//                                handleMsg.sendMessage(msg)
                                eLog("i$i")
                            }
                            uiThread { progressDialog?.dismiss() }
                        }
//                            progressDialog.secondaryProgress

//                        }

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
                    getDigit("APP重启") -> Hint("APP重启:${eApp.eAppRestart(APP.mAPP, this@MainActivity)}")
                    getDigit("串口通信") -> {
                        val msg = "A55501FB"
                        when (view?.id) {
                            R.id.bt_item1 -> Hint("串口数据发送：" + ePortMSG.sendMSG(this@MainActivity, msg, "/dev/ttyS1", 115200, object : ePortMSG.ICallBack {
                                override fun IonLockerDataReceived(buffer: ByteArray, size: Int, path: String) {
                                    Hint("串口数据接收:${eString.eGetByteArrToHexStr(buffer)}--$path")
                                }
                            }))
                            R.id.bt_item2 -> Hint("串口监听：" + ePortMSG.getMSG(this@MainActivity, callback = object : ePortMSG.ICallBack {
                                override fun IonLockerDataReceived(buffer: ByteArray, size: Int, path: String) {
                                    Hint("串口接收:${eString.eGetByteArrToHexStr(buffer)}--$path")
                                }
                            }, mPATH = MSG?.split("-")?.get(0)
                                    ?: "/dev/ttyS3", BAUDRATE = MSG?.split("-")?.get(1)?.toInt()
                                    ?: 9600))
                            R.id.bt_item3 -> Hint("串口关闭：" + ePortMSG.closeMSG())
                        }
                    }

                    getDigit("数据库") -> when (view?.id) {
                        R.id.bt_item1 -> Hint("数据库插入：${eGreenDao(this@MainActivity).insertUser(Data("00000", "11111"))}")
                        R.id.bt_item2 -> Hint("数据库查询:" + eGreenDao(this@MainActivity).queryAllUser(Data()).size)
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
                    getDigit("路由转发跳转") ->
                        when (view?.id) {
                            R.id.bt_item1 -> ARouter.getInstance().build("/face/ArcFaceFT").navigation()
                            R.id.bt_item2 -> ARouter.getInstance().build("/face/ArcFace").navigation()
                        }

                    getDigit("Shell执行") -> when (view?.id) {
                        R.id.bt_item1 -> Hint("ROOT权限检测:${eShell.eHaveRoot()}")
                        R.id.bt_item2 -> Hint("Shell执行：${eShell.eExecShell(MSG ?: "date")}")
                        R.id.bt_item3 -> {
                            if (MSG?.isNotEmpty() == true) {
                                val shell = "cp -r /datas/APP/$MSG* /system/priv*"
                                Hint("自定义修改为系统APP:" + eExecShell(shell))
                                Hint("执行Shell:$shell")
                            } else {
                                var shell = " cp -r /datas/APP/$packageName* /system/priv*"
                                Hint("修改为系统APP:" + eExecShell(shell))
                                Hint("执行Shell:$shell")
                                if (File("/datas/APP-lib/$packageName-1").exists()) {
                                    shell = "mv /datas/APP-lib/$packageName*/ /system/lib/"
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
                                val shell = " rm -rf /datas/APP/$MSG*"
                                Hint("删除数据遗留:" + eExecShell(shell))
                                Hint("执行Shell:$shell")
                                eShowTip("请重启设备")
                            }, 3500)
                        }
                    }
                    getDigit("正则匹配") -> Hint("正则匹配：/datas/APP/$packageName$MSG")
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


    /**
     * -----------------------------------------业务控制模块——————————————————————————————————————————
     */
    fun LoadingData() {
//        try {
        eLog("数量：${eUDevice.deviceCount}")
        @Suppress("MISSING_DEPENDENCY_CLASS")
        val device = eUDevice.getUsbMassDevice(0)
        @Suppress("MISSING_DEPENDENCY_CLASS")
        val rootF = eUDevice.getUsbFiles(eUDevice.readDevice(device!!)!!)
//        @Suppress("MISSING_DEPENDENCY_CLASS")
//        for (root in rootF)
//            eLog("root----:${root.name}")

    }

    /**
     * -----------------------------------------测试组件模块——————————————————————————————————————————
     */
    fun Hint(str: String) {
        tv_Hint.post {
            val Str = "${eGetCurrentTime("MM-dd HH:mm:ss")}： $str\n\n\n"
            eLog(Str, "SAK")
            tv_Hint.append(Str)
            sv_Hint.fullScroll(ScrollView.FOCUS_DOWN)
        }
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
                try {
                    var editContext: String? = holder.itemView.et_item1.text.toString()
                    editContext = if (editContext?.isEmpty() != false) null else editContext
                    mCallbacks.CallResult(it, position, editContext, holder.itemView.sp_item1)
                } catch (e: Exception) {
                    mainActivity?.Hint("数据操作错误-:$e")                }
            }
            holder.itemView.bt_item2.setOnClickListener {
                try {
                    var editContext: String?  = holder.itemView.et_item1.text.toString()
                    editContext = if (editContext?.isEmpty() != false) null else editContext
                    mCallbacks.CallResult(it, position, editContext, holder.itemView.sp_item1)
                } catch (e: Exception) {
                    mainActivity?.Hint("数据操作错误:$e")                }
            }
            holder.itemView.bt_item3.setOnClickListener {
                try {
                    var editContext: String?  = holder.itemView.et_item1.text.toString()
                    editContext = if (editContext?.isEmpty() != false) null else editContext
                    mCallbacks.CallResult(it, position, editContext, holder.itemView.sp_item1)
                } catch (e: Exception) {
                    mainActivity?.Hint("数据操作错误:$e")                }
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
                                mCallbacks.CallResult(null, position, null, itemView.sp_item1)
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

    /**
     * -----------------------------------------周期控制模块——————————————————————————————————————————
     */


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        ePermissions.eSetOnRequestPermissionsResult(this, requestCode, permissions, grantResults)
        if (requestCode != 1) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }


    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        Hint("keyCode:$keyCode")
        eLog("size" + APP.mAPP.mActivityList?.size)
        return eSetKeyDownExit(this, keyCode, APP.mAPP.mActivityList, false, exitHint = "完成退出")
    }

    override fun onDestroy() {
        if (tv_Hint.text.isNotEmpty()) {
            file!!.writeText(tv_Hint.text.toString())
            eLog("记录保存")
        }
        super.onDestroy()
    }

    /**
     * -----------------------------------------回调模块——————————————————————————————————————————
     */


    interface ICallBack {
        fun CallResult(view: View?, numID: Int, MSG: String?, spinner: Spinner)
    }

}



