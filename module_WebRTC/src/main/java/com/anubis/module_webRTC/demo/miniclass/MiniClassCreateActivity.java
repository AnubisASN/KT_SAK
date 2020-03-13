package com.anubis.module_webRTC.demo.miniclass;

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

public class MiniClassCreateActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.anubis.module_webRTC.R.layout.activity_mini_class_create);
        ((TextView)findViewById(com.anubis.module_webRTC.R.id.title_text)).setText("创建小班课");
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
                    MLOC.showMsg(MiniClassCreateActivity.this,"id不能为空");
                }else{
                    Intent intent = new Intent(MiniClassCreateActivity.this, MiniClassActivity.class);
                    intent.putExtra(MiniClassActivity.CLASS_NAME,inputId);
                    intent.putExtra(MiniClassActivity.CLASS_CREATOR,MLOC.userId);
                    intent.putExtra(MiniClassActivity.CLASS_TYPE,type);
                    startActivity(intent);
                    finish();
                }
            }
        });
    }
}
