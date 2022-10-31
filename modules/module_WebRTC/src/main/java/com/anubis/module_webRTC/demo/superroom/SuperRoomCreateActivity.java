package com.anubis.module_webRTC.demo.superroom;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.anubis.module_webRTC.demo.BaseActivity;
import com.anubis.module_webRTC.demo.MLOC;
import com.anubis.module_webRTC.R;
import com.anubis.module_webRTC.demo.BaseActivity;
import com.anubis.module_webRTC.demo.MLOC;
import com.anubis.module_webRTC.demo.audiolive.AudioLiveActivity;
import com.starrtc.starrtcsdk.api.XHConstants;

public class SuperRoomCreateActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.anubis.module_webRTC.R.layout.activity_super_room_create);
        ((TextView)findViewById(com.anubis.module_webRTC.R.id.title_text)).setText("创建对讲机房间");
        findViewById(com.anubis.module_webRTC.R.id.title_left_btn).setVisibility(View.VISIBLE);
        findViewById(com.anubis.module_webRTC.R.id.title_left_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        findViewById(com.anubis.module_webRTC.R.id.yes_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String inputId = ((EditText)findViewById(com.anubis.module_webRTC.R.id.targetid_input)).getText().toString();
                XHConstants.XHSuperRoomType type = XHConstants.XHSuperRoomType.XHSuperRoomTypeGlobalPublic;
                if(TextUtils.isEmpty(inputId)){
                    MLOC.INSTANCE.showMsg(SuperRoomCreateActivity.this,"id不能为空");
                }else{
                    Intent intent = new Intent(SuperRoomCreateActivity.this, SuperRoomActivity.class);
                    intent.putExtra(AudioLiveActivity.LIVE_TYPE,type);
                    intent.putExtra(AudioLiveActivity.LIVE_NAME,inputId);
                    intent.putExtra(AudioLiveActivity.CREATER_ID, MLOC.INSTANCE.getUserId());
                    startActivity(intent);
                    finish();
                }
            }
        });
    }
}
