package com.anubis.app_nertc

/**
 * Author  ： AnubisASN   on 21-6-5 上午11:28.
 * E-mail  ： anubisasn@gmail.com ( anubisasn@qq.com )
 *  Q Q： 773506352
 *命名规则定义：
 *Module :  module_'ModuleName'
 *Library :  lib_'LibraryName'
 *Package :  'PackageName'_'Module'
 *Class :  'Mark'_'Function'_'Tier'
 *Layout :  'Module'_'Function'
 *Resource :  'Module'_'ResourceName'_'Mark'
 *Layout Id :  'LoayoutName'_'Widget'_'FunctionName'
 *Class Id :  'LoayoutName'_'Widget'+'FunctionName'
 *Router :  /'Module'/'Function'
 *说明：
 */
import android.app.Activity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.anubis.app_nertc.Utils.getToken
import com.anubis.module_face_recognition.eFaceRe
import com.netease.lava.nertc.sdk.NERtc
import kotlinx.android.synthetic.main.activity_main.*


//  Created by NetEase on 7/31/20.
//  Copyright (c) 2014-2020 NetEase, Inc. All rights reserved.
//
class MainActivity : AppCompatActivity() {
    private var hintTv: TextView? = null
    private var roomIdEt: EditText? = null
    private var clearInputImg: ImageView? = null
    private var joinBtn: Button? = null

    private var mFaceRe: eFaceRe? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initViews()
        requestPermissionsIfNeeded()


    }

    private fun requestPermissionsIfNeeded() {
        val missedPermissions = NERtc.checkPermission(this)
        if (missedPermissions.size > 0) {
            ActivityCompat.requestPermissions(this, missedPermissions.toTypedArray(), PERMISSION_REQUEST_CODE)
        }
    }

    private fun initViews() {
        hintTv = findViewById(R.id.tv_hint)
        roomIdEt = findViewById(R.id.et_room_id)
        clearInputImg = findViewById(R.id.img_clear_input)
        joinBtn = findViewById(R.id.btn_join)

        // 输入框为空时才显示清除内容的图标
        roomIdEt?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                clearInputImg?.setVisibility(if (s == null || s.length <= 0) View.GONE else View.VISIBLE)
            }
        })
        clearInputImg?.setOnClickListener(View.OnClickListener { v: View? -> roomIdEt?.setText("") })
        joinBtn?.setOnClickListener(View.OnClickListener { v: View? ->
            val roomIdEdit = roomIdEt?.getText()
            if (roomIdEdit == null || roomIdEt?.length()!! <= 0) {
                hintTv?.setVisibility(View.VISIBLE)
                return@OnClickListener
            }
            hintTv?.setVisibility(View.GONE)
            val userId = et_user_id.text.toString().toLong()
            getToken(userId) {
                MeetingActivity.startActivity(this, roomIdEt?.getText().toString(), userId)
            }

            hideSoftKeyboard()
        })
    }

    private fun hideSoftKeyboard() {
        if (currentFocus == null) return
        val imm = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
                ?: return
        imm.hideSoftInputFromWindow(currentFocus.windowToken, 0)
    }

    companion object {
        private const val PERMISSION_REQUEST_CODE = 100
    }
}
