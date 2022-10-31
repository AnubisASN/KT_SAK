package com.anubis.module_navi.activity;

import androidx.appcompat.app.AppCompatActivity;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import com.anubis.module_navi.LocationController;
import com.baidu.navisdk.adapter.BaiduNaviManagerFactory;
import com.baidu.navisdk.adapter.IBNRouteGuideManager;

public class DemoExtGpsActivity extends AppCompatActivity {

    private IBNRouteGuideManager.OnNavigationListener mOnNavigationListener =
            new IBNRouteGuideManager.OnNavigationListener() {

                @Override
                public void onNaviGuideEnd() {
                    // 退出导航
                    finish();
                }

                @Override
                public void notifyOtherAction(int actionType, int arg1, int arg2, Object obj) {
                    if (actionType == 0) {
                        // 导航到达目的地 自动退出
                        BaiduNaviManagerFactory.getRouteGuideManager().forceQuitNaviWithoutDialog();
                    }
                }
            };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View mapView = BaiduNaviManagerFactory.getRouteGuideManager()
                .onCreate(this, mOnNavigationListener);
        setContentView(mapView);

        initExtGpsData();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        BaiduNaviManagerFactory.getRouteGuideManager().onConfigurationChanged(newConfig);
    }

    private void initExtGpsData() {
        BaiduNaviManagerFactory.getBaiduNaviManager().externalLocation(true);
        LocationController.getInstance().startLocation(getApplication());
    }

    @Override
    public void onBackPressed() {
        BaiduNaviManagerFactory.getRouteGuideManager()
                .onBackPressed(false, true);
    }

    @Override
    protected void onStart() {
        super.onStart();
        BaiduNaviManagerFactory.getRouteGuideManager().onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        BaiduNaviManagerFactory.getRouteGuideManager().onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        BaiduNaviManagerFactory.getRouteGuideManager().onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        BaiduNaviManagerFactory.getRouteGuideManager().onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocationController.getInstance().stopLocation();
        BaiduNaviManagerFactory.getBaiduNaviManager().externalLocation(false);
    }
}
