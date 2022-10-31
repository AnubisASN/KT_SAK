package com.anubis.module_xpath.support.widget;

import android.content.Context;
import android.util.AttributeSet;

import com.anubis.module_xpath.R;

import androidx.appcompat.widget.AppCompatRatingBar;

/**
 * Created by ximsfei on 17-1-21.
 */

public class SkinCompatRatingBar extends AppCompatRatingBar implements SkinCompatSupportable {
    private final SkinCompatProgressBarHelper mSkinCompatProgressBarHelper;

    public SkinCompatRatingBar(Context context) {
        this(context, null);
    }

    public SkinCompatRatingBar(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.ratingBarStyle);
    }

    public SkinCompatRatingBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mSkinCompatProgressBarHelper = new SkinCompatProgressBarHelper(this);
        mSkinCompatProgressBarHelper.loadFromAttributes(attrs, defStyleAttr);
    }

    @Override
    public void applySkin() {
        if (mSkinCompatProgressBarHelper != null) {
            mSkinCompatProgressBarHelper.applySkin();
        }
    }
}
