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

import android.content.Context;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;

import com.baoyz.dribble.R;

/**
 * Created by baoyz on 15/1/16.
 */
public class SuperRecyclerView extends RecyclerView {

    private OnScrollListener mScrollListener;
    private boolean isLoading;
    private WrapperAdapter mAdapter;
    private OnLoadMoreListener mOnLoadMoreListener;
    private float mDownX;
    private boolean mInterceptTouch;
    private int mTouchDistance;
    private float mDownY;
    private OnQuickScrollListener mOnQuickScrollListener;
    private GestureDetectorCompat mGesutureDetector;
    private int mFlingVelocity;

    public SuperRecyclerView(Context context) {
        this(context, null);
    }

    public SuperRecyclerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SuperRecyclerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        super.setOnScrollListener(new OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    LinearLayoutManager layoutManager = (LinearLayoutManager) getLayoutManager();
                    if (layoutManager.findLastVisibleItemPosition() == (layoutManager.getItemCount() - 1)) {
                        setLoadMore(true);
                    }
                }

                if (mScrollListener != null)
                    mScrollListener.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (mScrollListener != null)
                    mScrollListener.onScrolled(recyclerView, dx, dy);
            }
        });

        ViewConfiguration viewConfiguration = ViewConfiguration.get(context);
        mTouchDistance = viewConfiguration.getScaledOverflingDistance();
        mFlingVelocity = viewConfiguration.getScaledMinimumFlingVelocity();

        mGesutureDetector = new GestureDetectorCompat(context, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                if (mOnQuickScrollListener != null && Math.abs(velocityY) > mFlingVelocity) {
                    if (velocityY > 0)
                        mOnQuickScrollListener.onQuickDown();
                    else
                        mOnQuickScrollListener.onQuickUp();
                }
                return super.onFling(e1, e2, velocityX, velocityY);
            }
        });
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent e) {

        boolean b = super.onInterceptTouchEvent(e);

        int actionMasked = MotionEventCompat.getActionMasked(e);
        switch (actionMasked) {
            case MotionEvent.ACTION_DOWN:
                mDownX = e.getX();
                mDownY = e.getY();
                mInterceptTouch = false;
                break;
            case MotionEvent.ACTION_MOVE:
                float offsetX = e.getX() - mDownX;
                if (!b && Math.abs(offsetX) > mTouchDistance)
                    mInterceptTouch = true;
                break;
        }

        if (mInterceptTouch)
            return false;

        return b;
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        mGesutureDetector.onTouchEvent(e);
        return super.onTouchEvent(e);
    }

    public void setLoadMore(boolean load) {
        if (isLoading == load)
            return;
        isLoading = load;
        mAdapter.setFooterVisibility(isLoading ? View.VISIBLE : View.GONE);
        if (isLoading && mOnLoadMoreListener != null)
            mOnLoadMoreListener.onLoadMore(this);
        mAdapter.notifyDataSetChanged();
    }

    public void setOnLoadMoreListener(OnLoadMoreListener listener) {
        mOnLoadMoreListener = listener;
    }

    public void setOnQuickScrollListener(OnQuickScrollListener listener) {
        mOnQuickScrollListener = listener;
    }

    @Override
    public void setOnScrollListener(OnScrollListener listener) {
        mScrollListener = listener;
    }

    @Override
    public void setAdapter(Adapter adapter) {
        mAdapter = new WrapperAdapter(adapter);
        super.setAdapter(mAdapter);
    }

    static class FooterViewHolder extends ViewHolder {

        View mItemView;

        public FooterViewHolder(View itemView) {
            super(itemView);
            mItemView = itemView;
        }
    }

    static class WrapperAdapter extends Adapter {

        public static final int TYPE_FOOTER = 0xFFFF01;

        private Adapter mBase;
        private int mFooterVisibility;

        public WrapperAdapter(Adapter adapter) {
            super();
            mBase = adapter;
        }

        public void setFooterVisibility(int visibility) {
            mFooterVisibility = visibility;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if (viewType == TYPE_FOOTER)
                return new FooterViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.footer_load, parent, false));
            return mBase.onCreateViewHolder(parent, viewType);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            if (position == (getItemCount() - 1)) {
                FooterViewHolder vh = (FooterViewHolder) holder;
//                vh.mItemView.setVisibility(mFooterVisibility);
                return;
            }
            mBase.onBindViewHolder(holder, position);
        }

        @Override
        public int getItemViewType(int position) {
            if (position == (getItemCount() - 1))
                return TYPE_FOOTER;
            return mBase.getItemViewType(position);
        }

        @Override
        public void setHasStableIds(boolean hasStableIds) {
            mBase.setHasStableIds(hasStableIds);
        }

        @Override
        public long getItemId(int position) {
            return mBase.getItemId(position);
        }

        @Override
        public int getItemCount() {
            return mBase.getItemCount() + 1;
        }

        @Override
        public void onViewRecycled(ViewHolder holder) {
            mBase.onViewRecycled(holder);
        }

        @Override
        public void onViewAttachedToWindow(ViewHolder holder) {
            mBase.onViewAttachedToWindow(holder);
        }

        @Override
        public void onViewDetachedFromWindow(ViewHolder holder) {
            mBase.onViewDetachedFromWindow(holder);
        }

        @Override
        public void registerAdapterDataObserver(AdapterDataObserver observer) {
            mBase.registerAdapterDataObserver(observer);
        }

        @Override
        public void unregisterAdapterDataObserver(AdapterDataObserver observer) {
            mBase.unregisterAdapterDataObserver(observer);
        }

        @Override
        public void onAttachedToRecyclerView(RecyclerView recyclerView) {
            mBase.onAttachedToRecyclerView(recyclerView);
        }

        @Override
        public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
            mBase.onDetachedFromRecyclerView(recyclerView);
        }
    }

//    @Override
//    public int getChildCount() {
//        return super.getChildCount() - 1;
//    }

    public static interface OnLoadMoreListener {
        public void onLoadMore(RecyclerView recyclerView);
    }

    public static interface OnQuickScrollListener {
        public void onQuickUp();

        public void onQuickDown();
    }
}
