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
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.alibaba.android.arouter.launcher.ARouter
import com.anubis.kt_extends.*
import com.anubis.module_arcfaceft.eArcFaceFTActivity
import com.anubis.module_gorge.eGorgeMessage
import com.anubis.module_tts.Bean.TTSMode
import com.anubis.module_tts.Bean.VoiceModel
import com.anubis.module_tts.eTTS
import kotlinx.android.synthetic.main.list_edit_item.view.*
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
        data = arrayOf("初始化发音", "发音人切换调用", "动态加载", "AecFaceFT人脸跟踪模块（Intent跳转）", "AecFaceFT人脸跟踪模块（动态加载跳转）", "ROOT权限检测", "执行Shell1")
        init()
    }

    private fun init() {
        rvList.layoutManager = LinearLayoutManager(this)
        val callback = object : ICallBack {
            override fun CallResult(view: View, itmeID: Int, MSG: String) {
                when (itmeID) {
                    data!!.indexOf("初始化发音") -> TTS!!.setParams().speak("初始化发音调用")
                    data!!.indexOf("发音人切换调用") -> TTS!!.setParams(VoiceModel.EMOTIONAL_MALE).speak("发音人切换,网络优先调用")
                    data!!.indexOf("动态加载") -> reflection("com.anubis.SwissArmyKnife.MainActivity")
                    data!!.indexOf("AecFaceFT人脸跟踪模块（Intent跳转）") -> startActivity(Intent(this@MainActivity, Face::class.java))
                    data!!.indexOf("AecFaceFT人脸跟踪模块（动态加载跳转）") -> ARouter.getInstance().build("/face/arcFace").navigation()
                    data!!.indexOf("ROOT权限检测") -> tv_Hint.append("ROOT权限检测:${eExecShell.eHaveRoot()}\n")
                    data!!.indexOf("执行Shell1") -> if (MSG.isNotEmpty()) {
                        tv_Hint.append("Shell:\n" + eExecShell.eExecShell(MSG) + "\n")
                    }
                }

            }
        }
        val myAdapter = MyAdapter(this, data!!, callback)
        rvList.adapter = myAdapter
    }

    class MyAdapter(val mContext: Context, val mDatas: Array<String>, val mCallbacks: ICallBack) : RecyclerView.Adapter<MyAdapter.MyHolder>() {
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
                var editContext = ""
                if (it.tag == "1") {
                    editContext = holder.itemView.ed_item.text.toString()
                }
                mCallbacks.CallResult(it, position, editContext)
            }

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
        val method = cls.getDeclaredMethod("ShowTip",String::class.java)
        tv_Hint.append("获得所有方法:${cls.declaredMethods}--获得方法传入类型：${method.parameterTypes}")
        method.invoke(clsInstance, this, "类动态加载")
    }

   private fun ShowTip(msg: String) {
        eShowTip(msg)
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        eLog("size" + app().get()?.getActivity()!!.size)
        return eSetKeyDownExit(keyCode, app().get()?.getActivity(), false, exitHint = "完成退出")
    }

    interface ICallBack {
        fun CallResult(view: View, numID: Int, MSG: String)
    }


}






