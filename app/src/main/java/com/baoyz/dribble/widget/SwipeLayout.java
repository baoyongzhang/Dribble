package com.baoyz.dribble.widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v4.view.MotionEventCompat;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.FrameLayout;

import com.baoyz.dribble.R;
import com.baoyz.dribble.util.DimenUtil;
import com.daimajia.androidviewhover.tools.Blur;
import com.daimajia.androidviewhover.tools.Util;

public class SwipeLayout extends FrameLayout {

    public static final int ANIMATION_DURATION = 500;

    private int mScrollDistance;
    private int mCanScrollDistance;
    private int mFlingVelocity;
    private GestureDetectorCompat mGestureDetector;

    private boolean mFlingShow;
    private boolean mDistanceShow;
    private int mDownX;
    private int mMaxDistance;
    private int mTouchSlop;

    private int mHoverState = HoverState.HIDE;

    private View mHoverView;

    static class HoverState {
        public static final int HIDE = 0x0000;
        public static final int SHOW = ~HIDE;
    }

    public SwipeLayout(Context context) {
        super(context);
        init();
    }

    public SwipeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SwipeLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {

        ViewConfiguration viewConfiguration = ViewConfiguration.get(getContext());
        mScrollDistance = DimenUtil.dp2px(getContext(), 60);
        mCanScrollDistance = viewConfiguration.getScaledOverscrollDistance();
        mFlingVelocity = viewConfiguration.getScaledMinimumFlingVelocity();
        mTouchSlop = viewConfiguration.getScaledTouchSlop();

        mGestureDetector = new GestureDetectorCompat(getContext(), new GestureDetector.SimpleOnGestureListener() {

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                mFlingShow = Math.abs(velocityX) >= mFlingVelocity;
                return false;
            }

            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                int distance = Math.abs((int) (e2.getX() - mDownX));
                mDistanceShow = distance >= 10;
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
        if (distance < mCanScrollDistance)
            return;
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
        boolean flag = false;
        switch (actionMasked) {
            case MotionEvent.ACTION_DOWN:
                mDownX = (int) ev.getX();
                flag = true;
                break;
            case MotionEvent.ACTION_MOVE:
                if (Math.abs(ev.getX() - mDownX) > mTouchSlop)
                    flag = true;
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                flag = true;
                break;
        }
        return flag;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
//        boolean flag = mGestureDetector.onTouchEvent(ev);
//        int actionMasked = MotionEventCompat.getActionMasked(ev);
//        switch (actionMasked) {
//            case MotionEvent.ACTION_CANCEL:
//                startSmoothAnimation(mHoverState);
//                mDistanceShow = false;
//                mFlingShow = false;
//                break;
//            case MotionEvent.ACTION_UP:
//                if (mDistanceShow || mFlingShow) {
//                    startSmoothAnimation(~mHoverState);
//                } else {
//                    startSmoothAnimation(mHoverState);
//                }
//                mDistanceShow = false;
//                mFlingShow = false;
//                break;
//        }
//        return flag;
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

//    @Override
//    public boolean onInterceptTouchEvent(MotionEvent ev) {
//        return true;
//    }
//
//
//    @Override
//    public boolean onTouchEvent(MotionEvent event) {
//        return true;
//    }


}
