package com.anubis.SwissArmyKnife

import android.Manifest
import android.app.Dialog
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import androidx.recyclerview.widget.LinearLayoutManager
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.alibaba.android.arouter.launcher.ARouter
import com.android.xhapimanager.XHApiManager
import com.anubis.SwissArmyKnife.APP.Companion.mAPP
import com.anubis.SwissArmyKnife.GreenDao.Data
import com.anubis.SwissArmyKnife.ParameHandleMSG.handleMsg
import com.anubis.SwissArmyKnife.ParameHandleMSG.handleTTS
import com.anubis.SwissArmyKnife.ParameHandleMSG.handleWeb
import com.anubis.SwissArmyKnife.ParameHandleMSG.uHandler
import com.anubis.kt_extends.*
import com.anubis.module_asrw.eASRW
import com.anubis.module_cardotg.eCardOTG
import com.anubis.module_dialog.View.eArrowDownloadButton
import com.anubis.module_dialog.eDiaAlert
import com.anubis.module_ewifi.eWiFi
import com.anubis.module_ftp.FsService
import com.anubis.module_ftp.GUI.eFTPUIs
import com.anubis.module_greendao.eGreenDao
import com.anubis.module_office.eOffice
import com.anubis.module_portMSG.ePortMSG
import com.anubis.module_qrcode.eQRCodeCreate
import com.anubis.module_tcp.eTCP
import com.anubis.module_tts.Bean.TTSMode
import com.anubis.module_tts.Bean.VoiceModel
import com.anubis.module_tts.eTTS
import com.anubis.module_tts.listener.FileSaveListener
import com.anubis.module_usbdevice.eUDevice
import com.anubis.module_videochat.eVideoChatUI
import com.anubis.module_vncs.eVNC
import com.anubis.module_websocket.eWebSocket
import com.anubis.utils.util.eToastUtils
import com.google.gson.Gson
import com.huashi.otg.sdk.HSIDCardInfo
import com.lzy.okgo.OkGo
import com.lzy.okgo.callback.StringCallback
import com.lzy.okgo.model.Response
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.list_edit_item.*
import kotlinx.android.synthetic.main.list_edit_item.view.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jetbrains.anko.backgroundColor
import org.jetbrains.anko.custom.async
import org.jetbrains.anko.textColor
import org.jetbrains.anko.uiThread
import java.io.*
import java.net.Socket
import java.util.HashMap
import kotlin.collections.ArrayList

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
class MainActivity : AppCompatActivity() {
    private var filePath = ""
    private var file: File? = null
    private var datas: Array<String>? = null
    private var Time: Long = 0
    var XHA: XHApiManager? = null
    var progressDialog: ProgressDialog? = null

    /*扩展库对象*/
    private var mCardOTG: eCardOTG? = null
    private var mDevice: eUDevice? = null
    private var mGreenDao: eGreenDao? = null
    private var mPortMSG: ePortMSG? = null
    private var mOffice: eOffice? = null
    private var mTTS: eTTS? = null
    private var mTCP: eTCP? = null

