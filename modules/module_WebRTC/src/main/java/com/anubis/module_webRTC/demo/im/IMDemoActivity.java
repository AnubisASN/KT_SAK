package com.anubis.module_webRTC.demo.im;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.anubis.module_webRTC.demo.BaseActivity;
import com.anubis.module_webRTC.demo.im.c2c.C2CListActivity;
import com.anubis.module_webRTC.demo.im.chatroom.ChatroomListActivity;
import com.anubis.module_webRTC.demo.im.group.MessageGroupListActivity;
import com.anubis.module_webRTC.R;
import com.anubis.module_webRTC.demo.BaseActivity;
import com.anubis.module_webRTC.demo.MLOC;
import com.anubis.module_webRTC.demo.im.c2c.C2CListActivity;
import com.anubis.module_webRTC.demo.im.chatroom.ChatroomListActivity;
import com.anubis.module_webRTC.demo.im.group.MessageGroupListActivity;
import com.starrtc.starrtcsdk.api.XHClient;
import com.starrtc.starrtcsdk.apiInterface.IXHResultCallback;

public class IMDemoActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.anubis.module_webRTC.R.layout.activity_imdemo);
        ((TextView)findViewById(com.anubis.module_webRTC.R.id.title_text)).setText("IM演示");
        findViewById(com.anubis.module_webRTC.R.id.title_left_btn).setVisibility(View.VISIBLE);
        findViewById(com.anubis.module_webRTC.R.id.title_left_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        findViewById(com.anubis.module_webRTC.R.id.c2c_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(IMDemoActivity.this, C2CListActivity.class));
            }
        });
        findViewById(com.anubis.module_webRTC.R.id.chatroom_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(IMDemoActivity.this, ChatroomListActivity.class));
            }
        });
        findViewById(com.anubis.module_webRTC.R.id.group_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(IMDemoActivity.this, MessageGroupListActivity.class));
            }
        });
        XHClient.getInstance().getAliveUserNum(new IXHResultCallback() {
            @Override
            public void success(Object data) {
                MLOC.INSTANCE.d("!!!!!!!!!!!!!",data.toString());
            }

            @Override
            public void failed(String errMsg) {
                MLOC.INSTANCE.d("!!!!!!!!!!!!!",errMsg.toString());
            }
        });
        XHClient.getInstance().getAliveUserList(1,new IXHResultCallback() {
            @Override
            public void success(Object data) {
                MLOC.INSTANCE.d("!!!!!!!!!!!!!",data.toString());
            }

            @Override
            public void failed(String errMsg) {
                MLOC.INSTANCE.d("!!!!!!!!!!!!!",errMsg.toString());
            }
        });
    }

    @Override
    public void onResume(){
        super.onResume();
        findViewById(com.anubis.module_webRTC.R.id.c2c_new).setVisibility(MLOC.INSTANCE.getHasNewC2CMsg() ?View.VISIBLE:View.INVISIBLE);
        findViewById(com.anubis.module_webRTC.R.id.group_new).setVisibility(MLOC.INSTANCE.getHasNewGroupMsg() ?View.VISIBLE:View.INVISIBLE);
    }

    @Override
    public void dispatchEvent(String aEventID, boolean success, final Object eventObj) {
        super.dispatchEvent(aEventID,success,eventObj);
        onResume();
    }

}
