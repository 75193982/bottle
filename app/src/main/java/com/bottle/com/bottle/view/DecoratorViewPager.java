package com.bottle.com.bottle.view;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewGroup;

/**
 * viewpage 和listview 相互冲突 将父view 传递到viewpage 里面
 *
 * 使用父类的方法 parent.requestDisallowInterceptTouchEvent(true);
 *
 * 当 requestDisallowInterceptTouchEvent 如果为true的时候 表示:父view 不拦截子view的touch 事件
 *
 * 这个方法只是改变flag
 *
 * @author
 *
 */
public class DecoratorViewPager extends ViewPager {
    //mViewTouchMode表示ViewPager是否全权控制滑动事件，默认为false，即不控制
    private boolean mViewTouchMode = false;

    public DecoratorViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setViewTouchMode(boolean b) {
        if (b && !isFakeDragging()) {
//全权控制滑动事件
            beginFakeDrag();
        } else if (!b && isFakeDragging()) {
//终止控制滑动事件
            endFakeDrag();
        }
        mViewTouchMode = b;
    }

    /**
     * 在mViewTouchMode为true的时候，ViewPager不拦截点击事件，点击事件将由子View处理
     */
    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        if (mViewTouchMode) {
            return false;
        }
        return super.onInterceptTouchEvent(event);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        try {
            return super.onTouchEvent(ev);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 在mViewTouchMode为true或者滑动方向不是左右的时候，ViewPager将放弃控制点击事件，
     * 这样做有利于在ViewPager中加入ListView等可以滑动的控件，否则两者之间的滑动将会有冲突
     */
    @Override
    public boolean arrowScroll(int direction) {
        if (mViewTouchMode) return false;
        if (direction != FOCUS_LEFT && direction != FOCUS_RIGHT) return false;
        return super.arrowScroll(direction);
    }

}
