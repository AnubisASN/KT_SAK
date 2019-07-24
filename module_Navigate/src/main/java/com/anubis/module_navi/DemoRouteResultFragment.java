package com.anubis.module_navi;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.anubis.module_navi.custom.BNRecyclerView;
import com.anubis.module_navi.custom.BNScrollLayout;
import com.anubis.module_navi.custom.BNScrollView;
import com.baidu.navisdk.adapter.BNaviCommonParams;
import com.baidu.navisdk.adapter.BaiduNaviManagerFactory;
import com.baidu.navisdk.adapter.IBNRouteResultManager;
import com.baidu.navisdk.adapter.struct.BNRouteDetail;
import com.baidu.navisdk.adapter.struct.BNRoutePlanItem;

import java.util.ArrayList;


public class DemoRouteResultFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = "DemoRouteResultFragment";

    private LinearLayout mLayout_tab0;
    private LinearLayout mLayout_tab1;
    private LinearLayout mLayout_tab2;
    private RelativeLayout mRl_button;
    private RouteResultAdapter mResultAdapter;
    private BNRecyclerView mRecyclerView;
    private ArrayList<BNRoutePlanItem> mRoutePlanItems;
    private ArrayList<BNRouteDetail> mRouteList = new ArrayList<>();
    private Bundle mRouteDetails = new Bundle();
    private ArrayList<String> mLimitInfos = new ArrayList<>();
    private View mRootView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        BaiduNaviManagerFactory.getRouteResultManager().onCreate(getActivity());
        mRootView = inflater.inflate(R.layout.fragment_route_result, container, false);
        mLayout_tab0 = mRootView.findViewById(R.id.route_0);
        mLayout_tab0.setOnClickListener(this);
        mLayout_tab1 = mRootView.findViewById(R.id.route_1);
        mLayout_tab1.setOnClickListener(this);
        mLayout_tab2 = mRootView.findViewById(R.id.route_2);
        mLayout_tab2.setOnClickListener(this);
        mRl_button = mRootView.findViewById(R.id.rl_button);
        mRecyclerView = mRootView.findViewById(R.id.rv);
        mRootView.findViewById(R.id.btn_road).setOnClickListener(this);
        mRootView.findViewById(R.id.btn_fullView).setOnClickListener(this);
        mRootView.findViewById(R.id.btn_start_navi).setOnClickListener(this);

        BaiduNaviManagerFactory.getRouteResultSettingManager().setRouteMargin(
                100, 100, 100, 500);
        BaiduNaviManagerFactory.getRouteResultManager().setRouteClickedListener(
                new IBNRouteResultManager.IRouteClickedListener() {
                    @Override
                    public void routeClicked(int index) {
                        BaiduNaviManagerFactory.getRouteGuideManager().selectRoute(index);
                    }
                });
        initView();
        initData();
        return mRootView;
    }

    private void initView() {
        BNScrollView scrollView = mRootView.findViewById(R.id.content_scroll);
        scrollView.setVerticalScrollBarEnabled(false);
        final LinearLayout layoutTab = mRootView.findViewById(R.id.layout_3tab);
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                BNScrollLayout scrollLayout = mRootView.findViewById(R.id.layout_scroll);
                scrollLayout.setMaxOffset(layoutTab.getMeasuredHeight() + NormalUtils.dip2px(getActivity(), 10));
                scrollLayout.setToOpen();

                FrameLayout.LayoutParams layoutParams =
                        (FrameLayout.LayoutParams) mRl_button.getLayoutParams();
                layoutParams.bottomMargin =
                        layoutTab.getMeasuredHeight() + NormalUtils.dip2px(getActivity(), 10);
                mRl_button.setVisibility(View.VISIBLE);
            }
        });
    }

    private void initData() {
        Bundle bundle = BaiduNaviManagerFactory.getRouteResultManager().getRouteInfo();
        if (bundle == null) {
            return;
        }
        // 3Tab信息
        mRoutePlanItems = bundle.getParcelableArrayList(BNaviCommonParams.BNRouteInfoKey.INFO_TAB);
        // 每条路线的详细信息
        mRouteDetails = bundle.getBundle(BNaviCommonParams.BNRouteInfoKey.INFO_ROUTE_DETAIL);
        // 每条路线的限行信息
        mLimitInfos = bundle.getStringArrayList(BNaviCommonParams.BNRouteInfoKey.TRAFFIC_LIMIT_INFO);
        if (mLimitInfos != null) {
            for (int i = 0; i < mLimitInfos.size(); i++) {
                String[] arr = mLimitInfos.get(i).split(",");
                Log.e(TAG, "第" + arr[0] + "条路线限行消息：" + arr[1]);
            }
        }
        if (mRoutePlanItems != null) {
            if (mRoutePlanItems.size() > 0 && mRoutePlanItems.get(0) != null) {
                initTabView(mLayout_tab0, mRoutePlanItems.get(0));
            }

            if (mRoutePlanItems.size() > 1 && mRoutePlanItems.get(1) != null) {
                initTabView(mLayout_tab1, mRoutePlanItems.get(1));
            } else {
                mLayout_tab1.setVisibility(View.GONE);
            }

            if (mRoutePlanItems.size() > 2 && mRoutePlanItems.get(2) != null) {
                initTabView(mLayout_tab2, mRoutePlanItems.get(2));
            } else {
                mLayout_tab2.setVisibility(View.GONE);
            }
        }
        mLayout_tab0.setSelected(true);

        mRouteList.clear();
        mRouteList.addAll(mRouteDetails.<BNRouteDetail>getParcelableArrayList("0"));
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(layoutManager);
        mResultAdapter = new RouteResultAdapter(mRouteList);
        mRecyclerView.setAdapter(mResultAdapter);
    }

    private void initTabView(LinearLayout layout_tab, BNRoutePlanItem bnRoutePlanItem) {
        TextView prefer = layout_tab.findViewById(R.id.prefer);
        prefer.setText(bnRoutePlanItem.getPusLabelName());
        TextView time = layout_tab.findViewById(R.id.time);
        time.setText((int) bnRoutePlanItem.getPassTime() / 60 + "分钟");
        TextView distance = layout_tab.findViewById(R.id.distance);
        distance.setText((int) bnRoutePlanItem.getLength() / 1000 + "公里");
        TextView traffic_light = layout_tab.findViewById(R.id.traffic_light);
        traffic_light.setText(String.valueOf(bnRoutePlanItem.getLights()));
    }

    @Override
    public void onResume() {
        super.onResume();
        initView();
        BaiduNaviManagerFactory.getRouteResultManager().onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        BaiduNaviManagerFactory.getRouteResultManager().onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        BaiduNaviManagerFactory.getRouteResultManager().onDestroy();
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.route_0) {
            mLayout_tab0.setSelected(true);
            mLayout_tab1.setSelected(false);
            mLayout_tab2.setSelected(false);
            BaiduNaviManagerFactory.getRouteGuideManager().selectRoute(0);
            mRouteList.clear();
            mRouteList.addAll(mRouteDetails.<BNRouteDetail>getParcelableArrayList("0"));
            mResultAdapter.notifyDataSetChanged();

        } else if (i == R.id.route_1) {
            mLayout_tab0.setSelected(false);
            mLayout_tab1.setSelected(true);
            mLayout_tab2.setSelected(false);
            BaiduNaviManagerFactory.getRouteGuideManager().selectRoute(1);
            mRouteList.clear();
            mRouteList.addAll(mRouteDetails.<BNRouteDetail>getParcelableArrayList("1"));
            mResultAdapter.notifyDataSetChanged();

        } else if (i == R.id.route_2) {
            if (mRoutePlanItems.size() < 3) {
                return;
            }
            mLayout_tab0.setSelected(false);
            mLayout_tab1.setSelected(false);
            mLayout_tab2.setSelected(true);
            BaiduNaviManagerFactory.getRouteGuideManager().selectRoute(2);
            mRouteList.clear();
            mRouteList.addAll(mRouteDetails.<BNRouteDetail>getParcelableArrayList("2"));
            mResultAdapter.notifyDataSetChanged();

        } else if (i == R.id.btn_fullView) {
            BaiduNaviManagerFactory.getRouteResultManager().fullView();

        } else if (i == R.id.btn_road) {
            BaiduNaviManagerFactory.getRouteResultSettingManager().setRealRoadCondition(getActivity());

        } else if (i == R.id.btn_start_navi) {
            BaiduNaviManagerFactory.getRouteResultManager().startNavi();
            FragmentManager fm = getActivity().getSupportFragmentManager();
            FragmentTransaction tx = fm.beginTransaction();
            DemoGuideFragment fragment = new DemoGuideFragment();
            tx.replace(R.id.fragment_content, fragment, "DemoGuide");
            tx.addToBackStack(null);
            tx.commit();

        } else {
        }
    }
}
