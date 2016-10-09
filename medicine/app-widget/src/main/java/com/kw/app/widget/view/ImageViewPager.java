package com.kw.app.widget.view;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * @Decription ViewPager类，（此类只是为了防止双指缩放过小而出异常）
 */
public class ImageViewPager extends ViewPager {

    public ImageViewPager(Context context) {
        super(context);
    }

    public ImageViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent arg0) {
        boolean b = false;
        try {
            b = super.onInterceptTouchEvent(arg0);
        } catch (Exception e) {

        }
        return b; //网上看的方法是直接返回false，但是会导致ViewPager翻页有BUG
    }

    @Override
    public boolean onTouchEvent(MotionEvent arg0) {
        try {
            super.onTouchEvent(arg0);
        } catch (Exception e) {

        }
        return false;
    }

}