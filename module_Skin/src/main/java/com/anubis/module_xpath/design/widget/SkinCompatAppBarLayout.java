package com.anubis.module_xpath.design.widget;

import android.content.Context;
import android.util.AttributeSet;

import com.anubis.module_xpath.support.widget.SkinCompatBackgroundHelper;
import com.anubis.module_xpath.support.widget.SkinCompatSupportable;
import com.google.android.material.appbar.AppBarLayout;

/**
 * Created by ximsfei on 2017/1/13.
 */

public class SkinCompatAppBarLayout extends AppBarLayout implements SkinCompatSupportable {
    private final SkinCompatBackgroundHelper mBackgroundTintHelper;

    public SkinCompatAppBarLayout(Context context) {
        this(context, null);
    }

    public SkinCompatAppBarLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        mBackgroundTintHelper = new SkinCompatBackgroundHelper(this);
        mBackgroundTintHelper.loadFromAttributes(attrs, 0);
    }

    @Override
    public void applySkin() {
        if (mBackgroundTintHelper != null) {
            mBackgroundTintHelper.applySkin();
        }
    }
}
