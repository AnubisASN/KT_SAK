package com.anubis.module_webRTC.demo.p2p

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.view.WindowManager
import android.widget.TextView

import com.anubis.module_webRTC.R
import com.anubis.module_webRTC.demo.BaseActivity
import com.anubis.module_webRTC.demo.MLOC


class VoipP2PCreateActivity : BaseActivity(), View.OnClickListener {
    private var vTargetId: TextView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_voip_p2p_create)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN)
        (findViewById<View>(R.id.title_text) as TextView).text = "请输入目标终端IP"
        findViewById<View>(R.id.title_left_btn).visibility = View.VISIBLE
        findViewById<View>(R.id.title_left_btn).setOnClickListener { finish() }

        vTargetId = findViewById<View>(R.id.targetid_input) as TextView
        findViewById<View>(R.id.btn_1).setOnClickListener(this)
        findViewById<View>(R.id.btn_2).setOnClickListener(this)
        findViewById<View>(R.id.btn_3).setOnClickListener(this)
        findViewById<View>(R.id.btn_4).setOnClickListener(this)
        findViewById<View>(R.id.btn_5).setOnClickListener(this)
        findViewById<View>(R.id.btn_6).setOnClickListener(this)
        findViewById<View>(R.id.btn_7).setOnClickListener(this)
        findViewById<View>(R.id.btn_8).setOnClickListener(this)
        findViewById<View>(R.id.btn_9).setOnClickListener(this)
        findViewById<View>(R.id.btn_0).setOnClickListener(this)
        findViewById<View>(R.id.btn_point).setOnClickListener(this)
        findViewById<View>(R.id.btn_clean).setOnClickListener(this)
        findViewById<View>(R.id.btn_call).setOnClickListener(this)

    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.btn_call -> {
                val inputId = (findViewById<View>(R.id.targetid_input) as TextView).text.toString()
                if (TextUtils.isEmpty(inputId)) {
                    MLOC.showMsg(this@VoipP2PCreateActivity, "ip不能为空")
                } else {
                    saveIp(inputId)
                    val intent = Intent(this@VoipP2PCreateActivity, VoipP2PActivity::class.java)
                    intent.putExtra("targetId", inputId)
                    intent.putExtra(VoipP2PActivity.ACTION, VoipP2PActivity.CALLING)
                    startActivity(intent)
                    this@VoipP2PCreateActivity.finish()
                }
            }
            R.id.btn_clean -> vTargetId!!.text = ""
            R.id.btn_0 -> vTargetId!!.append("0")
            R.id.btn_1 -> vTargetId!!.append("1")
            R.id.btn_2 -> vTargetId!!.append("2")
            R.id.btn_3 -> vTargetId!!.append("3")
            R.id.btn_4 -> vTargetId!!.append("4")
            R.id.btn_5 -> vTargetId!!.append("5")
            R.id.btn_6 -> vTargetId!!.append("6")
            R.id.btn_7 -> vTargetId!!.append("7")
            R.id.btn_8 -> vTargetId!!.append("8")
            R.id.btn_9 -> vTargetId!!.append("9")
            R.id.btn_point -> vTargetId!!.append(".")
        }
    }

    public override fun onResume() {
        super.onResume()
        (findViewById<View>(R.id.targetid_input) as TextView).text = loadIP()
    }

    private fun loadIP(): String {
        val prefer = getSharedPreferences("com.starrtc.boins", Context.MODE_PRIVATE)
        return prefer.getString("P2P_IP", "")
    }

    private fun saveIp(IP: String) {
        val prefer = getSharedPreferences("com.starrtc.boins", Context.MODE_PRIVATE)
        val editor = prefer.edit()
        editor.putString("P2P_IP", IP)
        editor.commit()
    }
}
