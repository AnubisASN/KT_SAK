package com.anubis.module_webRTC.demo;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.anubis.module_webRTC.demo.im.IMDemoActivity;
import com.anubis.module_webRTC.demo.setting.SettingActivity;
import com.anubis.module_webRTC.demo.voip.VoipListActivity;
import com.anubis.module_webRTC.R;
import com.anubis.module_webRTC.demo.im.IMDemoActivity;
import com.anubis.module_webRTC.demo.miniclass.MiniClassListActivity;
import com.anubis.module_webRTC.demo.setting.SettingActivity;
import com.anubis.module_webRTC.demo.superroom.SuperRoomListActivity;
import com.anubis.module_webRTC.demo.videolive.VideoLiveListActivity;
import com.anubis.module_webRTC.demo.videomeeting.VideoMeetingListActivity;
import com.anubis.module_webRTC.demo.voip.VoipListActivity;
import com.starrtc.starrtcsdk.api.XHClient;


public class StarAvDemoActivity extends BaseActivity implements View.OnClickListener {

    private boolean isOnline = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.anubis.module_webRTC.R.layout.activity_star_rtc_main);
        ((TextView)findViewById(com.anubis.module_webRTC.R.id.title_text)).setText(com.anubis.module_webRTC.R.string.app_name);
        MLOC.userId = MLOC.loadSharedData(getApplicationContext(),"userId");
        ((ImageView)findViewById(com.anubis.module_webRTC.R.id.userinfo_head)).setImageResource(MLOC.getHeadImage(this,MLOC.userId));
        ((TextView)findViewById(com.anubis.module_webRTC.R.id.userinfo_id)).setText(MLOC.userId);
        findViewById(com.anubis.module_webRTC.R.id.btn_main_im).setOnClickListener(this);
        findViewById(com.anubis.module_webRTC.R.id.btn_main_voip).setOnClickListener(this);
        findViewById(com.anubis.module_webRTC.R.id.btn_main_meeting).setOnClickListener(this);
        findViewById(com.anubis.module_webRTC.R.id.btn_main_live).setOnClickListener(this);
        findViewById(com.anubis.module_webRTC.R.id.btn_main_setting).setOnClickListener(this);
        findViewById(com.anubis.module_webRTC.R.id.btn_main_class).setOnClickListener(this);
        findViewById(com.anubis.module_webRTC.R.id.btn_main_audio).setOnClickListener(this);
    }

    @Override
    public void onBackPressed(){
        finish();
    }

    @Override
    public void onResume(){
        super.onResume();
        if(MLOC.hasLogout){
            finish();
            MLOC.hasLogout = false;
            return;
        }
        if(MLOC.userId==null){
            startActivity(new Intent(StarAvDemoActivity.this,SplashActivity.class));
            finish();
        }
        isOnline = XHClient.getInstance().getIsOnline();
        if(isOnline){
            findViewById(com.anubis.module_webRTC.R.id.loading).setVisibility(View.INVISIBLE);
        }else{
            findViewById(com.anubis.module_webRTC.R.id.loading).setVisibility(View.VISIBLE);
        }
        findViewById(com.anubis.module_webRTC.R.id.voip_new).setVisibility(MLOC.hasNewVoipMsg?View.VISIBLE:View.INVISIBLE);
        findViewById(com.anubis.module_webRTC.R.id.im_new).setVisibility((MLOC.hasNewC2CMsg|| MLOC.hasNewGroupMsg)?View.VISIBLE:View.INVISIBLE);
    }

    @Override
    public void onRestart(){
        super.onRestart();
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case com.anubis.module_webRTC.R.id.btn_main_voip:
                startActivity(new Intent(this,VoipListActivity.class));
                break;
            case com.anubis.module_webRTC.R.id.btn_main_meeting:
                startActivity(new Intent(this,VideoMeetingListActivity.class));
                break;
            case com.anubis.module_webRTC.R.id.btn_main_live:
                Intent intent3 = new Intent(this, VideoLiveListActivity.class);
                startActivity(intent3);
                break;
            case com.anubis.module_webRTC.R.id.btn_main_setting:
                Intent intent6 = new Intent(this, SettingActivity.class);
                startActivity(intent6);
                break;
            case com.anubis.module_webRTC.R.id.btn_main_im:
                Intent intent7= new Intent(this, IMDemoActivity.class);
                startActivity(intent7);
                break;
            case com.anubis.module_webRTC.R.id.btn_main_class:
                Intent intent8= new Intent(this, MiniClassListActivity.class);
                startActivity(intent8);
                break;
            case com.anubis.module_webRTC.R.id.btn_main_audio:
                Intent intent9= new Intent(this, SuperRoomListActivity.class);
                startActivity(intent9);
                break;
        }
    }

}
