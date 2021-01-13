package com.anubis.module_skin.support.widget;

import android.content.Context;
import android.util.AttributeSet;

import com.anubis.module_skin.R;

import androidx.annotation.DrawableRes;
import androidx.appcompat.widget.AppCompatRadioButton;

/**
 * Created by ximsfei on 17-1-14.
 */

public class SkinCompatRadioButton extends AppCompatRadioButton implements SkinCompatSupportable {
    private final SkinCompatCompoundButtonHelper mCompoundButtonHelper;

    public SkinCompatRadioButton(Context context) {
        this(context, null);
    }

    public SkinCompatRadioButton(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.radioButtonStyle);
    }

    public SkinCompatRadioButton(Context context, AttributeSet attrs, int defStyleAttr) {
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
