package com.anubis.module_skin.support.widget;

import android.content.Context;
import android.util.AttributeSet;

import com.anubis.module_skin.R;

import androidx.appcompat.widget.AppCompatSeekBar;

/**
 * Created by ximsfei on 17-1-21.
 */

public class SkinCompatSeekBar extends AppCompatSeekBar implements SkinCompatSupportable {
    private final SkinCompatSeekBarHelper mSkinCompatSeekBarHelper;

    public SkinCompatSeekBar(Context context) {
        this(context, null);
    }

    public SkinCompatSeekBar(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.seekBarStyle);
    }

    public SkinCompatSeekBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mSkinCompatSeekBarHelper = new SkinCompatSeekBarHelper(this);
        mSkinCompatSeekBarHelper.loadFromAttributes(attrs, defStyleAttr);
    }


    @Override
    public void applySkin() {
        if (mSkinCompatSeekBarHelper != null) {
            mSkinCompatSeekBarHelper.applySkin();
        }
    }
}
