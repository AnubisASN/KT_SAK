/*
 * Copyright (C) 2018 Baidu, Inc. All Rights Reserved.
 */
package com.anubis.module_navi;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.anubis.module_navi.activity.DemoDrivingActivity;
import com.anubis.module_navi.activity.DemoExtGpsActivity;
import com.anubis.module_navi.activity.DemoGuideActivity;
import com.anubis.module_navi.activity.DemoMainActivity;
import com.baidu.navisdk.adapter.BNRoutePlanNode;
import com.baidu.navisdk.adapter.BNRoutePlanNode.CoordinateType;
import com.baidu.navisdk.adapter.BNaviCommonParams;
import com.baidu.navisdk.adapter.BaiduNaviManagerFactory;
import com.baidu.navisdk.adapter.IBNRoutePlanManager;
import com.baidu.navisdk.adapter.IBaiduNaviManager;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static android.content.Context.LOCATION_SERVICE;

public class eNavigate   {

    private  static eNavigate init;

    private eNavigate(){}

public  static synchronized   eNavigate eGetInit(){
        if (init==null)
            init=new eNavigate();
    return init;
}
    private static final String APP_FOLDER_NAME = "BNSDKSimpleDemo";

    static final String ROUTE_PLAN_NODE = "routePlanNode";

    private static final int NORMAL = 0;
    private static final int EXTERNAL = 1;

    private static final String[] authBaseArr = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_FINE_LOCATION
    };

    private Button mWgsNaviBtn = null;
    private Button mGcjNaviBtn = null;
    private Button mBdmcNaviBtn = null;
    private Button mSzNaviBtn = null;
    private Button mBjNaviBtn = null;
    private Button mCustomNaviBtn = null;
    private Button mDb06ll = null;
    private Button mGotoSettingsBtn = null;
    private Button mExternalBtn = null;
    private Button mDrivingBtn = null;
    private String mSDCardPath = null;

    private static final int authBaseRequestCode = 1;

    private boolean hasInitSuccess = false;

    private BNRoutePlanNode mStartNode = null;
    private double mCurrentLat;
    private double mCurrentLng;
    private LocationManager mLocationManager;
    private LocationListener mLocationListener = new LocationListener() {

        @Override
        public void onLocationChanged(Location location) {
            mCurrentLat = location.getLatitude();
            mCurrentLng = location.getLongitude();
            Log.i("TAG","mCurrentLat:"+mCurrentLat+"--mCurrentLng:"+mCurrentLng);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };


     public eNavigate  eInit(Activity activity) {
        if (initDirs()) {
            initNavi(activity);
        }
        initLocation(activity);
         return  init;
    }

    private void initLocation(Activity activity) {
        mLocationManager = (LocationManager) activity.getSystemService(LOCATION_SERVICE);
        if (mLocationManager != null) {
            if (ActivityCompat.checkSelfPermission(activity, Manifest.permission
                    .ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat
                    .checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(activity, "没有权限", Toast.LENGTH_SHORT).show();
                return;
            }
            mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                    1000, 1000, mLocationListener);
        }
    }


    public void eDestroy() {
        if (mLocationManager != null) {
            mLocationManager.removeUpdates(mLocationListener);
        }
    }
// .latitude(sNodeValue[0])
//                .longitude(sNodeValue[1])
     public void eStart(Activity activity,double[] sNode,double[] eNode) {

//        if (mDrivingBtn != null) {
//            mDrivingBtn.setOnClickListener(new OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    if (BaiduNaviManagerFactory.getBaiduNaviManager().isInited()) {
//                        NormalUtils.gotoDriving(DemoMainActivity.this);
//                    }
//                }
//            });
//        }

                    if (BaiduNaviManagerFactory.getBaiduNaviManager().isInited()) {
                        Intent it = new Intent(activity, DemoDrivingActivity.class);
                        it.putExtra("sNode",sNode);
                        it.putExtra("eNode",eNode);
                        activity.startActivity(it);
                    }
    }

    private boolean checkValid(String startPoint, String endPoint) {
        if (TextUtils.isEmpty(startPoint) || TextUtils.isEmpty(endPoint)) {
            return false;
        }

        if (!startPoint.contains(",") || !endPoint.contains(",")) {
            return false;
        }
        return true;
    }

    private boolean initDirs() {
        mSDCardPath = getSdcardDir();
        if (mSDCardPath == null) {
            return false;
        }
        File f = new File(mSDCardPath, APP_FOLDER_NAME);
        if (!f.exists()) {
            try {
                f.mkdir();
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
        return true;
    }

    private boolean hasBasePhoneAuth(Activity activity) {
        PackageManager pm = activity.getPackageManager();
        for (String auth : authBaseArr) {
            if (pm.checkPermission(auth, activity.getPackageName()) != PackageManager
                    .PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    private void initNavi(final Activity activity) {
        // 申请权限
        if (android.os.Build.VERSION.SDK_INT >= 23) {
            if (!hasBasePhoneAuth(activity)) {
                activity.requestPermissions(authBaseArr, authBaseRequestCode);
                return;
            }
        }

        if (BaiduNaviManagerFactory.getBaiduNaviManager().isInited()) {
            hasInitSuccess = true;
            return;
        }

        BaiduNaviManagerFactory.getBaiduNaviManager().init(activity,
                mSDCardPath, APP_FOLDER_NAME, new IBaiduNaviManager.INaviInitListener() {

                    @Override
                    public void onAuthResult(int status, String msg) {
                        String result;
                        if (0 == status) {
                            result = "key校验成功!";
                        } else {
                            result = "key校验失败, " + msg;
                        }
                        Toast.makeText(activity, result, Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void initStart() {
                        Toast.makeText(activity.getApplicationContext(),
                                "百度导航引擎初始化开始", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void initSuccess() {
                        Toast.makeText(activity.getApplicationContext(),
                                "百度导航引擎初始化成功", Toast.LENGTH_SHORT).show();
                        hasInitSuccess = true;
                        // 初始化tts
                        initTTS(activity);
                    }

                    @Override
                    public void initFailed(int errCode) {
                        Toast.makeText(activity.getApplicationContext(),
                                "百度导航引擎初始化失败 " + errCode, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private String getSdcardDir() {
        if (Environment.getExternalStorageState().equalsIgnoreCase(Environment.MEDIA_MOUNTED)) {
            return Environment.getExternalStorageDirectory().toString();
        }
        return null;
    }

    private void initTTS(Activity activity) {
        // 使用内置TTS
        BaiduNaviManagerFactory.getTTSManager().initTTS(activity.getApplicationContext(),
                getSdcardDir(), APP_FOLDER_NAME, NormalUtils.getTTSAppID());

    }


}
