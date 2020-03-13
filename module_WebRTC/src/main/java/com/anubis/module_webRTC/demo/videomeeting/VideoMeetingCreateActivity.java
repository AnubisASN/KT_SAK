package com.anubis.module_webRTC.demo.videomeeting;

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
import com.starrtc.starrtcsdk.api.XHConstants;

public class VideoMeetingCreateActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.anubis.module_webRTC.R.layout.activity_video_meeting_create);
        ((TextView)findViewById(com.anubis.module_webRTC.R.id.title_text)).setText("创建视频会议");
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
                if(TextUtils.isEmpty(inputId)){
                    MLOC.showMsg(VideoMeetingCreateActivity.this,"id不能为空");
                }else{
                    Intent intent = new Intent(VideoMeetingCreateActivity.this, VideoMeetingActivity.class);
                    intent.putExtra(VideoMeetingActivity.MEETING_NAME,inputId);
                    intent.putExtra(VideoMeetingActivity.MEETING_CREATER,MLOC.userId);
                    intent.putExtra(VideoMeetingActivity.MEETING_TYPE,XHConstants.XHMeetingType.XHMeetingTypeGlobalPublic);
                    startActivity(intent);
                    finish();
                }
            }
        });
    }
}
