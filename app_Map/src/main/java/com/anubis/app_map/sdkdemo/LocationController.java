package com.anubis.app_map.sdkdemo;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import androidx.core.app.ActivityCompat;
import android.util.Log;

import com.baidu.navisdk.adapter.BaiduNaviManagerFactory;
import com.baidu.navisdk.adapter.struct.BNLocationData;

/**
 * 系统GPS回调
 *
 * @author yangchao on 2018/9/28.
 */
public class LocationController implements LocationListener {

    private LocationManager mLocationManager;

    private static LocationController sInstance;

    private BNLocationData mLocData;

    private boolean isLocating;

    private LocationController() {
    }

    public static synchronized LocationController getInstance() {
        if (sInstance == null) {
            sInstance = new LocationController();
        }

        return sInstance;
    }

    public boolean isLocating() {
        return isLocating;
    }

    @Override
    public void onLocationChanged(Location location) {
        if (location == null) {
            return;
        }
        Log.e("onLocationChanged: ", location.toString());
        mLocData = new BNLocationData.Builder()
                .latitude(location.getLatitude())
                .longitude(location.getLongitude())
                .accuracy(location.getAccuracy())
                .speed(location.getSpeed())
                .direction(location.getBearing())
                .altitude((int) location.getAltitude())
                .time(location.getTime())
                .build();
        BaiduNaviManagerFactory.getMapManager().setMyLocationData(mLocData);
    }

    public void startLocation(Context ctx) {
        if (isLocating) {
            return;
        }
        if (mLocationManager == null) {
            mLocationManager = (LocationManager) ctx.getSystemService(Context
                    .LOCATION_SERVICE);
        }
        if (ActivityCompat.checkSelfPermission(ctx, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(ctx,
                        Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.e("LocationDemo", "initLocationClient: permission failed");
            return;
        }
        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                1000, 1, this);
        isLocating = true;
    }

    public void stopLocation() {
        mLocationManager.removeUpdates(this);
        isLocating = false;
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
}
