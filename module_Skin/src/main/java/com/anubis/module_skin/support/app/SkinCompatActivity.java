package com.anubis.module_skin.support.app;

import android.os.Bundle;
import com.anubis.module_skin.support.SkinCompatManager;
import com.anubis.module_skin.support.observe.SkinObservable;
import com.anubis.module_skin.support.observe.SkinObserver;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.LayoutInflaterCompat;

/**
 * Created by ximsfei on 17-1-8.
 */

public class SkinCompatActivity extends AppCompatActivity implements SkinObserver {

    private SkinCompatDelegate mSkinDelegate;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        LayoutInflaterCompat.setFactory(getLayoutInflater(), getSkinDelegate());
        super.onCreate(savedInstanceState);
    }

    @NonNull
    public SkinCompatDelegate getSkinDelegate() {
        if (mSkinDelegate == null) {
            mSkinDelegate = SkinCompatDelegate.create(this);
        }
        return mSkinDelegate;
    }

    @Override
    protected void onResume() {
        super.onResume();
        SkinCompatManager.getInstance().addObserver(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SkinCompatManager.getInstance().deleteObserver(this);
    }

    @Override
    public void updateSkin(SkinObservable observable, Object o) {
        getSkinDelegate().applySkin();
    }
}
