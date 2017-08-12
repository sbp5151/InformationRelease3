package com.jld.InformationRelease.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.RelativeLayout;

/**
 * Created by boping on 2017/8/10.
 */

public class MyRelativeLayout extends RelativeLayout {
    public MyRelativeLayout(Context context) {
        super(context);
    }

    public MyRelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyRelativeLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public interface OnTouchListen {
        void touchListen();
    }

    OnTouchListen mOnTouchListen;

    public void setOnTouchListen(OnTouchListen onTouchListen) {
        mOnTouchListen = onTouchListen;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (mOnTouchListen != null)
            mOnTouchListen.touchListen();
        return super.dispatchTouchEvent(ev);
    }
}
