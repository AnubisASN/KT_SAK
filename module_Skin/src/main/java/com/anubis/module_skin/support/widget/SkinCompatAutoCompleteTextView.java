package com.anubis.module_skin.support.widget;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;

import com.anubis.module_skin.R;
import com.anubis.module_skin.support.content.res.SkinCompatResources;

import androidx.annotation.DrawableRes;
import androidx.appcompat.widget.AppCompatAutoCompleteTextView;
import androidx.appcompat.widget.TintTypedArray;
import androidx.core.graphics.drawable.DrawableCompat;

import static com.anubis.module_skin.support.widget.SkinCompatHelper.INVALID_ID;

/**
 * Created by ximsfei on 2017/1/13.
 */

public class SkinCompatAutoCompleteTextView extends AppCompatAutoCompleteTextView implements SkinCompatSupportable {
    private static final int[] TINT_ATTRS = {
            android.R.attr.popupBackground
    };
    private int mDropDownBackgroundResId = INVALID_ID;
    private SkinCompatTextHelper mTextHelper;
    private SkinCompatBackgroundHelper mBackgroundTintHelper;

    public SkinCompatAutoCompleteTextView(Context context) {
        this(context, null);
    }

    public SkinCompatAutoCompleteTextView(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.autoCompleteTextViewStyle);
    }

    public SkinCompatAutoCompleteTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TintTypedArray a = TintTypedArray.obtainStyledAttributes(getContext(), attrs,
                TINT_ATTRS, defStyleAttr, 0);
        if (a.hasValue(0)) {
            mDropDownBackgroundResId = a.getResourceId(0, INVALID_ID);
        }
        a.recycle();
        applyDropDownBackgroundResource();
        mBackgroundTintHelper = new SkinCompatBackgroundHelper(this);
        mBackgroundTintHelper.loadFromAttributes(attrs, defStyleAttr);
        mTextHelper = new SkinCompatTextHelper(this);
        mTextHelper.loadFromAttributes(attrs, defStyleAttr);
    }

    @Override
    public void setDropDownBackgroundResource(@DrawableRes int resId) {
        super.setDropDownBackgroundResource(resId);
        mDropDownBackgroundResId = resId;
        applyDropDownBackgroundResource();
    }

    private void applyDropDownBackgroundResource() {
        mDropDownBackgroundResId = SkinCompatHelper.checkResourceId(mDropDownBackgroundResId);
        if (mDropDownBackgroundResId != INVALID_ID) {
            String typeName = getResources().getResourceTypeName(mDropDownBackgroundResId);
            if ("color".equals(typeName)) {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                    int color = SkinCompatResources.getInstance().getColor(mDropDownBackgroundResId);
                    setDrawingCacheBackgroundColor(color);
                } else {
                    ColorStateList colorStateList =
                            SkinCompatResources.getInstance().getColorStateList(mDropDownBackgroundResId);
                    Drawable drawable = getDropDownBackground();
                    DrawableCompat.setTintList(drawable, colorStateList);
                    setDropDownBackgroundDrawable(drawable);
                }
            } else if ("drawable".equals(typeName)) {
                Drawable drawable = SkinCompatResources.getInstance().getDrawable(mDropDownBackgroundResId);
                setDropDownBackgroundDrawable(drawable);
            }
        }
    }

    @Override
    public void setBackgroundResource(@DrawableRes int resId) {
        super.setBackgroundResource(resId);
        if (mBackgroundTintHelper != null) {
            mBackgroundTintHelper.onSetBackgroundResource(resId);
        }
    }

    @Override
    public void setTextAppearance(Context context, int resId) {
        super.setTextAppearance(context, resId);
        if (mTextHelper != null) {
            mTextHelper.onSetTextAppearance(context, resId);
        }
    }

    @Override
    public void applySkin() {
        if (mBackgroundTintHelper != null) {
            mBackgroundTintHelper.applySkin();
        }
        if (mTextHelper != null) {
            mTextHelper.applySkin();
        }
        applyDropDownBackgroundResource();
    }
}
