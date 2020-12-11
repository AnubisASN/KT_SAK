/*
 * Copyright (C) 2018 Baidu, Inc. All Rights Reserved.
 */
package com.anubis.module_navi;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.content.Intent;
import com.anubis.module_navi.activity.DemoNaviSettingActivity;

public class NormalUtils {

    public static void gotoSettings(AppCompatActivity activity) {
        Intent it = new Intent(activity, DemoNaviSettingActivity.class);
        activity.startActivity(it);
    }

    public static void gotoDriving(AppCompatActivity activity) {

    }

    public static String getTTSAppID() {
        return "11213224";
    }

    public static int dip2px(Context context, float dipValue){
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int)(dipValue * scale + 0.5f);
    }
}
