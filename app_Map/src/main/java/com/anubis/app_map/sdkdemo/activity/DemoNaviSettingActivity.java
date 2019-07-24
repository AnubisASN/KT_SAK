/*
 * Copyright (C) 2018 Baidu, Inc. All Rights Reserved.
 */
package com.anubis.app_map.sdkdemo.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.anubis.app_map.R;
import com.baidu.navisdk.adapter.BaiduNaviManagerFactory;
import com.baidu.navisdk.adapter.IBNOuterSettingParams;
import com.baidu.navisdk.comapi.setting.SettingParams;

public class DemoNaviSettingActivity extends Activity implements View.OnClickListener {

    private static final String TAG = "DemoNaviSettingActivity";

    // 导航视角
    private static final int GUIDE_VIEW_OPTION_CNT = 2;
    private static final int GUIDE_VIEW_3D_INDEX = 0;
    private static final int GUIDE_VIEW_2D_INDEX = 1;
    private View[] mGuideViewModeViews = new View[GUIDE_VIEW_OPTION_CNT];
    private TextView[] mGuideViewModeTVs = new TextView[GUIDE_VIEW_OPTION_CNT];
    // 日夜模式
    private static final int DAY_NIGHT_MODE_OPTION_CNT = 3;
    private static final int AUTO_MODE_INDEX = 0;
    private static final int DAY_MODE_INDEX = 1;
    private static final int NIGHT_MODE_INDEX = 2;
    private View[] mDayNightModeViews = new View[DAY_NIGHT_MODE_OPTION_CNT];
    private TextView[] mDayNightModeTVs = new TextView[DAY_NIGHT_MODE_OPTION_CNT];
    // 导航中图面显示
    private static final int NAV_DISPLAY_MODE_OPTION_CNT = 2;
    private static final int NAV_DISPLAY_OVERVIEW_INDEX = 0;
    private static final int NAV_DISPLAY_ROAD_COND_BAR_INDEX = 1;
    private View[] mNavDisplayModeViews = new View[NAV_DISPLAY_MODE_OPTION_CNT];
    private TextView[] mNavDisplayModeTVs = new TextView[NAV_DISPLAY_MODE_OPTION_CNT];
    // 列表设置总数
    private static final int LIST_OPTION_CNT = 6;
    // 智能比例尺
    private static final int SCALE_INDEX = 0;
    private static final int MULTI_ROUTE_INDEX = 1;
    private static final int BOTTOMBAR_OPEN_INDEX = 2;
    private static final int MORE_SETTINGS_INDEX = 3;
    private static final int ROUTE_SORT_INDEX = 4;
    private static final int ROUTE_SEARCH_INDEX = 5;

