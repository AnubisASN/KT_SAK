package com.anubis.module_webRTC.demo.setting

import androidx.appcompat.app.AppCompatActivity
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.EditText
import android.widget.TextView

import com.anubis.module_webRTC.demo.MLOC
import com.anubis.module_webRTC.demo.service.FloatWindowsService
import com.anubis.module_webRTC.demo.service.KeepLiveService
import com.anubis.module_webRTC.R
import com.starrtc.starrtcsdk.api.XHClient

class SetupServerHostActivity : androidx.appcompat.app.AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.anubis.module_webRTC.R.layout.activity_setup_server_host)
        findViewById<View>(com.anubis.module_webRTC.R.id.title_left_btn).visibility = View.VISIBLE
        findViewById<View>(com.anubis.module_webRTC.R.id.title_left_btn).setOnClickListener { finish() }
        (findViewById<View>(com.anubis.module_webRTC.R.id.title_text) as TextView).text = "服务器配置"

        findViewById<View>(com.anubis.module_webRTC.R.id.btn).setOnClickListener {
            val user_id = (findViewById<View>(com.anubis.module_webRTC.R.id.user_id) as EditText).text.toString().trim { it <= ' ' }
            if (!TextUtils.isEmpty(user_id)) {
                MLOC.saveUserId(user_id)
            }
            val voip_server = (findViewById<View>(com.anubis.module_webRTC.R.id.voip_server) as EditText).text.toString().trim { it <= ' ' }
            if (!TextUtils.isEmpty(voip_server)) {
                MLOC.saveVoipServerUrl(voip_server)
            }
            val im_server = (findViewById<View>(com.anubis.module_webRTC.R.id.im_server) as EditText).text.toString().trim { it <= ' ' }
            if (!TextUtils.isEmpty(im_server)) {
                MLOC.saveImServerUrl(im_server)
            }
            val chatroom_server = (findViewById<View>(com.anubis.module_webRTC.R.id.chatroom_server) as EditText).text.toString().trim { it <= ' ' }
            if (!TextUtils.isEmpty(chatroom_server)) {
                MLOC.saveChatroomServerUrl(chatroom_server)
            }
            val src_server = (findViewById<View>(com.anubis.module_webRTC.R.id.src_server) as EditText).text.toString().trim { it <= ' ' }
            if (!TextUtils.isEmpty(src_server)) {
                MLOC.saveSrcServerUrl(src_server)
            }
            val vdn_server = (findViewById<View>(com.anubis.module_webRTC.R.id.vdn_server) as EditText).text.toString().trim { it <= ' ' }
            if (!TextUtils.isEmpty(vdn_server)) {
                MLOC.saveVdnServerUrl(vdn_server)
            }
            val proxy_server = (findViewById<View>(com.anubis.module_webRTC.R.id.proxy_server) as EditText).text.toString().trim { it <= ' ' }
            if (!TextUtils.isEmpty(proxy_server)) {
                MLOC.saveProxyServerUrl(proxy_server)
            }

            val imGroupListUrl = (findViewById<View>(com.anubis.module_webRTC.R.id.im_group_list) as EditText).text.toString().trim { it <= ' ' }
            if (!TextUtils.isEmpty(imGroupListUrl)) {
                MLOC.saveImGroupListUrl(imGroupListUrl)
            }
            val imGroupInfoUrl = (findViewById<View>(com.anubis.module_webRTC.R.id.im_group_info) as EditText).text.toString().trim { it <= ' ' }
            if (!TextUtils.isEmpty(imGroupInfoUrl)) {
                MLOC.saveImGroupInfoUrl(imGroupInfoUrl)
            }
            val listSaveUrl = (findViewById<View>(com.anubis.module_webRTC.R.id.list_save) as EditText).text.toString().trim { it <= ' ' }
            if (!TextUtils.isEmpty(listSaveUrl)) {
                MLOC.saveListSaveUrl(listSaveUrl)
            }
            val listDeleteUrl = (findViewById<View>(com.anubis.module_webRTC.R.id.list_delete) as EditText).text.toString().trim { it <= ' ' }
            if (!TextUtils.isEmpty(listDeleteUrl)) {
                MLOC.saveListDeleteUrl(listDeleteUrl)
            }
            val listQueryUrl = (findViewById<View>(com.anubis.module_webRTC.R.id.list_query) as EditText).text.toString().trim { it <= ' ' }
            if (!TextUtils.isEmpty(listQueryUrl)) {
                MLOC.saveListQueryUrl(listQueryUrl)
            }

            XHClient.getInstance().loginManager.logout()
            stopService(Intent(this@SetupServerHostActivity, KeepLiveService::class.java))
            stopService(Intent(this@SetupServerHostActivity, FloatWindowsService::class.java))
            startService(Intent(this@SetupServerHostActivity, KeepLiveService::class.java))
            finish()
        }

    }

    public override fun onResume() {
        super.onResume()
        (findViewById<View>(com.anubis.module_webRTC.R.id.user_id) as EditText).setText(MLOC.userId)
        (findViewById<View>(com.anubis.module_webRTC.R.id.voip_server) as EditText).setText(MLOC.VOIP_SERVER_URL)
        (findViewById<View>(com.anubis.module_webRTC.R.id.im_server) as EditText).setText(MLOC.IM_SERVER_URL)
        (findViewById<View>(com.anubis.module_webRTC.R.id.chatroom_server) as EditText).setText(MLOC.CHATROOM_SERVER_URL)
        (findViewById<View>(com.anubis.module_webRTC.R.id.src_server) as EditText).setText(MLOC.LIVE_SRC_SERVER_URL)
        (findViewById<View>(com.anubis.module_webRTC.R.id.vdn_server) as EditText).setText(MLOC.LIVE_VDN_SERVER_URL)
        (findViewById<View>(com.anubis.module_webRTC.R.id.proxy_server) as EditText).setText(MLOC.LIVE_PROXY_SERVER_URL)

        (findViewById<View>(com.anubis.module_webRTC.R.id.im_group_list) as EditText).setText(MLOC.IM_GROUP_LIST_URL)
        (findViewById<View>(com.anubis.module_webRTC.R.id.im_group_info) as EditText).setText(MLOC.IM_GROUP_INFO_URL)
        (findViewById<View>(com.anubis.module_webRTC.R.id.list_save) as EditText).setText(MLOC.LIST_SAVE_URL)
        (findViewById<View>(com.anubis.module_webRTC.R.id.list_delete) as EditText).setText(MLOC.LIST_DELETE_URL)
        (findViewById<View>(com.anubis.module_webRTC.R.id.list_query) as EditText).setText(MLOC.LIST_QUERY_URL)
    }
}
