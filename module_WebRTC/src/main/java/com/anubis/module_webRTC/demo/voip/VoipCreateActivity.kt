package com.anubis.module_webRTC.demo.voip

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.EditText
import android.widget.TextView

import com.anubis.module_webRTC.R
import com.anubis.module_webRTC.demo.BaseActivity
import com.anubis.module_webRTC.demo.MLOC

class VoipCreateActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_voip_create)
        (findViewById<View>(R.id.title_text) as TextView).text = "创建新会话"
        findViewById<View>(R.id.title_left_btn).visibility = View.VISIBLE
        findViewById<View>(R.id.title_left_btn).setOnClickListener { finish() }
        findViewById<View>(R.id.yes_btn).setOnClickListener {
            val inputId = (findViewById<View>(R.id.targetid_input) as EditText).text.toString()
            if (TextUtils.isEmpty(inputId)) {
                MLOC.showMsg(this@VoipCreateActivity, "id不能为空")
            } else {
                val builder = AlertDialog.Builder(this@VoipCreateActivity)
                builder.setItems(arrayOf("视频通话", "音频通话")) { dialogInterface, i ->
                    if (i == 0) {
                        val intent = Intent(this@VoipCreateActivity, VoipActivity::class.java)
                        intent.putExtra("targetId", inputId)
                        intent.putExtra(VoipActivity.ACTION, VoipActivity.CALLING)
                        startActivity(intent)
                        this@VoipCreateActivity.finish()
                    } else if (i == 1) {
                        val intent = Intent(this@VoipCreateActivity, VoipAudioActivity::class.java)
                        intent.putExtra("targetId", inputId)
                        intent.putExtra(VoipActivity.ACTION, VoipAudioActivity.CALLING)
                        startActivity(intent)
                        this@VoipCreateActivity.finish()
                    }
                }
                builder.setCancelable(true)
                val dialog = builder.create()
                dialog.show()
            }
        }
    }
}
