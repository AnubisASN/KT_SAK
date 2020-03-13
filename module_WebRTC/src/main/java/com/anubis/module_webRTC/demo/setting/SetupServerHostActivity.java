package com.anubis.module_webRTC.demo.setting;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.anubis.module_webRTC.demo.MLOC;
import com.anubis.module_webRTC.demo.service.FloatWindowsService;
import com.anubis.module_webRTC.demo.service.KeepLiveService;
import com.anubis.module_webRTC.R;
import com.anubis.module_webRTC.demo.MLOC;
import com.anubis.module_webRTC.demo.service.FloatWindowsService;
import com.anubis.module_webRTC.demo.service.KeepLiveService;
import com.starrtc.starrtcsdk.api.XHClient;

public class SetupServerHostActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.anubis.module_webRTC.R.layout.activity_setup_server_host);
        findViewById(com.anubis.module_webRTC.R.id.title_left_btn).setVisibility(View.VISIBLE);
        findViewById(com.anubis.module_webRTC.R.id.title_left_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        ((TextView)findViewById(com.anubis.module_webRTC.R.id.title_text)).setText("服务器配置");

        findViewById(com.anubis.module_webRTC.R.id.btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String user_id = ((EditText)findViewById(com.anubis.module_webRTC.R.id.user_id)).getText().toString().trim();
                if(!TextUtils.isEmpty(user_id)){
                    MLOC.saveUserId(user_id);
                }
                String voip_server = ((EditText)findViewById(com.anubis.module_webRTC.R.id.voip_server)).getText().toString().trim();
                if(!TextUtils.isEmpty(voip_server)){
                    MLOC.saveVoipServerUrl(voip_server);
                }
                String im_server = ((EditText)findViewById(com.anubis.module_webRTC.R.id.im_server)).getText().toString().trim();
                if(!TextUtils.isEmpty(im_server)){
                    MLOC.saveImServerUrl(im_server);
                }
                String chatroom_server = ((EditText)findViewById(com.anubis.module_webRTC.R.id.chatroom_server)).getText().toString().trim();
                if(!TextUtils.isEmpty(chatroom_server)){
                    MLOC.saveChatroomServerUrl(chatroom_server);
                }
                String src_server = ((EditText)findViewById(com.anubis.module_webRTC.R.id.src_server)).getText().toString().trim();
                if(!TextUtils.isEmpty(src_server)){
                    MLOC.saveSrcServerUrl(src_server);
                }
                String vdn_server = ((EditText)findViewById(com.anubis.module_webRTC.R.id.vdn_server)).getText().toString().trim();
                if(!TextUtils.isEmpty(vdn_server)){
                    MLOC.saveVdnServerUrl(vdn_server);
                }
                String proxy_server = ((EditText)findViewById(com.anubis.module_webRTC.R.id.proxy_server)).getText().toString().trim();
                if(!TextUtils.isEmpty(proxy_server)){
                    MLOC.saveProxyServerUrl(proxy_server);
                }

                String imGroupListUrl = ((EditText)findViewById(com.anubis.module_webRTC.R.id.im_group_list)).getText().toString().trim();
                if(!TextUtils.isEmpty(imGroupListUrl)){
                    MLOC.saveImGroupListUrl(imGroupListUrl);
                }
                String imGroupInfoUrl = ((EditText)findViewById(com.anubis.module_webRTC.R.id.im_group_info)).getText().toString().trim();
                if(!TextUtils.isEmpty(imGroupInfoUrl)){
                    MLOC.saveImGroupInfoUrl(imGroupInfoUrl);
                }
                String listSaveUrl = ((EditText)findViewById(com.anubis.module_webRTC.R.id.list_save)).getText().toString().trim();
                if(!TextUtils.isEmpty(listSaveUrl)){
                    MLOC.saveListSaveUrl(listSaveUrl);
                }
                String listDeleteUrl = ((EditText)findViewById(com.anubis.module_webRTC.R.id.list_delete)).getText().toString().trim();
                if(!TextUtils.isEmpty(listDeleteUrl)){
                    MLOC.saveListDeleteUrl(listDeleteUrl);
                }
                String listQueryUrl = ((EditText)findViewById(com.anubis.module_webRTC.R.id.list_query)).getText().toString().trim();
                if(!TextUtils.isEmpty(listQueryUrl)){
                    MLOC.saveListQueryUrl(listQueryUrl);
                }

                XHClient.getInstance().getLoginManager().logout();
                stopService(new Intent(SetupServerHostActivity.this, KeepLiveService.class));
                stopService(new Intent(SetupServerHostActivity.this, FloatWindowsService.class));
                startService(new Intent(SetupServerHostActivity.this, KeepLiveService.class));
                finish();
            }
        });

    }
    @Override
    public void onResume(){
        super.onResume();
        ((EditText)findViewById(com.anubis.module_webRTC.R.id.user_id)).setText(MLOC.userId);
        ((EditText)findViewById(com.anubis.module_webRTC.R.id.voip_server)).setText(MLOC.VOIP_SERVER_URL);
        ((EditText)findViewById(com.anubis.module_webRTC.R.id.im_server)).setText(MLOC.IM_SERVER_URL);
        ((EditText)findViewById(com.anubis.module_webRTC.R.id.chatroom_server)).setText(MLOC.CHATROOM_SERVER_URL);
        ((EditText)findViewById(com.anubis.module_webRTC.R.id.src_server)).setText(MLOC.LIVE_SRC_SERVER_URL);
        ((EditText)findViewById(com.anubis.module_webRTC.R.id.vdn_server)).setText(MLOC.LIVE_VDN_SERVER_URL);
        ((EditText)findViewById(com.anubis.module_webRTC.R.id.proxy_server)).setText(MLOC.LIVE_PROXY_SERVER_URL);

        ((EditText)findViewById(com.anubis.module_webRTC.R.id.im_group_list)).setText(MLOC.IM_GROUP_LIST_URL);
        ((EditText)findViewById(com.anubis.module_webRTC.R.id.im_group_info)).setText(MLOC.IM_GROUP_INFO_URL);
        ((EditText)findViewById(com.anubis.module_webRTC.R.id.list_save)).setText(MLOC.LIST_SAVE_URL);
        ((EditText)findViewById(com.anubis.module_webRTC.R.id.list_delete)).setText(MLOC.LIST_DELETE_URL);
        ((EditText)findViewById(com.anubis.module_webRTC.R.id.list_query)).setText(MLOC.LIST_QUERY_URL);
    }
}
