package com.anubis.SwissArmyKnife

import android.Manifest
import android.app.Activity
import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Printer
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import com.anubis.SwissArmyKnife.Adapter.MyAdapter
import com.anubis.kt_extends.*
import com.anubis.module_arcfaceft.eArcFaceFTActivity
import com.anubis.module_gorge.eGorgeMessage
import com.anubis.module_tts.Bean.TTSMode
import com.anubis.module_tts.eTTS
import kotlinx.android.synthetic.main.list_edit_item.view.*
import com.anubis.SwissArmyKnife.R.id.rvList
import com.tencent.bugly.proguard.v
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : Activity() {
    private var TTS: eTTS? = null
    private var APP: app? = null
    private var data: Array<String>? = null
    var mEGorge: eGorgeMessage? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val s = eSetPermissions(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA))
        APP = app().get()
        app().get()?.getActivity()!!.add(this)
        TTS = eTTS.initTTS(app().get()!!, app().get()!!.mHandler!!, TTSMode.ONLINE)
        mEGorge = eGorgeMessage().getInit(this)
        getInfo()
        eLog(eGetShowActivity())
        data = arrayOf("初始化发音", "发音人切记调用", "发音人切记调用", "动态加载", "AecFaceFT人脸跟踪模块（Intent跳转）", "AecFaceFT人脸跟踪模块（动态加载跳转）", "ROOT检测权限", "执行Shell1")
        init()
    }

    private fun init() {
        rvList.layoutManager = LinearLayoutManager(this)
        val myAdapter = MyAdapter(this, data!!)
        rvList.adapter = myAdapter

    }

    inner class People {

        internal var printer = Printer()

        /*
   * 同步回调
   */
     //   fun goToPrintSyn(callback: Callback, text: String) {
     //       printer.print(callback, text)
     //   }

        /*
   * 异步回调
   */
        fun goToPrintASyn(callback: Callback, text: String) {
            Thread(Runnable { printer.print(callback, text) }).start()
        }
    }

    class MyAdapter(val mContext: Context, val mDatas: Array<String>) : RecyclerView.Adapter<MyAdapter.MyHolder>() {
        var mPosition: Int? = null
        override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): MyHolder {
            val view = LayoutInflater.from(mContext).inflate(R.layout.list_edit_item, parent, false)

            return MyHolder(view)
        }

        override fun getItemCount(): Int {
            return mDatas.size
        }

        override fun onBindViewHolder(holder: MyHolder, position: Int) {
            mPosition = position
            holder.setData(mDatas[position])
            holder.itemView.bt_item.setOnClickListener {
                var editContext: String = ""
                if (it.tag == "1") {
                    editContext = holder.itemView.ed_item.text.toString()
                }
                mContext.eShowTip("itID:--$position--输入内容：$editContext")
            }

        }

        interface Callback {
            fun Result(mes:String)
        }
        inner class MyHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            fun setData(data: String) {
                itemView.ed_item.visibility = View.GONE
                if (data.last().toString() == "1") {
                    itemView.ed_item.visibility = View.VISIBLE
                    itemView.bt_item.tag = "1"
                }
                itemView.bt_item.text = data

            }
        }


    }

    fun getInfo() {
        eLog("packageName:$packageName---CPU:${Build.CPU_ABI}")
    }


