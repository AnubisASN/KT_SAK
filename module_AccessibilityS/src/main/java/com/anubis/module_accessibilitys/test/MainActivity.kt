package com.anubis.module_accessibilitys.test

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import com.anubis.kt_extends.eApp
import com.anubis.kt_extends.ePermissions
import com.anubis.kt_extends.eShell
import com.anubis.kt_extends.eShowTip
import com.anubis.module_accessibilitys.R
import com.anubis.module_accessibilitys.eAccessibilityService
import com.anubis.module_accessibilitys.eTools
import com.anubis.module_extends.DataItemInfo
import com.anubis.module_extends.eRvAdapter
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    val tData = arrayListOf<String>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        for (i in 0..50) {
            tData.add( i.toString())
        }
//        rv.adapter=ArrayAdapter(this,android.R.layout.simple_list_item_1,tData)
//        eRvAdapter(this, rv, R.layout.adapter_default_item, tData) { view: View, dataItemInfo: DataItemInfo, i: Int ->
//            eShowTip("点击了：${dataItemInfo.str1}")
//        }
    }

    fun onClick(v: View) {
        when (v) {
            button -> {
                if (eApp.eInit.eIsServiceRunning(this, testServer::class.java.name)) {
                    eShowTip("服务已经在运行！")
                } else {
                    eShowTip("服务未运行！")
                }
            }
            button1 -> eApp.eInit.eInstallApkFile(this, "/sdcard/T.apk")
            button2 -> ePermissions.eInit.eAccessibilityPermissions(this)
            button3 -> {
                if (button3.text == "测试") {
                    button3.text = "被点击了"
                    eShowTip("测试点击了")
                } else {
                    button3.text = "测试"
                }
            }
            button4 -> {
                if (button3.text == "ID点击") {
                    button3.text = "被点击了"
                    eShowTip("测试点击了")
                } else {
                    button3.text = "ID点击"
                }
            }
            button5->{
//                eShell.eInit.eExecShell(eTools.s0)
//                eShell.eInit.eExecShell(eTools.s1)
            }
        }
    }


}
