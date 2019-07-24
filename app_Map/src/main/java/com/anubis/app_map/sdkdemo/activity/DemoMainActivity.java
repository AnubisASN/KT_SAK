/*
 * Copyright (C) 2018 Baidu, Inc. All Rights Reserved.
 */
package com.anubis.app_map.sdkdemo.activity;

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

import com.anubis.app_map.R;
import com.anubis.app_map.sdkdemo.NormalUtils;
import com.baidu.navisdk.adapter.BNRoutePlanNode;
import com.baidu.navisdk.adapter.BNRoutePlanNode.CoordinateType;
import com.baidu.navisdk.adapter.BNaviCommonParams;
import com.baidu.navisdk.adapter.BaiduNaviManagerFactory;
import com.baidu.navisdk.adapter.IBNRoutePlanManager;
import com.baidu.navisdk.adapter.IBaiduNaviManager;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class DemoMainActivity extends Activity {

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
            Toast.makeText(DemoMainActivity.this, mCurrentLat
                    + "--" + mCurrentLng, Toast.LENGTH_LONG).show();
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.normal_demo_activity_main);

        mWgsNaviBtn = findViewById(R.id.wgsNaviBtn);
        mGcjNaviBtn = findViewById(R.id.gcjNaviBtn);
        mBdmcNaviBtn = findViewById(R.id.bdmcNaviBtn);
        mDb06ll = findViewById(R.id.mDb06llNaviBtn);
        mSzNaviBtn = findViewById(R.id.szNaviBtn);
        mBjNaviBtn = findViewById(R.id.bjNaviBtn);
        mCustomNaviBtn = findViewById(R.id.customNaviBtn);
        mGotoSettingsBtn = findViewById(R.id.mGotoSettingsBtn);
        mExternalBtn = findViewById(R.id.externalBtn);
        mDrivingBtn = findViewById(R.id.drivingBtn);

        initListener();
        if (initDirs()) {
            initNavi();
        }
        initLocation();
    }

    private void initLocation() {
        mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (mLocationManager != null) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission
                    .ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat
                    .checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "没有权限", Toast.LENGTH_SHORT).show();
                return;
            }
            mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                    1000, 1000, mLocationListener);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mLocationManager != null) {
            mLocationManager.removeUpdates(mLocationListener);
        }
    }

    private void initListener() {
        if (mWgsNaviBtn != null) {
            mWgsNaviBtn.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View arg0) {
                    if (BaiduNaviManagerFactory.getBaiduNaviManager().isInited()) {
                        calRoutePlanNode(CoordinateType.WGS84);
                    }
                }

            });
        }
        if (mGcjNaviBtn != null) {
            mGcjNaviBtn.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View arg0) {
                    if (BaiduNaviManagerFactory.getBaiduNaviManager().isInited()) {
                        calRoutePlanNode(CoordinateType.GCJ02);
                    }
                }

            });
        }
        if (mBdmcNaviBtn != null) {
            mBdmcNaviBtn.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View arg0) {
                    if (BaiduNaviManagerFactory.getBaiduNaviManager().isInited()) {
                        calRoutePlanNode(CoordinateType.BD09_MC);
                    }
                }
            });
        }

        if (mDb06ll != null) {
            mDb06ll.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View arg0) {
                    if (BaiduNaviManagerFactory.getBaiduNaviManager().isInited()) {
                        calRoutePlanNode(CoordinateType.BD09LL);
                    }
                }
            });
        }

        if (mSzNaviBtn != null) {
            mSzNaviBtn.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View arg0) {
                    if (BaiduNaviManagerFactory.getBaiduNaviManager().isInited()) {
                        if (mCurrentLat == 0 && mCurrentLng == 0) {
                            return;
                        }
                        BNRoutePlanNode sNode = new BNRoutePlanNode.Builder()
                                .latitude(mCurrentLat)
                                .longitude(mCurrentLng)
                                .coordinateType(CoordinateType.WGS84)
                                .build();
                        BNRoutePlanNode eNode = new BNRoutePlanNode.Builder()
                                .latitude(22.613435)
                                .longitude(114.025550)
                                .name("深圳北站")
                                .description("深圳北站")
                                .coordinateType(CoordinateType.WGS84)
                                .build();
                        mStartNode = sNode;

                        routePlanToNavi(sNode, eNode, NORMAL);
                    }
                }
            });
        }

        if (mBjNaviBtn != null) {
            mBjNaviBtn.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View arg0) {
                    if (BaiduNaviManagerFactory.getBaiduNaviManager().isInited()) {
                        if (mCurrentLat == 0 && mCurrentLng == 0) {
                            return;
                        }
                        BNRoutePlanNode sNode = new BNRoutePlanNode.Builder()
                                .latitude(mCurrentLat)
                                .longitude(mCurrentLng)
                                .coordinateType(CoordinateType.WGS84)
                                .build();
                        BNRoutePlanNode eNode = new BNRoutePlanNode.Builder()
                                .latitude(39.908749)
                                .longitude(116.397491)
                                .name("北京天安门")
                                .description("北京天安门")
                                .coordinateType(CoordinateType.WGS84)
                                .build();
                        mStartNode = sNode;

                        routePlanToNavi(sNode, eNode, NORMAL);
                    }
                }
            });
        }

        if (mCustomNaviBtn != null) {
            mCustomNaviBtn.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View arg0) {
                    if (BaiduNaviManagerFactory.getBaiduNaviManager().isInited()) {
                        View dialogView = View.inflate(DemoMainActivity.this, R.layout
                                .dialog_node, null);
                        final EditText editStart = dialogView.findViewById(R.id.edit_start);
                        final EditText editEnd = dialogView.findViewById(R.id.edit_end);
                        new AlertDialog.Builder(DemoMainActivity.this)
                                .setView(dialogView)
                                .setPositiveButton("导航", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        String startPoint = editStart.getText().toString().trim();
                                        String endPoint = editEnd.getText().toString().trim();
                                        if (!checkValid(startPoint, endPoint)) {
                                            Toast.makeText(DemoMainActivity.this, "填写格式有误", Toast
                                                    .LENGTH_SHORT).show();
                                            return;
                                        }
                                        String[] starts = startPoint.split(",");
                                        String[] ends = endPoint.split(",");
                                        BNRoutePlanNode sNode = new BNRoutePlanNode.Builder()
                                                .latitude(Double.parseDouble(starts[1]))
                                                .longitude(Double.parseDouble(starts[0]))
                                                .coordinateType(CoordinateType.WGS84)
                                                .build();
                                        BNRoutePlanNode eNode = new BNRoutePlanNode.Builder()
                                                .latitude(Double.parseDouble(ends[1]))
                                                .longitude(Double.parseDouble(ends[0]))
                                                .coordinateType(CoordinateType.WGS84)
                                                .build();

                                        routePlanToNavi(sNode, eNode, NORMAL);
                                    }
                                })
                                .show();
                    }
                }
            });
        }

        if (mExternalBtn != null) {
            mExternalBtn.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (BaiduNaviManagerFactory.getBaiduNaviManager().isInited()) {
                        BNRoutePlanNode sNode = new BNRoutePlanNode.Builder()
                                .latitude(40.050969)
                                .longitude(116.300821)
                                .name("百度大厦")
                                .description("百度大厦")
                                .coordinateType(CoordinateType.WGS84)
                                .build();
                        BNRoutePlanNode eNode = new BNRoutePlanNode.Builder()
                                .latitude(39.908749)
                                .longitude(116.397491)
                                .name("北京天安门")
                                .description("北京天安门")
                                .coordinateType(CoordinateType.WGS84)
                                .build();
                        routePlanToNavi(sNode, eNode, EXTERNAL);
                    }
                }
            });
        }

        if (mDrivingBtn != null) {
            mDrivingBtn.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (BaiduNaviManagerFactory.getBaiduNaviManager().isInited()) {
                        NormalUtils.gotoDriving(DemoMainActivity.this);
                    }
                }
            });
        }

        if (mGotoSettingsBtn != null) {
            mGotoSettingsBtn.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (BaiduNaviManagerFactory.getBaiduNaviManager().isInited()) {
                        NormalUtils.gotoSettings(DemoMainActivity.this);
                    }
                }
            });
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

    private boolean hasBasePhoneAuth() {
        PackageManager pm = this.getPackageManager();
        for (String auth : authBaseArr) {
            if (pm.checkPermission(auth, this.getPackageName()) != PackageManager
                    .PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    private void initNavi() {
        // 申请权限
        if (android.os.Build.VERSION.SDK_INT >= 23) {
            if (!hasBasePhoneAuth()) {
                this.requestPermissions(authBaseArr, authBaseRequestCode);
                return;
            }
        }

        if (BaiduNaviManagerFactory.getBaiduNaviManager().isInited()) {
            hasInitSuccess = true;
            return;
        }

        BaiduNaviManagerFactory.getBaiduNaviManager().init(this,
                mSDCardPath, APP_FOLDER_NAME, new IBaiduNaviManager.INaviInitListener() {

                    @Override
                    public void onAuthResult(int status, String msg) {
                        String result;
                        if (0 == status) {
                            result = "key校验成功!";
                        } else {
                            result = "key校验失败, " + msg;
                        }
                        Toast.makeText(DemoMainActivity.this, result, Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void initStart() {
                        Toast.makeText(DemoMainActivity.this.getApplicationContext(),
                                "百度导航引擎初始化开始", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void initSuccess() {
                        Toast.makeText(DemoMainActivity.this.getApplicationContext(),
                                "百度导航引擎初始化成功", Toast.LENGTH_SHORT).show();
                        hasInitSuccess = true;
                        // 初始化tts
                        initTTS();
                    }

                    @Override
                    public void initFailed(int errCode) {
                        Toast.makeText(DemoMainActivity.this.getApplicationContext(),
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

    private void initTTS() {
        // 使用内置TTS
        BaiduNaviManagerFactory.getTTSManager().initTTS(getApplicationContext(),
                getSdcardDir(), APP_FOLDER_NAME, NormalUtils.getTTSAppID());

    }

    private void calRoutePlanNode(final int coType) {
        if (!hasInitSuccess) {
            Toast.makeText(DemoMainActivity.this.getApplicationContext(), "还未初始化!", Toast
                    .LENGTH_SHORT).show();
        }

        BNRoutePlanNode sNode = new BNRoutePlanNode.Builder()
                .latitude(40.05087)
                .longitude(116.30142)
                .name("百度大厦")
                .description("百度大厦")
                .coordinateType(coType)
                .build();
        BNRoutePlanNode eNode = new BNRoutePlanNode.Builder()
                .latitude(39.90882)
                .longitude(116.39750)
                .name("北京天安门")
                .description("北京天安门")
                .coordinateType(coType)
                .build();
        switch (coType) {
            case CoordinateType.GCJ02: {
                sNode = new BNRoutePlanNode.Builder()
                        .latitude(40.05087)
                        .longitude(116.30142)
                        .name("百度大厦")
                        .description("百度大厦")
                        .coordinateType(coType)
                        .build();
                eNode = new BNRoutePlanNode.Builder()
                        .latitude(39.90882)
                        .longitude(116.39750)
                        .name("北京天安门")
                        .description("北京天安门")
                        .coordinateType(coType)
                        .build();
                break;
            }
            case CoordinateType.WGS84: {
                sNode = new BNRoutePlanNode.Builder()
                        .latitude(40.050969)
                        .longitude(116.300821)
                        .name("百度大厦")
                        .description("百度大厦")
                        .coordinateType(coType)
                        .build();
                eNode = new BNRoutePlanNode.Builder()
                        .latitude(39.908749)
                        .longitude(116.397491)
                        .name("北京天安门")
                        .description("北京天安门")
                        .coordinateType(coType)
                        .build();
                break;
            }
            case CoordinateType.BD09_MC: {
                sNode = new BNRoutePlanNode.Builder()
                        .latitude(4846474)
                        .longitude(12947471)
                        .name("百度大厦")
                        .description("百度大厦")
                        .coordinateType(coType)
                        .build();
                eNode = new BNRoutePlanNode.Builder()
                        .latitude(4825947)
                        .longitude(12958160)
                        .name("北京天安门")
                        .description("北京天安门")
                        .coordinateType(coType)
                        .build();
                break;
            }
            case CoordinateType.BD09LL: {
                sNode = new BNRoutePlanNode.Builder()
                        .latitude(40.057009624099436)
                        .longitude(116.30784537597782)
                        .name("百度大厦")
                        .description("百度大厦")
                        .coordinateType(coType)
                        .build();
                eNode = new BNRoutePlanNode.Builder()
                        .latitude(39.915160800132085)
                        .longitude(116.40386525193937)
                        .name("北京天安门")
                        .description("北京天安门")
                        .coordinateType(coType)
                        .build();
                break;
            }
            default:
                break;
        }

        mStartNode = sNode;
        routePlanToNavi(sNode, eNode, NORMAL);
    }

    private void routePlanToNavi(BNRoutePlanNode sNode, BNRoutePlanNode eNode, final int from) {
        List<BNRoutePlanNode> list = new ArrayList<>();
        list.add(sNode);
        list.add(eNode);

        BaiduNaviManagerFactory.getCommonSettingManager().setCarNum(this, "粤B66666");
        BaiduNaviManagerFactory.getRoutePlanManager().routeplanToNavi(
                list,
                IBNRoutePlanManager.RoutePlanPreference.ROUTE_PLAN_PREFERENCE_DEFAULT,
                null,
                new Handler(Looper.getMainLooper()) {
                    @Override
                    public void handleMessage(Message msg) {
                        switch (msg.what) {
                            case IBNRoutePlanManager.MSG_NAVI_ROUTE_PLAN_START:
                                Toast.makeText(DemoMainActivity.this.getApplicationContext(),
                                        "算路开始", Toast.LENGTH_SHORT).show();
                                break;
                            case IBNRoutePlanManager.MSG_NAVI_ROUTE_PLAN_SUCCESS:
                                Toast.makeText(DemoMainActivity.this.getApplicationContext(),
                                        "算路成功", Toast.LENGTH_SHORT).show();
                                // 躲避限行消息
                                Bundle infoBundle = (Bundle) msg.obj;
                                if (infoBundle != null) {
                                    String info = infoBundle.getString(
                                            BNaviCommonParams.BNRouteInfoKey.TRAFFIC_LIMIT_INFO
                                    );
                                    Log.d("OnSdkDemo", "info = " + info);
                                }
                                break;
                            case IBNRoutePlanManager.MSG_NAVI_ROUTE_PLAN_FAILED:
                                Toast.makeText(DemoMainActivity.this.getApplicationContext(),
                                        "算路失败", Toast.LENGTH_SHORT).show();
                                BaiduNaviManagerFactory.getRoutePlanManager()
                                        .removeRequestByHandler(this);
                                break;
                            case IBNRoutePlanManager.MSG_NAVI_ROUTE_PLAN_TO_NAVI:
                                Toast.makeText(DemoMainActivity.this.getApplicationContext(),
                                        "算路成功准备进入导航", Toast.LENGTH_SHORT).show();

                                Intent intent = null;
                                if (from == NORMAL) {
                                    intent = new Intent(DemoMainActivity.this,
                                            DemoGuideActivity.class);
                                }  else if (from == EXTERNAL) {
                                    intent = new Intent(DemoMainActivity.this,
                                            DemoExtGpsActivity.class);
                                }

                                Bundle bundle = new Bundle();
                                bundle.putSerializable(ROUTE_PLAN_NODE, mStartNode);
                                intent.putExtras(bundle);
                                startActivity(intent);
                                BaiduNaviManagerFactory.getRoutePlanManager()
                                        .removeRequestByHandler(this);
                                break;
                            default:
                                // nothing
                                break;
                        }
                    }
                });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[]
            grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == authBaseRequestCode) {
            for (int ret : grantResults) {
                if (ret == 0) {
                    continue;
                } else {
                    Toast.makeText(DemoMainActivity.this.getApplicationContext(),
                            "缺少导航基本的权限!", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
            initNavi();
        }
    }
}