    companion object {
        var mainActivity: MainActivity? = null
        lateinit var mHandler: Handler
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mainActivity = this@MainActivity
        mHandler = Handler()
        ParameHandleMSG.mainActivity = mainActivity
        ePermissions.eInit.eSetPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO))
        APP.mActivityList.add(this)
        datas = arrayOf( "sp_bt切换化发音调用_bt语音唤醒识别_bt语音识别", "et_bt语音合成_bt播放", "et_btSTRING_btInt_btBoolean", "et_btFloat_bt获取", "bt读取身份证_bt自动读取_bt停止读取", "et_btUSB设备数量_btUSB设备_bt文件读取", "bt加载弹窗_bteAlert弹窗_btButton弹窗", "bt导出Excel_bt导出ExcelS", "et_bt串口通信r_bt监听串口_bt关闭串口", "btHTTP测试_btHTTP循环测试", "btZIP压缩_btZIP解压_btZIP读取","bt后台启动_bt后台杀死_bt吐司改变", "et_bt二维码生成", "btLogCat", "btVNC二进制文件执行", "et_bt数据库插入_bt数据库查询_bt数据库删除", "btCPU架构", "et_btTCP连接C_bt数据发送_btTCP创建", "et_btTCP连接C关闭_btTCP连接S关闭_btTCP服务关闭", "et_btWeb连接_btWeb发送_btWeb关闭", "btAecFaceFT人脸跟踪模块_bt活体跟踪检测（路由转发跳转）", "bt开启常亮_bt关闭常亮_bt保持唤醒", "et_bt关闭唤醒_bt音视频通话", "et_bt跨APRTC初始化_bt跨AP连接_bt跨AP设置", "et_btGPIO读取", "bt开启FTP服务_bt关闭FTP服务", "bt系统设置权限检测_bt搜索WIFI", "bt创建WIFI热点0_bt创建WIFI热点_bt关闭WIFI热点", "btAPP重启", "et_btROOT权限检测_btShell执行_bt修改为系统APP", "et_bt正则匹配", "bt清除记录_bt清除缓存")
        init()
        //业务测试模块
        LoadingData()
        eGetDataBasePath(this).eLog("eGetDataBasePath")
        eGetAppDir(this).eLog("eGetAppDir")
        eGetSharedPrefsDir(this).eLog("eGetSharedPrefsDir")

        this.databaseList().joinToString().eLog("databaseList")
         eGetDataBasePath(this,"DB_ASN") .eLog("eGetDataBasePath")
    }


    /**
     * -----------------------------------------测试控制模块——————————————————————————————————————————
     */

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
                                mTTS!!.eSetParams(voiceModel[spID])
                                Handler().postDelayed({
                                    val state = mTTS!!.eSpeak("发音人切换发音调用")
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
                            iv_Hint.setImageBitmap(eQRCodeCreate.eInit.eCreateQRCode(MSG
                                    ?: "请输入内容"))
                            iv_Hint.visibility = View.VISIBLE
                            Handler().postDelayed({ iv_Hint.visibility = View.GONE }, 5000)
                        }
                    }
                    getDigit("语音合成") -> when (view?.id) {
                        R.id.bt_item1 -> Hint("语音合成：${mTTS!!.eSynthesize(MSG ?: "语音合成", "0")}")
                        R.id.bt_item2 -> Hint("语音播放：${ePlayPCM("/sdcard/img/info/output-${"0"}.pcm")}")
                    }
                    getDigit("ZIP") -> when (view?.id) {
                        R.id.bt_item1 -> {
                            val file=File("/sdcard/zip_test.text")
                            eFile.eInit.eCheckFile(file)
                            file.writeText("123546")
                            val zipFile=File("/sdcard/zip_test.zip")
                            Hint("ZIP压缩：${eFile.eInit.eZipFile(file,zipFile)}")
                        }
                        R.id.bt_item2 -> Hint("ZIP解压：${eFile.eInit.eUZipFile("/sdcard/zip_test.zip","/sdcard/zip/")}")
                        R.id.bt_item3 ->eFile.eInit.eGetFilesPath("/sdcard/zip_test.zip")?.forEach {
                            Hint("ZIP读取：$it")
                        }
                    }
                    getDigit("读取身份证") ->
                        when (view?.id) {
                            R.id.bt_item1 -> Hint("身份证读取：${mCardOTG?.eOTGRead()}")
                            R.id.bt_item2 -> Hint("身份证自动读取：${mCardOTG?.eOTGAutoRead()}")
                            R.id.bt_item3 -> Hint("关闭：${mCardOTG?.eOTGStop()}")
                        }
                    getDigit("USB设备") ->
                        when (view?.id) {
                            R.id.bt_item1 -> Hint("设备数量：${mDevice?.eGetUsbDeviceCoun}")
                            R.id.bt_item2 -> mDevice?.eGetUsbDevices?.forEach {
                                Hint("设备：${it.usbDevice}")
                            }
                            R.id.bt_item3 -> {
                                with(mDevice!!) {
                                    eGetUsbFiles(eReadUsbDevice(eGetUsbDevice(0)!!, MSG?.toInt()
                                            ?: 0)!!).forEach {
                                        Hint("文件：${it.name}")
                                    }
                                }
                            }
                        }
                    getDigit("LogCat") ->
                        async {
                            eShell.eInit.eExecShell("logcat  *:e -v time   -s AndroidRuntime ${this@MainActivity.packageName}  ${android.os.Process.myPid()} -d > /mnt/sdcard/log.txt "
                            )
                        }
                    getDigit("TCP创建") -> when (view?.id) {
                        R.id.bt_item1 -> {
                            GlobalScope.launch {
                                mTCP?.eSocketConnect(MSG?.split("||")?.get(0)
                                        ?: "192.168.1.110", MSG?.split("||")?.get(1)?.toInt()
                                        ?: 3335) { s: String, i: Int, s1: String, hashMap: HashMap<String, Socket?> ->
                                    eLog("$s-$i-$s1")
                                }
                            }
                        }
                        R.id.bt_item2 -> Hint("数据发送:${mTCP?.eSocketSend(MSG?.split("||")?.get(0)
                                ?: "123", MSG?.split("||")?.get(1)
                                ?: "192.168.1.110", mTCP?.eClientHashMap)}")
                        R.id.bt_item3 -> {
                            GlobalScope.launch {
                                mTCP?.eServerSocket { s: String, i: Int, s1: String, hashMap: HashMap<String, Socket?> ->
                                    eLog("$s-$i-$s1")
                                }
                            }
                        }
                    }
                    getDigit("服务关闭") -> when (view?.id) {
                        R.id.bt_item1 -> {
                            Hint("TCP连接C关闭:${mTCP?.eCloseServer()} ")
                        }
                        R.id.bt_item2 -> Hint("TCP连接S关闭:${mTCP?.eCloseTask(mTCP!!.eServerHashMap, MSG)} ")
                        R.id.bt_item3 -> Hint("TCP服务关闭 ：" + mTCP?.eCloseServer())
                    }

                    getDigit("Web") -> when (view?.id) {
                        R.id.bt_item1 -> Hint("Web服务连接:${eWebSocket.eInit.eConnect(MSG
                                ?: "ws://121.40.165.18:8800", handleWeb)}")
                        R.id.bt_item2 -> Hint("Web服务发送:${eWebSocket.eInit.eSendMSG(MSG ?: "123")}")
                        R.id.bt_item3 -> Hint("Web服务关闭 ：" + eWebSocket.eInit.eClose())
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
                        R.id.bt_item2 -> Hint("获取-String:${eGetSystemSharedPreferences("string", "string")}-Int:${eGetSystemSharedPreferences("int", 123)}-Boolean:${eGetSystemSharedPreferences("boolean", true)}-Float:${eGetSystemSharedPreferences("float", 0f)}}")
                    }

                    getDigit("VNC") -> when (view?.id) {
                        R.id.bt_item1 -> Hint("VNC二进制文件执行:${if (eVNC.eInit(this@MainActivity).eStartVNCs()) "成功：5901" else "失败"}")
                    }
                    getDigit("开启常亮") -> when (view?.id) {
                        R.id.bt_item1 ->   eApp.eInit.eLongScreen(this@MainActivity,true)
                        R.id.bt_item2 ->   eApp.eInit.eLongScreen(this@MainActivity,false)
                        R.id.bt_item3 ->   eApp.eInit.eCUPWakeLock(this@MainActivity,true)
                    }
                    getDigit("音视频") ->
                        when (view?.id) {
                            R.id.bt_item1 -> eApp.eInit.eCUPWakeLock(this@MainActivity,false)
                            R.id.bt_item2 -> {
                                val intent = Intent(this@MainActivity, eVideoChatUI::class.java)
                                intent.putExtra("appID", MSG?.split("||")?.get(0))
                                intent.putExtra("channelName", MSG?.split("||")?.get(1))
                                startActivity(intent)
                            }
                        }
                    getDigit("跨AP") -> when (view?.id) {
                        R.id.bt_item1 -> {
                            val intent = Intent()
                            if (MSG == null)
                                intent.data = Uri.parse("sak://com.anubis.app_webrtc?url=119.23.77.41&localId=123&autoAnswer=true&type=SET")
                            else
                                intent.data = Uri.parse("sak://com.anubis.app_webrtc?url=${MSG.split("||")[0]}&localId=${MSG.split("||")[1]}&autoAnswer=${MSG.split("||")[2]}&type=SET")
                            startActivity(intent)
                        }
                        R.id.bt_item2 -> {
                            Hint("运行状态:${eApp.eInit.eIsAppRunning(this@MainActivity, "com.anubis.app_webrtc")}")
                            Hint("安装状态:${eApp.eInit.eIsAppInstall(this@MainActivity, "com.anubis.app_webrtc")}")
                            val intent = Intent()
                            intent.data = Uri.parse("sak://com.anubis.app_webrtc?targetId=${MSG?.split("||")?.get(0)}&maxTime=${MSG?.split("||")?.get(1)}&cameraId=${MSG?.split("||")?.get(2)}&type=CALL")
                            startActivity(intent)
                        }
//                        R.id.bt_item2 -> {
//                     挂断
//                            val intent = Intent()
//                            intent.data= Uri.parse("asn://com.anubis.app_webrtc?type=HANG")
//                            startActivity(intent)
//                        }
                        R.id.bt_item3 -> {
                            val intent = Intent()
                            intent.data = Uri.parse("sak://com.anubis.app_webrtc?type=SETUI")
                            startActivity(intent)
                        }
                    }
