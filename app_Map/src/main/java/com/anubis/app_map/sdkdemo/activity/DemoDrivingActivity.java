package com.anubis.app_map.sdkdemo.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.anubis.app_map.R;
import com.anubis.app_map.sdkdemo.DemoRouteResultFragment;
import com.baidu.navisdk.adapter.BNRoutePlanNode;
import com.baidu.navisdk.adapter.BaiduNaviManagerFactory;
import com.baidu.navisdk.adapter.IBNRoutePlanManager;

import java.util.ArrayList;
import java.util.List;

public class DemoDrivingActivity extends FragmentActivity {

    private FrameLayout mFl_retry;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driving);
        ViewGroup mapContent = findViewById(R.id.map_container);
        View map = BaiduNaviManagerFactory.getMapManager().getMapView();
        if (map != null && map.getParent() != null) {
            ((ViewGroup) map.getParent()).removeView(map);
        }
        mapContent.addView(map);

        mFl_retry = findViewById(R.id.fl_retry);
        LinearLayout ll_retry = findViewById(R.id.ll_retry);
        final BNRoutePlanNode sNode = new BNRoutePlanNode.Builder()
                .latitude(40.050969)
                .longitude(116.300821)
                .name("百度大厦")
                .description("百度大厦")
                .coordinateType(BNRoutePlanNode.CoordinateType.WGS84)
                .build();
        final BNRoutePlanNode eNode = new BNRoutePlanNode.Builder()
                .latitude(39.908749)
                .longitude(116.397491)
                .name("北京天安门")
                .description("北京天安门")
                .coordinateType(BNRoutePlanNode.CoordinateType.WGS84)
                .build();
        ll_retry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                routePlan(sNode, eNode);
            }
        });
        routePlan(sNode, eNode);
    }


    @Override
    protected void onResume() {
        super.onResume();
        BaiduNaviManagerFactory.getMapManager().onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        BaiduNaviManagerFactory.getMapManager().onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void routePlan(BNRoutePlanNode sNode, BNRoutePlanNode eNode) {
        List<BNRoutePlanNode> list = new ArrayList<>();
        list.add(sNode);
        list.add(eNode);

        BaiduNaviManagerFactory.getCommonSettingManager().setCarNum(this, "粤B88888");
        BaiduNaviManagerFactory.getRoutePlanManager().routeplan(
                list,
                IBNRoutePlanManager.RoutePlanPreference.ROUTE_PLAN_PREFERENCE_DEFAULT,
                null,
                new Handler(Looper.getMainLooper()) {
                    @Override
                    public void handleMessage(Message msg) {
                        switch (msg.what) {
                            case IBNRoutePlanManager.MSG_NAVI_ROUTE_PLAN_START:
                                Toast.makeText(getApplicationContext(),
                                        "算路开始", Toast.LENGTH_SHORT).show();
                                break;
                            case IBNRoutePlanManager.MSG_NAVI_ROUTE_PLAN_SUCCESS:
                                mFl_retry.setVisibility(View.GONE);
                                Toast.makeText(getApplicationContext(),
                                        "算路成功", Toast.LENGTH_SHORT).show();

                                FragmentManager fm = getSupportFragmentManager();
                                FragmentTransaction tx = fm.beginTransaction();
                                DemoRouteResultFragment fragment = new DemoRouteResultFragment();
                                tx.add(R.id.fragment_content, fragment, "RouteResult");
                                tx.commit();
                                break;
                            case IBNRoutePlanManager.MSG_NAVI_ROUTE_PLAN_FAILED:
                                mFl_retry.setVisibility(View.VISIBLE);
                                Toast.makeText(getApplicationContext(),
                                        "算路失败", Toast.LENGTH_SHORT).show();
                                break;
                            default:
                                // nothing
                                break;
                        }
                    }
                });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
