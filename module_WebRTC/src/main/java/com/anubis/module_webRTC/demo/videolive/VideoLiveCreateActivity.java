package com.anubis.module_webRTC.demo.videolive;

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
import com.starrtc.starrtcsdk.api.XHSDKHelper;
import com.starrtc.starrtcsdk.core.player.StarPlayer;

public class VideoLiveCreateActivity extends BaseActivity {

    private XHSDKHelper xhsdkHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.anubis.module_webRTC.R.layout.activity_video_live_create);
        ((TextView)findViewById(com.anubis.module_webRTC.R.id.title_text)).setText("创建互动直播");
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
                    MLOC.showMsg(VideoLiveCreateActivity.this,"id不能为空");
                }else{
                    xhsdkHelper.stopPerview();
                    xhsdkHelper = null;
                    Intent intent = new Intent(VideoLiveCreateActivity.this, VideoLiveActivity.class);
                    intent.putExtra(VideoLiveActivity.LIVE_TYPE,XHConstants.XHLiveType.XHLiveTypeGlobalPublic);
                    intent.putExtra(VideoLiveActivity.LIVE_NAME,inputId);
                    intent.putExtra(VideoLiveActivity.CREATER_ID,MLOC.userId);
                    startActivity(intent);
                    finish();
                }
            }
        });
        findViewById(com.anubis.module_webRTC.R.id.switch_camera).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(xhsdkHelper!=null){
                    xhsdkHelper.switchCamera();
                }
            }
        });
        xhsdkHelper = new XHSDKHelper();
        xhsdkHelper.setDefaultCameraId(1);
        xhsdkHelper.startPerview(this,((StarPlayer)findViewById(com.anubis.module_webRTC.R.id.previewPlayer)));
    }
    @Override
    public void onPause(){
        super.onPause();
        if(xhsdkHelper!=null){
            xhsdkHelper.stopPerview();
            xhsdkHelper = null;
        }
    }
}
