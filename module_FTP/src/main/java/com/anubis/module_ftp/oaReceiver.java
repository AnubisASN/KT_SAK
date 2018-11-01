package com.anubis.module_ftp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.anubis.module_ftp.GUI.eFTPUI;


/**
 * Description: app在系统打开时自动开启的广播
 * AUTHOR: Champion Dragon
 * created at 2018/1/24
 **/

public class oaReceiver extends BroadcastReceiver{
    public oaReceiver() {

    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            Intent i = new Intent(context, eFTPUI.class);
//            非常重要，如果缺少的话，程序将在启动时报错
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(i);
        }
    }
}