//

                    getDigit("CPU") -> when (view?.id) {
                        R.id.bt_item1 -> Hint("CPU架构:${android.os.Build.CPU_ABI}")
                    }
                    getDigit("FTP") -> when (view?.id) {
                        R.id.bt_item1 -> Hint("FTP服务启动:${startActivity(Intent(this@MainActivity, eFTPUIs::class.java))}")
                        R.id.bt_item2 -> Hint("FTP服务关闭:${sendBroadcast(Intent(FsService.ACTION_STOP_FTPSERVER))}")
                    }
                    getDigit("加载弹窗") -> when (view?.id) {
                        bt_item1.id -> {
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
                                    mHandler.post { progressDialog!!.incrementProgressBy(1) }
//                                handleMsg.sendMessage(msg)
                                    eLog("i$i")
                                }
                                uiThread { progressDialog?.dismiss() }
                            }
                        }
                        bt_item2.id -> eDiaAlert.eInit(this@MainActivity).eDefaultShow("动态弹窗测试", adbEditBlock = { dialog: Dialog, view: View, eArrowDownloadButton: eArrowDownloadButton ->
                            eArrowDownloadButton.startAnimating()
                            eArrowDownloadButton.textPaintColor = Color.BLUE
                            GlobalScope.launch {
                                for (i in 0..100) {
                                    this@MainActivity.runOnUiThread { eArrowDownloadButton.progress = i.toFloat() }
                                    delay(100)
                                }
                                this@MainActivity.runOnUiThread { dialog.dismiss() }
                            }
                        }, isCanceledOnTouchOutside = true)
                        bt_item3.id -> eDiaAlert.eInit(this@MainActivity).eDefaultShow("点击弹窗测试", AVIID = 5,btOK = "测试提交", btCancel = "测试关闭", onClickAnimation = { view: View?, l: Long ->
                            with(view as? Button) {
                                this?.textColor = Color.parseColor("#ff0000")
                                this?.backgroundColor = Color.parseColor("#04FFE3")
                                Handler().postDelayed({
                                    this?.textColor = Color.parseColor("#25CEFB")
                                    this?.backgroundColor = Color.parseColor("#04FFE3")
                                }, l)
                            }
                        }, ICallBackClick = object : eDiaAlert.ICallBackClick {
                            override fun onClickOK(dia: Dialog, it: View?) {
                                eShowTip("确定")
                                dia.dismiss()
                            }

                            override fun onClickCancel(dia: Dialog, it: View?) {
                                eShowTip("关闭")
                                dia.dismiss()
                            }

                            override fun onClickClose(dia: Dialog, it: View?) {
                            }

                        })
                    }
                    getDigit("Excel导出") -> when (view?.id) {
                        bt_item1.id -> {
                            Hint("Excel导出测试数据库：${mOffice?.eExportExcel(arrayOf("Time", "Name"), mGreenDao?.eQueryAllUser(data())!!)}")
                        }
                        bt_item2.id -> {
                            val tDatas = mGreenDao?.eQueryAllUser(data())!!
                            Hint("ExcelS导出测试数据库：${mOffice?.eExportExcel(arrayOf(arrayOf("Time1", "Name1"), arrayOf("Time2", "Name2")), arrayOf(tDatas, tDatas.asReversed()), mSheetNames = arrayOf("记录1", "记录2"))}")
                        }
                    }
                    getDigit("后台杀死") -> when (view?.id) {
                        R.id.bt_item1 -> {
                            Hint("后台服务状态：${eApp.eInit.eIsServiceRunning(this@MainActivity, MyService::class.java.name)}")
                            Hint("后台启动状态：${startService(Intent(this@MainActivity, MyService::class.java))}")
                        }
                        R.id.bt_item2 -> Hint("后台杀死状态：${eApp.eInit.eKillBackgroundProcesses(this@MainActivity, MyService::class.java.name)}")
                        R.id.bt_item3 -> {
                            eToastUtils.setMsgColor(Color.GREEN)
                            eToastUtils.showShort("Toast测试")
                        }
                    }
                    getDigit("APP重启") -> Hint("APP重启:${eApp.eInit.eAppRestart(mAPP, this@MainActivity)}")
                    getDigit("串口通信") -> {
                        val msg = "A55501FB"
                        when (view?.id) {
                            R.id.bt_item1 -> Hint("串口数据发送：" + mPortMSG?.eSendMSG(msg))
                            R.id.bt_item2 -> Hint("串口监听：" + mPortMSG?.eGetMSG())
                            R.id.bt_item3 -> Hint("串口关闭：" + mPortMSG?.eClosePort())
                        }
                    }
                    getDigit("数据库") -> when (view?.id) {
                        R.id.bt_item1 -> Hint("数据库插入：${mGreenDao?.eInsertUser(data(eTime.eInit.eGetTime(), MSG
                                ?: ""))}")
                        R.id.bt_item2 -> {
                            Hint("数据库查询:")
                            mGreenDao?.eQueryAllUser(data())?.forEach {
                                Hint("${it.s}:${it.ss}")
                            }
                        }
                        R.id.bt_item3 -> Hint("数据库删除：${mGreenDao?.eDeleteAll(data("", ""))}")
                    }
                    getDigit("系统设置权限检测") -> when (view?.id) {
                        R.id.bt_item1 -> if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            Hint("系统设置权限检测：${ePermissions.eInit.eSetSystemPermissions(this@MainActivity)}")
                        } else {
                            Hint("安卓版本低于6.0--${Build.VERSION.SDK_INT}")
                        }
                        R.id.bt_item2 -> {
                            Hint("搜索WIFI:")
                            for (wifi in eWiFi.eInit.eGetScanWiFi(this@MainActivity)!!) {
                                Hint("SSID:${wifi.SSID}")
                            }
                        }
                    }
                    getDigit("热点") -> when (view?.id) {
                        R.id.bt_item1 -> Hint("创建WIFI热点0:${eWiFi.eInit.eCreateWifiHotspot(this@MainActivity)}")
                        R.id.bt_item2 -> Hint("创建WIFI热点:${eWiFi.eInit.eCreateWifiHotspot(this@MainActivity)}")
                        R.id.bt_item3 -> Hint("关闭WIFI热点：${eWiFi.eInit.eCloseWifiHotspot(this@MainActivity)}")
                    }
