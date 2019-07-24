package com.anubis.module_navi.custom;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewParent;
import android.widget.ScrollView;

public class BNScrollView extends ScrollView {

    public BNScrollView(Context context) {
        super(context);
    }

    public BNScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BNScrollView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        ViewParent parent = this.getParent();
        while (parent != null) {
            if (parent instanceof BNScrollLayout) {
                ((BNScrollLayout) parent).setAssociatedScrollView(this);
                break;
            }
            parent = parent.getParent();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        ViewParent parent = this.getParent();
        if (parent instanceof BNScrollLayout) {
            if (((BNScrollLayout) parent).getCurrentStatus() == BNScrollLayout.Status.OPENED) {
                return false;
            }
        }
        return super.onTouchEvent(ev);
    }

    public interface OnScrollChangedListener {
        void onScrollChanged(int l, int t, int oldl, int oldt);
    }

    private OnScrollChangedListener listener;

    public void setOnScrollChangeListener(OnScrollChangedListener listener) {
        this.listener = listener;
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        if (listener != null) {
            listener.onScrollChanged(l, t, oldl, oldt);
        }
    }
}
