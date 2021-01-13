package com.anubis.module_skin.design.widget;

import android.content.Context;
import android.util.AttributeSet;
import com.anubis.module_skin.support.widget.SkinCompatBackgroundHelper;
import com.anubis.module_skin.support.widget.SkinCompatSupportable;
import com.google.android.material.navigation.NavigationView;


/**
 * Created by pengfengwang on 2017/1/15.
 */

public class SkinCompatNavigationView extends NavigationView implements SkinCompatSupportable {
    private final SkinCompatBackgroundHelper mBackgroundTintHelper;
//    private int mBackgroundResId = INVALID_ID;

    public SkinCompatNavigationView(Context context) {
        this(context, null);
    }

    public SkinCompatNavigationView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SkinCompatNavigationView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mBackgroundTintHelper = new SkinCompatBackgroundHelper(this);
        mBackgroundTintHelper.loadFromAttributes(attrs, 0);
        applySkin();
    }

    @Override
    public void applySkin() {
        if (mBackgroundTintHelper != null) {
            mBackgroundTintHelper.applySkin();
        }
    }
}
