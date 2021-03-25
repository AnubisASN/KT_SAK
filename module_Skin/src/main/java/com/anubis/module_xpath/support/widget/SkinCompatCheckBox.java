package com.anubis.module_xpath.support.widget;

import android.content.Context;
import android.util.AttributeSet;

import com.anubis.module_xpath.R;

import androidx.annotation.DrawableRes;
import androidx.appcompat.widget.AppCompatCheckBox;

/**
 * Created by ximsfei on 17-1-14.
 */

public class SkinCompatCheckBox extends AppCompatCheckBox implements SkinCompatSupportable {
    private final SkinCompatCompoundButtonHelper mCompoundButtonHelper;

    public SkinCompatCheckBox(Context context) {
        this(context, null);
    }

    public SkinCompatCheckBox(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.checkboxStyle);
    }

    public SkinCompatCheckBox(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mCompoundButtonHelper = new SkinCompatCompoundButtonHelper(this);
        mCompoundButtonHelper.loadFromAttributes(attrs, defStyleAttr);
    }

    @Override
    public void setButtonDrawable(@DrawableRes int resId) {
        super.setButtonDrawable(resId);
        if (mCompoundButtonHelper != null) {
            mCompoundButtonHelper.setButtonDrawable(resId);
        }
    }

    @Override
    public void applySkin() {
        if (mCompoundButtonHelper != null) {
            mCompoundButtonHelper.applySkin();
        }
    }
}
