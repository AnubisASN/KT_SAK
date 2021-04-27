package com.anubis.app_skin

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.anubis.kt_extends.eLog
import com.anubis.kt_extends.eShowTip
import kotlinx.android.synthetic.main.activity_board_test.*
import org.jetbrains.anko.onClick
import readsense.face.util.A905Board


class BoardTest : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_board_test)
        eShowTip("主板：${Build.MODEL}")
        val x= A905Board.eInit(this)
        button1.onClick {
            it as View
            if (it.tag=="0"){
                it.tag="1"
                x.ctrlPelay(true)
                eShowTip("继电器：开")
            }else{
                it.tag="0"
                x.ctrlPelay(false)
                eShowTip("继电器：关")
            }
        }
        button3.onClick {
                x.outWG("123")
            eShowTip("WG发送：123")
        }
        button4.onClick {
            it as View
            if (it.tag=="0"){
                it.tag="1"
                x.ctrlHideNAV()
                eShowTip("导航栏：开")
            }else{
                it.tag="0"
                x.ctrlHideNAV(false)
                eShowTip("导航栏：关")
            }
        }
        button5.onClick {
                x.dog()
            eShowTip("看门狗：开")
        }
        button7.onClick {
            x.dog(false)
            eShowTip("看门狗：关")
        }
        button6.onClick {
            it as View
            if (it.tag=="0"){
                it.tag="1"
                x.ctrlWifi()
                eShowTip("WIFI：开")
            }else{
                it.tag="0"
                x.ctrlWifi(false)
                eShowTip("WIFI：关")
            }
        }

        button2.onClick {
            it as View
            if (it.tag=="0"){
                it.tag="1"
                x.ctrlLED(1)
                eShowTip("LED：开")
            }else{
                it.tag="0"
                x.ctrlLED(0)
                eShowTip("LED：关")
            }
        }
        button20.onClick {
                x.getEthernet {
                    it.toString().eLog("getEthernet")
                }

            eShowTip("获取以太网")
        }
        button21.onClick {
                x.setEthernet(A905Board.dataNET(false,"10.2.192.25","10.2.192.1","255.0.0.0"))
                eShowTip("设置以太网")
        }
        button22.onClick {
            it as View
            if (it.tag=="0"){
                it.tag="1"
            }else{
                it.tag="0"
            }
        }
        button23.onClick {
            it as View
            if (it.tag=="0"){
                it.tag="1"
            }else{
                it.tag="0"
            }
        }
    }
}
