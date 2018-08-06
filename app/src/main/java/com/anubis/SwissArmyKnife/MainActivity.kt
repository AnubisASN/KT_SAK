package com.anubis.SwissArmyKnife

import android.Manifest
import android.app.Activity
import android.app.ActivityManager
import android.app.Notification
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.PackageInfo
import android.content.pm.PackageManager.GET_UNINSTALLED_PACKAGES
import android.os.Build
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.view.KeyEvent
import android.view.View
import android.widget.EditText
import android.widget.TextView

import com.anubis.module_arcfaceft.eArcFaceFTActivity
import com.anubis.module_gorge.eGorgeMessage
import com.anubis.module_tts.eTTS
import com.alibaba.android.arouter.launcher.ARouter
import com.anubis.SwissArmyKnife.R.id.edit
import com.anubis.kt_extends.*
import com.anubis.module_tts.Bean.*
import kotlinx.android.synthetic.main.activity_main.*
import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import kotlin.math.log
import android.widget.TextView.OnEditorActionListener
import com.anubis.SwissArmyKnife.R.id.async
import org.jetbrains.anko.activityManager
import org.jetbrains.anko.custom.async


class MainActivity : Activity() {
    var TTS: eTTS? = null
    var APP: app? = null
    var mEGorge: eGorgeMessage? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
      val s=  eSetPermissions(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA))
        eLog("ssss:$s")
        APP = app().get()
        app().get()?.getActivity()!!.add(this)
        TTS = eTTS.initTTS(app().get()!!, app().get()!!.mHandler!!, TTSMode.ONLINE)
        mEGorge = eGorgeMessage().getInit(this)
        edit.setOnEditorActionListener { textView, i, keyEvent ->
            if (i == 0) {
                async {
                    eLog("Shell:\n" + eExecShell.eExecShell(edit.text.toString()))
                }
                edit.setText("")
            }
            false
        }
        getInfo()
        eLog(eGetShowActivity())
    }


    fun getInfo() {
        eLog("packageName:$packageName---CPU:${Build.CPU_ABI}")


    }


    fun mainClick(v: View) {
        when (v.id) {
            R.id.button2 -> TTS!!.setParams().speak("初始化调用")
            R.id.button3 -> TTS!!.setParams(VoiceModel.EMOTIONAL_MALE).speak("发音人切换,网络优先调用")
            R.id.button4 -> ARouter.getInstance().build("/app/Test1").navigation()
            R.id.button5 -> reflection("com.anubis.SwissArmyKnife.Reflection.Reflection")
            R.id.button6 -> ARouter.getInstance().build("/face/arcFace").navigation()
            R.id.button7 -> startActivity(Intent(this, Face::class.java))
            R.id.button8 -> {
                val cls = Class.forName("com.anubis.SwissArmyKnife.Face")
                startActivity(Intent(this, cls))
            }
            R.id.button9 -> {
                if (edit.text.toString().trim().isEmpty()) {
                    eLog(Runtime.getRuntime().exec("ls").eText())
                } else {
                    eLog("Shell:\n"+eExecShell.eExecShell (edit.text.toString()))
                }
            }
            R.id.button10 -> eShowTip(eExecShell.eHaveRoot())
            R.id.button11 -> {
                val f = File(this.filesDir.path + "/123.txt")
                if (f.exists()) {
                    f.writeText(edit.text.toString())
                    eShowTip("文件写入成功")
                } else {
                    f.createNewFile()
                    eShowTip("文件创建成功")
                }
            }
            R.id.button12 -> {
                val f = FileReader(this.filesDir.path + "/123.txt")
                var out: String? = ""
                val buf = BufferedReader(f)
                while (true) {
                    out = buf.readLine() ?: break
                    eLog(out)
                    eShowTip("buf" + out)

                }

//                eLog(buf.readLine())
            }
            R.id.button13 -> eLog("Ping:" + eGetNetDelayTime())
            R.id.button14 -> {
                val acs=PackageInfo().activities
                for (a in acs){
                    eLog("a:"+a.toString())
                }
                eLog(ActivityInfo().name)
               eLog("ac"+ intent.resolveActivityInfo(packageManager,0))
            }
            R.id.button15-> eLog(eGetShowActivity())
        }
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
        val method = cls.getDeclaredMethod("toastr", Activity::class.java, String::class.java)
        eLog("获得所有方法${cls.declaredMethods}--获得方法传入类型：${method.parameterTypes}")
        method.invoke(clsInstance, this, "00115492654+")
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        eLog("size" + app().get()?.getActivity()!!.size)
        return eSetKeyDownExit(keyCode, app().get()?.getActivity(), false, exitHint = "完成退出")
    }


}





