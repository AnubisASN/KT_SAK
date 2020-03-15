package com.anubis.module_webRTC.demo.im.c2c;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.anubis.module_webRTC.demo.BaseActivity;
import com.anubis.module_webRTC.demo.MLOC;


public class C2CCreateActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.anubis.module_webRTC.R.layout.activity_c2c_create);
        ((TextView)findViewById(com.anubis.module_webRTC.R.id.title_text)).setText("创建新会话");
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
                    MLOC.INSTANCE.showMsg(C2CCreateActivity.this,"id不能为空");
                }else{
                    MLOC.INSTANCE.saveC2CUserId(C2CCreateActivity.this,inputId);
                    Intent intent = new Intent(C2CCreateActivity.this,C2CActivity.class);
                    intent.putExtra("targetId",inputId);
                    startActivity(intent);
                    finish();
                }
            }
        });
    }
}