//                    getDigit("自定义") ->startActivity(Intent(this@MainActivity,TestViewActivity::class.java))
                    getDigit("路由转发跳转") ->
                        when (view?.id) {
                            R.id.bt_item1 -> ARouter.getInstance().build("/face/ArcFaceFT").navigation()
                            R.id.bt_item2 -> ARouter.getInstance().build("/face/ArcFace").navigation()
                        }

                    getDigit("Shell执行") -> when (view?.id) {
                        R.id.bt_item1 -> Hint("ROOT权限检测:${eShell.eInit.eHaveRoot()}")
                        R.id.bt_item2 -> Hint("Shell执行：${eShell.eInit.eExecShell(MSG ?: "setprop service.adb.tcp.port 5555 stop adbd start adbd")}")
                        R.id.bt_item3 -> {
                            if (MSG?.isNotEmpty() == true) {
                                val shell = "cp -r /datas/APP/$MSG* /system/priv*"
                                Hint("自定义修改为系统APP:" + eShell.eInit.eExecShell(shell))
                                Hint("执行Shell:$shell")
                            } else {
                                var shell = " cp -r /datas/APP/$packageName* /system/priv*"
                                Hint("修改为系统APP:" + eShell.eInit.eExecShell(shell))
                                Hint("执行Shell:$shell")
                                if (File("/datas/APP-lib/$packageName-1").exists()) {
                                    shell = "mv /datas/APP-lib/$packageName*/ /system/lib/"
                                    Hint("文件夹存在，修改lib数据:" + eShell.eInit.eExecShell(shell))
                                    Hint("执行Shell:$shell")
                                }
                            }
                            Handler().postDelayed({
                                val shell = "chmod -R 755   /system/priv*/$MSG*"
                                Hint("修改文件权限:" + eShell.eInit.eExecShell(shell))
                                Hint("执行Shell:$shell")
                            }, 2000)
                            Handler().postDelayed({
                                val shell = " rm -rf /datas/APP/$MSG*"
                                Hint("删除数据遗留:" + eShell.eInit.eExecShell(shell))
                                Hint("执行Shell:$shell")
                                eShowTip("请重启设备")
                            }, 3500)
                        }
                    }
                    getDigit("正则匹配") -> Hint("正则匹配：/datas/APP/$packageName$MSG")
                    getDigit("清除记录") ->
                        when (view?.id) {
                            R.id.bt_item1->     if (System.currentTimeMillis() - Time > 1000) {
                                Time = System.currentTimeMillis()
                            } else {
                                tv_Hint.text = ""
                                file!!.writeText("")
                                eShowTip("记录已清除")
                            }
                            R.id.bt_item2-> Hint("缓存清理：${eApp.eInit.eAppCleanData(this@MainActivity)}")
                        }
                }
            }
        }
        val myAdapter = MyAdapter(this, datas!!, callback)
        rvList.adapter = myAdapter
        rvList.setItemViewCacheSize(datas!!.size)
        eShell.eInit.eExecShell("mount -o remount,rw rootfs /system/")


    }


    /**
     * -----------------------------------------业务控制模块——————————————————————————————————————————
     */
    fun LoadingData() {
        /* 主板SDK*/
        if (Build.MODEL == "ZK-R32A")
            XHA = XHApiManager()
        /*身份证阅读器*/
        mCardOTG = eCardOTG.eInit(mAPP, mHandler, object : eCardOTG.IResult {
            override fun CONNECT_SUCCESS(successMsg: String, SAMID: String) {
                eLog("$SAMID--$successMsg")
            }

            override fun CONNECT_ERROR(errorMsg: String, SAMID: String) {
                eLog("$SAMID--$errorMsg")
            }

            override fun READ_SUCCESS(cardInfo: HSIDCardInfo, fingerprintStr: String, imgPath: String, SAMID: String) {
                eLog("$SAMID--${Gson().toJson(cardInfo)}--$fingerprintStr--$imgPath")
            }

            override fun READ_ERROR(msg: String, SAMID: String) {
                eLog("$SAMID--$msg")
            }

        })
        /*SB设备*/
        mDevice = eUDevice.eInit(mAPP, uHandler)

        /*数据库*/
        mGreenDao = eGreenDao.eInit(this)

        /*Excel导出*/
        mOffice = eOffice.eInit(this)

        /*串口通信*/
        mPortMSG = ePortMSG.eInit(this, callback = object : ePortMSG.ICallBack {
            override fun IonLockerDataReceived(buffer: ByteArray, size: Int, path: String) {
                Hint("串口数据接收:${eString.eInit.eGetByteArrToHexStr(buffer)}--$path")
            }
        })

        /*TTS语音合成*/
        mTTS = eTTS.eInit(mAPP, arrayOf("13612239", "yfXyxUQXxDO7Vcp6h7LtH3RC", "UdKuiwWqIeFlzr3aGUNEutCkA0avXE3o"), handleTTS, TTSMode.MIX, VoiceModel.MALE, listener = FileSaveListener(handleTTS, "/sdcard/img/info"))
        mTCP = eTCP.eInit(Handler())
    }

    /**
     * -----------------------------------------测试组件模块——————————————————————————————————————————
     */
    fun Hint(str: String) {
        tv_Hint.post {
            val Str = "${eTime.eInit.eGetTime("MM-dd HH:mm:ss")}： $str\n\n\n"
            str.eLog()
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

    class MyAdapter(val mContext: Context, val mDatas: Array<String>, val mCallbacks: ICallBack) : androidx.recyclerview.widget.RecyclerView.Adapter<MyAdapter.MyHolder>() {
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
                    mainActivity?.Hint("执行错误-:${eErrorOut(e)}")
                }
            }
            holder.itemView.bt_item2.setOnClickListener {
                try {
                    var editContext: String? = holder.itemView.et_item1.text.toString()
                    editContext = if (editContext?.isEmpty() != false) null else editContext
                    mCallbacks.CallResult(it, position, editContext, holder.itemView.sp_item1)
                } catch (e: Exception) {
                    mainActivity?.Hint("数据操作错误:$e")
                }
            }
            holder.itemView.bt_item3.setOnClickListener {
                try {
                    var editContext: String? = holder.itemView.et_item1.text.toString()
                    editContext = if (editContext?.isEmpty() != false) null else editContext
                    mCallbacks.CallResult(it, position, editContext, holder.itemView.sp_item1)
                } catch (e: Exception) {
                    mainActivity?.Hint("数据操作错误:$e")
                }
            }

        }

        //界面设置 ed_    sp_  bt_x3
        inner class MyHolder(itemView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {
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
                                itemView.bt_item1.text = eString.eInit.eGetNumberPeriod(str, 2, "MAX")
                            }
                            1 -> {
                                itemView.bt_item2.visibility = View.VISIBLE
                                itemView.bt_item2.text = eString.eInit.eGetNumberPeriod(str, 2, "MAX")
                            }
                            2 -> {
                                itemView.bt_item3.visibility = View.VISIBLE
                                itemView.bt_item3.text = eString.eInit.eGetNumberPeriod(str, 2, "MAX")
                            }
                        }

                    }
                } catch (e: Exception) {
                    if (datas.substring(0, 2) == "bt") {
                        itemView.bt_item1.visibility = View.VISIBLE
                        itemView.bt_item1.text = eString.eInit.eGetNumberPeriod(datas, 2, "MAX")
                    }
                }
            }
        }
    }

    /**
     * -----------------------------------------周期控制模块——————————————————————————————————————————
     */


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        ePermissions.eInit.eSetOnRequestPermissionsResult(this, requestCode, permissions, grantResults)
        if (requestCode != 1) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }


    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        Hint("keyCode:$keyCode")
        eLog("size" + APP.mAPP.mActivityList?.size)
        return eEvent.eInit.eSetKeyDownExit(this, keyCode, mAPP.mActivityList, false, exitHint = "完成退出")
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



