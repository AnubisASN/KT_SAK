package com.anubis.module_webRTC.demo.miniclass

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.EditText
import android.widget.TextView

import com.anubis.module_webRTC.demo.BaseActivity
import com.anubis.module_webRTC.demo.MLOC
import com.anubis.module_webRTC.R
import com.starrtc.starrtcsdk.api.XHConstants
import demo.miniclass.MiniClassActivity

class MiniClassCreateActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.anubis.module_webRTC.R.layout.activity_mini_class_create)
        (findViewById<View>(com.anubis.module_webRTC.R.id.title_text) as TextView).text = "创建小班课"
        findViewById<View>(com.anubis.module_webRTC.R.id.title_left_btn).visibility = View.VISIBLE
        findViewById<View>(com.anubis.module_webRTC.R.id.title_left_btn).setOnClickListener { finish() }
        findViewById<View>(com.anubis.module_webRTC.R.id.yes_btn).setOnClickListener {
            val inputId = (findViewById<View>(com.anubis.module_webRTC.R.id.targetid_input) as EditText).text.toString()
            val type = XHConstants.XHLiveType.XHLiveTypeGlobalPublic

            if (TextUtils.isEmpty(inputId)) {
                MLOC.showMsg(this@MiniClassCreateActivity, "id不能为空")
            } else {
                val intent = Intent(this@MiniClassCreateActivity, MiniClassActivity::class.java)
                intent.putExtra(MiniClassActivity.CLASS_NAME, inputId)
                intent.putExtra(MiniClassActivity.CLASS_CREATOR, MLOC.userId)
                intent.putExtra(MiniClassActivity.CLASS_TYPE, type)
                startActivity(intent)
                finish()
            }
        }
    }
}
