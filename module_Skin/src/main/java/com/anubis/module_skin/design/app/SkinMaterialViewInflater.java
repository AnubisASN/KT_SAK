package com.anubis.module_skin.design.app;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import com.anubis.module_skin.support.app.SkinLayoutInflater;
import com.anubis.module_skin.design.widget.SkinCompatAppBarLayout;
import com.anubis.module_skin.design.widget.SkinCompatNavigationView;
import com.anubis.module_skin.design.widget.SkinCompatTabLayout;
import androidx.annotation.NonNull;

/**
 * Created by ximsfei on 2017/1/13.
 */
public class SkinMaterialViewInflater implements SkinLayoutInflater {
    @Override
    public View createView(@NonNull Context context, final String name, @NonNull AttributeSet attrs) {
        View view = null;
        switch (name) {
            case "android.support.design.widget.AppBarLayout":
                view = new SkinCompatAppBarLayout(context, attrs);
                break;
            case "android.support.design.widget.TabLayout":
                view = new SkinCompatTabLayout(context, attrs);
                break;
            case "android.support.design.widget.NavigationView":
                view = new SkinCompatNavigationView(context, attrs);
                break;
        }
        return view;
    }
}