//    fun mainClick(v: View) {
//        when (v.id) {
//            R.id.button2 -> TTS!!.setParams().speak("初始化调用")
//            R.id.button3 -> TTS!!.setParams(VoiceModel.EMOTIONAL_MALE).speak("发音人切换,网络优先调用")
//            R.id.button4 -> ARouter.getInstance().build("/app/Test1").navigation()
//            R.id.button5 -> reflection("com.anubis.SwissArmyKnife.Reflection.Reflection")
//            R.id.button6 -> ARouter.getInstance().build("/face/arcFace").navigation()
//            R.id.button7 -> startActivity(Intent(this, Face::class.java))
//            R.id.button8 -> {
//                val cls = Class.forName("com.anubis.SwissArmyKnife.Face")
//                startActivity(Intent(this, cls))
//            }
//            R.id.button9 -> {
//                if (edit.text.toString().trim().isEmpty()) {
//                try {
//                 val shell="cp /data/app/com.anubis.SwissArmyKnife-1/base.apk /data/app/com.anubis.SwissArmyKnife-1/base1.apk"
//                 val shell1="cp /storage/emulated/0/Record/记录.xls /storage/emulated/0/Record/记录1.xls"
//                    Runtime.getRuntime().exec(shell)
//                } catch (e: IOException) {
//                    Log.e("runtime", e.toString())
//                    e.printStackTrace()
//                }
//                } else {
//                    eLog("Shell:\n" + eExecShell.eExecShell(edit.text.toString()))
//                }
//            }
//            R.id.button10 ->  eExecShell.eShell()
////                eShowTip(eExecShell.eHaveRoot())
//            R.id.button11 -> {
//                val f = File(this.filesDir.path + "/123.txt")
//                if (f.exists()) {
//                    f.writeText(edit.text.toString())
//                    eShowTip("文件写入成功")
//                } else {
//                    f.createNewFile()
//                    eShowTip("文件创建成功")
//                }
//            }
//            R.id.button12 -> {
//                val f = FileReader(this.filesDir.path + "/123.txt")
//                var out: String? = ""
//                val buf = BufferedReader(f)
//                while (true) {
//                    out = buf.readLine() ?: break
//                    eLog(out)
//                    eShowTip("buf" + out)
//
//                }
//
////                eLog(buf.readLine())
//            }
//            R.id.button13 -> eLog("Ping:" + eGetNetDelayTime())
//            R.id.button14 -> {
//                val acs = PackageInfo().activities
//                for (a in acs) {
//                    eLog("a:" + a.toString())
//                }
//                eLog(ActivityInfo().name)
//                eLog("ac" + intent.resolveActivityInfo(packageManager, 0))
//            }
//            R.id.button15 -> eLog(eGetShowActivity())
//            R.id.button16 ->{
////                val da= dataTest("s","ss")
////                eExportExcel(this, arrayOf("1","2"), mutableListOf(da,da))
//            }
//            R.id.button17->{
//                SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(eGetCurrentTime())-etTime
//
//
//            }
////                ExportExcel(this, arrayOf("1","2"), mutableListOf(dataTest("s", "ss")), "列表测试", "列表测试", "列表测试1")
//        }
//    }

    fun Context.esExistMainActivity(activity: Class<*>): Boolean {
        val intent = Intent(this, activity)
        val cmpName = intent.resolveActivity(packageManager)
        var flag = false
        if (cmpName != null) { // 说明系统中存在这个activity
            val am: ActivityManager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            val taskInfoList = am.getRunningTasks(30) //获取从栈顶开始往下查找的10个activity
            for (taskInfo in taskInfoList) {
                eLog("$taskInfo---" + cmpName)
                if (taskInfo.baseActivity == cmpName) { // 说明它已经启动了
                    flag = true
                    break //跳出循环，优化效率
                }
            }
        }
        return flag
    }

    fun startDetector() {
        val it = Intent(this, eArcFaceFTActivity::class.java)
        ActivityCompat.startActivityForResult(this, it, 3, null)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        eSetOnRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode != 1) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }

    }

    fun reflection(packName: String) {
        val cls = Class.forName(packName)
        val clsInstance = cls.newInstance()
        val method = cls.getDeclaredMethod("toastr", Activity::class.java, String::class.java)
        eLog("获得所有方法${cls.declaredMethods}--获得方法传入类型：${method.parameterTypes}")
        method.invoke(clsInstance, this, "00115492654+")
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        eLog("size" + app().get()?.getActivity()!!.size)
        return eSetKeyDownExit(keyCode, app().get()?.getActivity(), false, exitHint = "完成退出")
    }


}






