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
package com.baoyz.dribble.fragment;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;

import com.baoyz.dribble.AppModule;
import com.baoyz.dribble.MainActivity;
import com.baoyz.dribble.R;
import com.baoyz.dribble.adapter.FeedAdapter;
import com.baoyz.dribble.model.Shot;
import com.baoyz.dribble.network.DribbleClient;
import com.baoyz.dribble.util.ToastUtil;
import com.baoyz.dribble.widget.SuperRecyclerView;

import java.util.List;

import javax.inject.Inject;

import butterknife.InjectView;
import jp.wasabeef.recyclerview.animators.LandingAnimator;
import rx.Observer;

/**
 * Created by baoyz on 15/1/12.
 */
public class FeedFragment extends BaseFragment {

    @InjectView(R.id.rv_feed)
    SuperRecyclerView mFeedList;

    @Inject
    DribbleClient mClient;

    private String mList;
    private MainActivity mActivity;
    private int mPage;
    private FeedAdapter mAdapter;
    private Toolbar mToolBar;
    private ViewGroup mContainer;
    private int mHeight;
    private int mTop;

    public static FeedFragment newInstance(String list) {
        FeedFragment f = new FeedFragment();
        f.mList = list;
        return f;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        AppModule.inject(this);

        mToolBar = mActivity.getToolbar();
        mContainer = (ViewGroup) view.getParent();

        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext) {
            @Override
            protected int getExtraLayoutSpace(RecyclerView.State state) {
                return 300;
            }
        };
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mFeedList.setLayoutManager(layoutManager);
        mFeedList.setItemAnimator(new LandingAnimator());

        mAdapter = new FeedAdapter(null);
        mFeedList.setAdapter(mAdapter);

        mFeedList.setOnLoadMoreListener(new SuperRecyclerView.OnLoadMoreListener() {
            @Override
            public void onLoadMore(RecyclerView recyclerView) {
                loadData(mPage + 1);
            }
        });
        mFeedList.post(new Runnable() {
            @Override
            public void run() {
                mHeight = mFeedList.getHeight();
                mTop = mToolBar.getHeight();
                mFeedList.setTranslationY(mTop);
            }
        });
        mFeedList.setOnQuickScrollListener(new SuperRecyclerView.OnQuickScrollListener() {
            @Override
            public void onQuickUp() {
                if (mFeedList.getTranslationY() == mTop) {
                    start(mTop, 0);
                }
            }

            private void start(int start, int end){

                ValueAnimator valueAnimator = ValueAnimator.ofInt(start, end).setDuration(200);
                valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        int animatedValue = (int) animation.getAnimatedValue();
                        mToolBar.setTranslationY(animatedValue - mTop);
                        mFeedList.setTranslationY(animatedValue);
                    }
                });
                valueAnimator.start();
            }

            @Override
            public void onQuickDown() {
                if (mFeedList.getTranslationY() == 0) {
                    start(0, mTop);
                }
            }
        });

        loadData(1);

    }

    @Override
    protected int onGetViewId() {
        return R.layout.fragment_feed;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = (MainActivity) activity;
    }

    private void loadData(int page) {
        mPage = page;
        mClient.shots(mList, mPage, new Observer<List<Shot>>() {
            @Override
            public void onCompleted() {
                mFeedList.setLoadMore(false);
            }

            @Override
            public void onError(Throwable throwable) {
                ToastUtil.showShort(mContext, "error : " + throwable.getMessage());
            }

            @Override
            public void onNext(List<Shot> shots) {
                mAdapter.addList(shots);
            }
        });
    }

}
