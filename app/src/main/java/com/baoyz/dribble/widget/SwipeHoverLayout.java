/*
 * The MIT License (MIT)
 * 
 * Copyright (c) 2015 baoyongzhang <baoyz94@gmail.com>
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.baoyz.dribble.widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v4.view.MotionEventCompat;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.baoyz.dribble.R;
import com.baoyz.dribble.util.DimenUtil;
import com.daimajia.androidviewhover.tools.Blur;
import com.daimajia.androidviewhover.tools.Util;

/**
 * Created by baoyz on 15/1/19.
 */
public class SwipeHoverLayout extends RelativeLayout {

    public static final int ANIMATION_DURATION = 500;

    private int mScrollDistance;
    private int mFlingVelocity;
    private GestureDetectorCompat mGestureDetector;

    private boolean mFlingShow;
    private boolean mDistanceShow;
    private int mDownX;
    private int mMaxDistance;

    private int mHoverState = HoverState.HIDE;

    private View mHoverView;

    static class HoverState {
        public static final int HIDE = 0x0000;
        public static final int SHOW = ~HIDE;
    }

    public SwipeHoverLayout(Context context) {
        super(context);
        init();
    }

    public SwipeHoverLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SwipeHoverLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public SwipeHoverLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {

        mHoverView = new ImageView(getContext());
        mHoverView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));

        ViewConfiguration viewConfiguration = ViewConfiguration.get(getContext());
        mScrollDistance = DimenUtil.dp2px(getContext(), 60);
        mFlingVelocity = viewConfiguration.getScaledMinimumFlingVelocity();

        mGestureDetector = new GestureDetectorCompat(getContext(), new GestureDetector.SimpleOnGestureListener() {

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
//                Timber.e("fling " + velocityX);
                mFlingShow = Math.abs(velocityX) >= mFlingVelocity;
                return mFlingShow;
            }

            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                int distance = Math.abs((int) (e2.getX() - mDownX));
//                Timber.e("distance " + distance);
                mDistanceShow = distance >= mScrollDistance;
                dispatchSwipe(distance);
                return mDistanceShow;
            }

            @Override
            public boolean onDown(MotionEvent e) {
                mDownX = (int) e.getX();
                mFlingShow = false;
                mDistanceShow = false;
                return true;
            }

        });
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mHoverView = findViewById(R.id.hoverView);
        mHoverView.setVisibility(View.GONE);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mMaxDistance = w;
    }

    private void dispatchSwipe(int distance) {
        float percent = (float) distance / mMaxDistance;
        switch (mHoverState) {
            case HoverState.HIDE:
                swipeShow(percent);
                break;
            case HoverState.SHOW:
                swipeHide(percent);
                break;
        }
    }

    private void swipeHide(float percent) {
        ensureHoverView();
        mHoverView.setAlpha(1 - percent);
    }

    private void swipeShow(float percent) {
        ensureHoverView();
        mHoverView.setAlpha(percent);
    }

    private void startSmoothAnimation(final int state) {
        ensureHoverView();
        switch (state) {
            case HoverState.HIDE:
                mHoverView.animate().alpha(0f)
                        .setDuration(ANIMATION_DURATION)
                        .setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                super.onAnimationEnd(animation);
                                mHoverState = state;
                                destroyHover();
                            }
                        }).start();
                break;
            case HoverState.SHOW:
                mHoverView.animate().alpha(1f)
                        .setDuration(ANIMATION_DURATION)
                        .setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                super.onAnimationEnd(animation);
                                mHoverState = state;
                            }
                        }).start();
                break;
        }
    }

    private void ensureHoverView() {
        if (mHoverView.getVisibility() == View.GONE) {
            mHoverView.setVisibility(View.VISIBLE);
            mHoverView.setBackgroundDrawable(generateBlurDrawable());
            mHoverView.setAlpha(0f);
        }
        if (mHoverView.getParent() == null) {
//            addView(mHoverView);
        }
    }

    private Drawable generateBlurDrawable() {
        return new BitmapDrawable(getResources(), Blur.apply(getContext(), Util.getViewBitmap(this)));
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        int actionMasked = MotionEventCompat.getActionMasked(ev);
        switch (actionMasked) {
            case MotionEvent.ACTION_DOWN:
                mDownX = (int) ev.getX();
                break;
            case MotionEvent.ACTION_MOVE:
                break;
            case MotionEvent.ACTION_UP:
                break;
        }
        return true;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        boolean flag = mGestureDetector.onTouchEvent(ev);
        int actionMasked = MotionEventCompat.getActionMasked(ev);
        switch (actionMasked) {
            case MotionEvent.ACTION_CANCEL:
                startSmoothAnimation(mHoverState);
                mDistanceShow = false;
                mFlingShow = false;
                break;
            case MotionEvent.ACTION_UP:
                if (mDistanceShow || mFlingShow) {
                    startSmoothAnimation(~mHoverState);
                } else {
                    startSmoothAnimation(mHoverState);
                }
                mDistanceShow = false;
                mFlingShow = false;
                break;
        }
        return true;
    }

    public View getHoverView() {
        return mHoverView;
    }

    public void destroyHover() {
        if (getHoverView().getVisibility() == View.VISIBLE) {
            getHoverView().setBackgroundDrawable(null);
            getHoverView().setVisibility(View.GONE);
        }
    }
}
