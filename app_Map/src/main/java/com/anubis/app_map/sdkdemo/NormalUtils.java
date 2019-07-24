/*
 * Copyright (C) 2018 Baidu, Inc. All Rights Reserved.
 */
package com.anubis.app_map.sdkdemo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.anubis.app_map.sdkdemo.activity.DemoDrivingActivity;
import com.anubis.app_map.sdkdemo.activity.DemoNaviSettingActivity;

public class NormalUtils {

    public static void gotoSettings(Activity activity) {
        Intent it = new Intent(activity, DemoNaviSettingActivity.class);
        activity.startActivity(it);
    }

    public static void gotoDriving(Activity activity) {
        Intent it = new Intent(activity, DemoDrivingActivity.class);
        activity.startActivity(it);
    }

    public static String getTTSAppID() {
        return "11213224";
    }

    public static int dip2px(Context context, float dipValue){
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int)(dipValue * scale + 0.5f);
    }
}
