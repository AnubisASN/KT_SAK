package com.anubis.module_xpath.support.widget;


import com.anubis.module_xpath.support.utils.SkinLog;

/**
 * Created by ximsfei on 2017/1/13.
 */

public abstract class SkinCompatHelper {
    protected static final String TAG = SkinCompatHelper.class.getSimpleName();
    protected static final String SYSTEM_ID_PREFIX = "1";
    public static final int INVALID_ID = -1;

    final static public int checkResourceId(int resId) {
        String hexResId = Integer.toHexString(resId);
        SkinLog.d(TAG, "hexResId = " + hexResId);
        return hexResId.startsWith(SYSTEM_ID_PREFIX) ? INVALID_ID : resId;
    }

    abstract public void applySkin();
}
