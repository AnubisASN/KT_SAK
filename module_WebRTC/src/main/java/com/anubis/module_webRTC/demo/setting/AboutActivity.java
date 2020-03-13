package com.anubis.module_webRTC.demo.setting;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.anubis.module_webRTC.R;
import com.anubis.module_webRTC.demo.BaseActivity;
import com.starrtc.starrtcsdk.api.XHClient;

public class AboutActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        findViewById(R.id.title_left_btn).setVisibility(View.VISIBLE);
        findViewById(R.id.title_left_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        ((TextView)findViewById(R.id.title_text)).setText("关于");
        ((TextView)findViewById(R.id.version)).setText(XHClient.getVersion());
    }
}