    ImageView[] mCheckboxs = new ImageView[LIST_OPTION_CNT];
    boolean[] mIsChecked = new boolean[LIST_OPTION_CNT];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.onsdk_activity_setting);
        initUserConfig();
        initViews();
        initClickListener();
    }

    private void initUserConfig() {
        try {
            mIsChecked[SCALE_INDEX] = BaiduNaviManagerFactory.getProfessionalNaviSettingManager()
                    .isAutoScale();
            mIsChecked[MULTI_ROUTE_INDEX] = BaiduNaviManagerFactory.getCommonSettingManager()
                    .isMultiRouteEnable();
            mIsChecked[BOTTOMBAR_OPEN_INDEX] = true;
            mIsChecked[MORE_SETTINGS_INDEX] = true;
            mIsChecked[ROUTE_SORT_INDEX] = true;
            mIsChecked[ROUTE_SEARCH_INDEX] = true;

            // 因为没有提供获取功能入口控制状态接口，所以demo里每次都先重置为开
            BaiduNaviManagerFactory.getProfessionalNaviSettingManager().enableBottomBarOpen(true);
            BaiduNaviManagerFactory.getProfessionalNaviSettingManager().enableMoreSettings(true);
            BaiduNaviManagerFactory.getProfessionalNaviSettingManager().enableRouteSort(true);
            BaiduNaviManagerFactory.getProfessionalNaviSettingManager().enableRouteSearch(true);
        } catch (Exception e) {
            // TODO
        }
    }

    private void initViews() {
        try {
            // 导航视角
            mGuideViewModeViews[GUIDE_VIEW_3D_INDEX] = findViewById(R.id.bnav_view_car3d_layout);
            mGuideViewModeViews[GUIDE_VIEW_2D_INDEX] = findViewById(R.id.bnav_view_north2d_layout);
            mGuideViewModeTVs[GUIDE_VIEW_3D_INDEX] = findViewById(R.id.bnav_view_car3d_tv);
            mGuideViewModeTVs[GUIDE_VIEW_2D_INDEX] = findViewById(R.id.bnav_view_north2d_tv);

            // 日夜模式
            mDayNightModeViews[AUTO_MODE_INDEX] = findViewById(R.id.bnav_auto_mode_layout);
            mDayNightModeViews[DAY_MODE_INDEX] = findViewById(R.id.bnav_day_mode_layout);
            mDayNightModeViews[NIGHT_MODE_INDEX] = findViewById(R.id.bnav_night_mode_layout);
            mDayNightModeTVs[AUTO_MODE_INDEX] = findViewById(R.id.bnav_auto_mode_tv);
            mDayNightModeTVs[DAY_MODE_INDEX] = findViewById(R.id.bnav_day_mode_tv);
            mDayNightModeTVs[NIGHT_MODE_INDEX] = findViewById(R.id.bnav_night_mode_tv);

            mCheckboxs[SCALE_INDEX] = (ImageView) findViewById(R.id.nav_scale_cb);
            mCheckboxs[MULTI_ROUTE_INDEX] = (ImageView) findViewById(R.id.nav_multi_route_cb);
            mCheckboxs[BOTTOMBAR_OPEN_INDEX] = (ImageView) findViewById(R.id.nav_bottombar_open_cb);
            mCheckboxs[MORE_SETTINGS_INDEX] = (ImageView) findViewById(R.id.nav_more_settings_cb);
            mCheckboxs[ROUTE_SORT_INDEX] = (ImageView) findViewById(R.id.nav_route_sort_cb);
            mCheckboxs[ROUTE_SEARCH_INDEX] = (ImageView) findViewById(R.id.nav_route_search_cb);

            // 导航中图面显示
            mNavDisplayModeViews[NAV_DISPLAY_OVERVIEW_INDEX] = findViewById(R.id
                    .bnav_display_overview_mode_layout);
            mNavDisplayModeTVs[NAV_DISPLAY_OVERVIEW_INDEX] = findViewById(R.id
                    .nav_display_overview_mode_tv);

            mNavDisplayModeViews[NAV_DISPLAY_ROAD_COND_BAR_INDEX] = findViewById(R.id
                    .bnav_display_road_cond_mode_layout);
            mNavDisplayModeTVs[NAV_DISPLAY_ROAD_COND_BAR_INDEX] = findViewById(R.id
                    .nav_display_road_condition_mode_tv);
        } catch (Exception e) {
            // TODO
        }

        findViewById(R.id.tv_setting).setVisibility(View.GONE);
        findViewById(R.id.nav_bottombar_open_layout).setVisibility(View.GONE);
        findViewById(R.id.nav_more_settings_layout).setVisibility(View.GONE);
        findViewById(R.id.nav_route_sort_layout).setVisibility(View.GONE);
        findViewById(R.id.nav_route_search_layout).setVisibility(View.GONE);

        updateDayNightModeView(BaiduNaviManagerFactory.getProfessionalNaviSettingManager()
                .getDayNightMode());
        updateGuideViewModeView(BaiduNaviManagerFactory.getProfessionalNaviSettingManager()
                .getGuideViewMode());
        updateNavDisplayViewModeView(BaiduNaviManagerFactory.getProfessionalNaviSettingManager()
                .getFullViewMode());

        for (int i = 0; i < LIST_OPTION_CNT; i++) {
            updateView(i);
        }
    }

    private void initClickListener() {
        findViewById(R.id.bnav_view_north2d_layout).setOnClickListener(this);
        findViewById(R.id.bnav_view_car3d_layout).setOnClickListener(this);

        findViewById(R.id.bnav_auto_mode_layout).setOnClickListener(this);
        findViewById(R.id.bnav_day_mode_layout).setOnClickListener(this);
        findViewById(R.id.bnav_night_mode_layout).setOnClickListener(this);

        findViewById(R.id.nav_scale_layout).setOnClickListener(this);
        findViewById(R.id.nav_multi_route_layout).setOnClickListener(this);

        findViewById(R.id.bnav_display_overview_mode_layout).setOnClickListener(this);
        findViewById(R.id.bnav_display_road_cond_mode_layout).setOnClickListener(this);

        findViewById(R.id.nav_bottombar_open_layout).setOnClickListener(this);
        findViewById(R.id.nav_more_settings_layout).setOnClickListener(this);
        findViewById(R.id.nav_route_sort_layout).setOnClickListener(this);
        findViewById(R.id.nav_route_search_layout).setOnClickListener(this);
    }

    private void updateView(int index) {
        switch (index) {
            case SCALE_INDEX:
                updateCheckDrawable(index);
                break;
            case MULTI_ROUTE_INDEX:
                updateCheckDrawable(index);
                break;
            default:
                updateCheckDrawable(index);
                break;
        }
    }

    private void updateCheckDrawable(int index) {
        try {
            if (mIsChecked[index]) {
                mCheckboxs[index].setImageResource(R.drawable.set_checkin_icon);
            } else {
                mCheckboxs[index].setImageResource(R.drawable.set_checkout_icon);
            }
        } catch (Exception e) {
            // TODO
        }
    }

    private void updateDayNightModeView(int mode) {
        try {
            mDayNightModeViews[AUTO_MODE_INDEX].setSelected(mode == SettingParams.Action
                    .DAY_NIGHT_MODE_AUTO);
            mDayNightModeTVs[AUTO_MODE_INDEX].setSelected(mode == SettingParams.Action
                    .DAY_NIGHT_MODE_AUTO);

            mDayNightModeViews[DAY_MODE_INDEX].setSelected(mode == SettingParams.Action
                    .DAY_NIGHT_MODE_DAY);
            mDayNightModeTVs[DAY_MODE_INDEX].setSelected(mode == SettingParams.Action
                    .DAY_NIGHT_MODE_DAY);

            mDayNightModeViews[NIGHT_MODE_INDEX].setSelected(mode == SettingParams.Action
                    .DAY_NIGHT_MODE_NIGHT);
            mDayNightModeTVs[NIGHT_MODE_INDEX].setSelected(mode == SettingParams.Action
                    .DAY_NIGHT_MODE_NIGHT);
        } catch (Exception e) {
            // TODO
        }
    }

    private void updateGuideViewModeView(int mode) {
        try {
            mGuideViewModeViews[GUIDE_VIEW_3D_INDEX].setSelected(mode == SettingParams.MapMode
                    .CAR_3D);
            mGuideViewModeTVs[GUIDE_VIEW_3D_INDEX].setSelected(mode == SettingParams.MapMode
                    .CAR_3D);

            mGuideViewModeViews[GUIDE_VIEW_2D_INDEX].setSelected(mode == SettingParams.MapMode
                    .NORTH_2D);
            mGuideViewModeTVs[GUIDE_VIEW_2D_INDEX].setSelected(mode == SettingParams.MapMode
                    .NORTH_2D);
        } catch (Exception e) {
            // TODO
        }
    }

    private void updateNavDisplayViewModeView(int mode) {
        try {
            mNavDisplayModeViews[NAV_DISPLAY_OVERVIEW_INDEX]
                    .setSelected(mode == IBNOuterSettingParams.PreViewMode.MapMini);
            mNavDisplayModeTVs[NAV_DISPLAY_OVERVIEW_INDEX]
                    .setSelected(mode == IBNOuterSettingParams.PreViewMode.MapMini);

            mNavDisplayModeViews[NAV_DISPLAY_ROAD_COND_BAR_INDEX]
                    .setSelected(mode == IBNOuterSettingParams.PreViewMode.RoadBar);
            mNavDisplayModeTVs[NAV_DISPLAY_ROAD_COND_BAR_INDEX]
                    .setSelected(mode == IBNOuterSettingParams.PreViewMode.RoadBar);
        } catch (Exception e) {
            // TODO
        }
    }

    private boolean isCarLogoToEndLine() {
        return BaiduNaviManagerFactory.getProfessionalNaviSettingManager()
                .isShowCarLogoToEndRedLine();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        if (v == null) {
            return;
        }
        switch (v.getId()) {
            case R.id.nav_scale_layout:
                reverseItemCheck(SCALE_INDEX);
                onSettingsChange(SCALE_INDEX);
                break;
            case R.id.nav_multi_route_layout:
                reverseItemCheck(MULTI_ROUTE_INDEX);
                onSettingsChange(MULTI_ROUTE_INDEX);
            case R.id.bnav_view_car3d_layout:
                updateGuideViewModeView(SettingParams.MapMode.CAR_3D);
                BaiduNaviManagerFactory.getProfessionalNaviSettingManager()
                        .setGuideViewMode(SettingParams.MapMode.CAR_3D);
                break;
            case R.id.bnav_view_north2d_layout:
                updateGuideViewModeView(SettingParams.MapMode.NORTH_2D);
                BaiduNaviManagerFactory.getProfessionalNaviSettingManager()
                        .setGuideViewMode(SettingParams.MapMode.NORTH_2D);
                break;
            case R.id.bnav_auto_mode_layout:
                updateDayNightModeView(SettingParams.Action.DAY_NIGHT_MODE_AUTO);
                BaiduNaviManagerFactory.getProfessionalNaviSettingManager()
                        .setDayNightMode(SettingParams.Action.DAY_NIGHT_MODE_AUTO);
                break;
            case R.id.bnav_day_mode_layout:
                updateDayNightModeView(SettingParams.Action.DAY_NIGHT_MODE_DAY);
                BaiduNaviManagerFactory.getProfessionalNaviSettingManager()
                        .setDayNightMode(SettingParams.Action.DAY_NIGHT_MODE_DAY);
                break;
            case R.id.bnav_night_mode_layout:
                updateDayNightModeView(SettingParams.Action.DAY_NIGHT_MODE_NIGHT);
                BaiduNaviManagerFactory.getProfessionalNaviSettingManager()
                        .setDayNightMode(SettingParams.Action.DAY_NIGHT_MODE_NIGHT);
                break;
            case R.id.bnav_display_overview_mode_layout:
                updateNavDisplayViewModeView(IBNOuterSettingParams.PreViewMode.MapMini);
                BaiduNaviManagerFactory.getProfessionalNaviSettingManager()
                        .setFullViewMode(IBNOuterSettingParams.PreViewMode.MapMini);
                break;
            case R.id.bnav_display_road_cond_mode_layout:
                updateNavDisplayViewModeView(IBNOuterSettingParams.PreViewMode.RoadBar);
                BaiduNaviManagerFactory.getProfessionalNaviSettingManager()
                        .setFullViewMode(IBNOuterSettingParams.PreViewMode.RoadBar);
                break;

            case R.id.nav_bottombar_open_layout:
                reverseItemCheck(BOTTOMBAR_OPEN_INDEX);
                onSettingsChange(BOTTOMBAR_OPEN_INDEX);
                break;
            case R.id.nav_more_settings_layout:
                reverseItemCheck(MORE_SETTINGS_INDEX);
                onSettingsChange(MORE_SETTINGS_INDEX);
                break;
            case R.id.nav_route_sort_layout:
                reverseItemCheck(ROUTE_SORT_INDEX);
                onSettingsChange(ROUTE_SORT_INDEX);
                break;
            case R.id.nav_route_search_layout:
                reverseItemCheck(ROUTE_SEARCH_INDEX);
                onSettingsChange(ROUTE_SEARCH_INDEX);
                break;
            default:
                break;
        }
    }

    private void reverseItemCheck(int index) {
        try {
            mIsChecked[index] = !mIsChecked[index];
        } catch (Exception e) {
            // TODO
        }
    }

    private void onSettingsChange(int index) {
        try {
            switch (index) {
                case SCALE_INDEX: {
                    boolean open = mIsChecked[index];
                    BaiduNaviManagerFactory.getProfessionalNaviSettingManager().setAutoScale(open);
                    break;
                }
                case MULTI_ROUTE_INDEX: {
                    boolean open = mIsChecked[index];
                    BaiduNaviManagerFactory.getCommonSettingManager().setMultiRouteEnable(open);
                    break;
                }
                case BOTTOMBAR_OPEN_INDEX: {
                    boolean open = mIsChecked[index];
                    BaiduNaviManagerFactory.getProfessionalNaviSettingManager()
                            .enableBottomBarOpen(open);
                    break;
                }
                case MORE_SETTINGS_INDEX: {
                    boolean open = mIsChecked[index];
                    BaiduNaviManagerFactory.getProfessionalNaviSettingManager()
                            .enableMoreSettings(open);
                    break;
                }
                case ROUTE_SORT_INDEX: {
                    boolean open = mIsChecked[index];
                    BaiduNaviManagerFactory.getProfessionalNaviSettingManager().enableRouteSort
                            (open);
                    break;
                }
                case ROUTE_SEARCH_INDEX: {
                    boolean open = mIsChecked[index];
                    BaiduNaviManagerFactory.getProfessionalNaviSettingManager().enableRouteSearch
                            (open);
                    break;
                }
                default: {
                    break;
                }
            }
            updateView(index);
        } catch (Throwable t) {
            // TODO
        }
    }
}
