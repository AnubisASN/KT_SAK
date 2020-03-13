package com.anubis.module_webRTC.demo.audiolive;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.anubis.module_webRTC.demo.BaseActivity;
import com.anubis.module_webRTC.demo.MLOC;
import com.starrtc.starrtcsdk.api.XHConstants;

public class AudioLiveCreateActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.anubis.module_webRTC.R.layout.activity_audio_live_create);
        ((TextView)findViewById(com.anubis.module_webRTC.R.id.title_text)).setText("创建互动语音直播间");
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
                XHConstants.XHLiveType type = XHConstants.XHLiveType.XHLiveTypeGlobalPublic;

                if(TextUtils.isEmpty(inputId)){
                    MLOC.showMsg(AudioLiveCreateActivity.this,"id不能为空");
                }else{
                    Intent intent = new Intent(AudioLiveCreateActivity.this, AudioLiveActivity.class);
                    intent.putExtra(AudioLiveActivity.LIVE_TYPE,type);
                    intent.putExtra(AudioLiveActivity.LIVE_NAME,inputId);
                    intent.putExtra(AudioLiveActivity.CREATER_ID,MLOC.userId);
                    startActivity(intent);
                    finish();
                }
            }
        });
    }
}
