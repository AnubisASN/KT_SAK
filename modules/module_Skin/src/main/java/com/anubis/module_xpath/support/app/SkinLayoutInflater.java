package com.anubis.module_xpath.support.app;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.NonNull;

/**
 * Created by ximsfei on 2017/1/13.
 */

public interface SkinLayoutInflater {
    View createView(@NonNull Context context, final String name, @NonNull AttributeSet attrs);
}
