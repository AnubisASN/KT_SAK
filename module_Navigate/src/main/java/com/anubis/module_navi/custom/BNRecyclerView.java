package com.anubis.module_navi.custom;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;

import com.anubis.module_navi.NormalUtils;


public class BNRecyclerView extends RecyclerView {

    public BNRecyclerView(Context context) {
        this(context, null);
    }

    public BNRecyclerView(Context context, AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public BNRecyclerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initEvent(context);
    }

    private void initEvent(final Context context) {
        getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                ViewGroup.LayoutParams layoutParams = getLayoutParams();
                ViewParent parent = getParent();
                ViewParent rvParent = parent;
                while (parent != null) {
                    if (parent instanceof BNScrollLayout) {
                        int tabHeight = 0;
                        if (rvParent instanceof LinearLayout) {
                            tabHeight = ((LinearLayout) rvParent).getChildAt(0).getMeasuredHeight();
                        }
                        int height = ((BNScrollLayout) parent).getMeasuredHeight()
                                - ((BNScrollLayout) parent).minOffset - tabHeight
                                - NormalUtils.dip2px(context, 15);
                        if (layoutParams.height == height) {
                            return;
                        } else {
                            layoutParams.height = height;
                            break;
                        }
                    }
                    parent = parent.getParent();
                }
                setLayoutParams(layoutParams);
            }
        });
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        ViewParent parent = getParent();
        while (parent != null) {
            if (parent instanceof BNScrollLayout) {
                ((BNScrollLayout) parent).setAssociatedRecyclerView(this);
                break;
            }
            parent = parent.getParent();
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }
}
