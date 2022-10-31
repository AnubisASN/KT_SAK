package com.anubis.module_xpath.support.widget;

import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.CompoundButton;

import com.anubis.module_xpath.R;
import com.anubis.module_xpath.support.content.res.SkinCompatResources;


/**
 * Created by ximsfei on 17-1-14.
 */
public class SkinCompatCompoundButtonHelper extends SkinCompatHelper {
    private final CompoundButton mView;
    private int mButtonResourceId = INVALID_ID;

    public SkinCompatCompoundButtonHelper(CompoundButton view) {
        mView = view;
    }

    void loadFromAttributes(AttributeSet attrs, int defStyleAttr) {
        TypedArray a = mView.getContext().obtainStyledAttributes(attrs, R.styleable.CompoundButton,
                defStyleAttr, INVALID_ID);
        try {
            if (a.hasValue(R.styleable.CompoundButton_android_button)) {
                mButtonResourceId = a.getResourceId(
                        R.styleable.CompoundButton_android_button, INVALID_ID);
            }
//                if (resourceId != 0) {
//                    mView.setButtonDrawable(
//                            AppCompatResources.getDrawable(mView.getContext(), resourceId));
//                }
//            }
//            if (a.hasValue(R.styleable.CompoundButton_buttonTint)) {
//                CompoundButtonCompat.setButtonTintList(mView,
//                        a.getColorStateList(R.styleable.CompoundButton_buttonTint));
//            }
//            if (a.hasValue(R.styleable.CompoundButton_buttonTintMode)) {
//                CompoundButtonCompat.setButtonTintMode(mView,
//                        DrawableUtils.parseTintMode(
//                                a.getInt(R.styleable.CompoundButton_buttonTintMode, -1),
//                                null));
//            }
        } finally {
            a.recycle();
        }
        applySkin();
    }

    public void setButtonDrawable(int resId) {
        mButtonResourceId = resId;
        applySkin();
    }

    @Override
    public void applySkin() {
        mButtonResourceId = SkinCompatHelper.checkResourceId(mButtonResourceId);
        if (mButtonResourceId != INVALID_ID) {
            mView.setButtonDrawable(SkinCompatResources.getInstance().getDrawable(mButtonResourceId));
        }
    }
}
