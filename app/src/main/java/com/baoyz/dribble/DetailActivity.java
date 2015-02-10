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
package com.baoyz.dribble;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Rect;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.baoyz.dribble.model.Shot;
import com.baoyz.dribble.util.ImageViewTarget;
import com.baoyz.dribble.util.ViewUtil;
import com.squareup.picasso.Picasso;

import javax.inject.Inject;

import butterknife.InjectView;

/**
 * Created by baoyz on 15/1/24.
 */
public class DetailActivity extends BaseActivity {

    public static String EXTRA_SHOT = "extra_shot";

    private static View sharedView;

    private Shot mShot;
    private Rect mImageViewFrame;

    public Context mContext;

    @InjectView(R.id.iv_image)
    ImageView mImageView;
    @InjectView(R.id.tv_description)
    TextView mDescriptionTv;
    @InjectView(R.id.tv_username)
    TextView mUsernameTv;
    @InjectView(R.id.iv_avatar)
    ImageView mAvatarIv;

    @InjectView(R.id.container)
    View mContainerView;
    @InjectView(R.id.ll_content)
    View mContentView;

    @InjectView(R.id.toolbar)
    Toolbar mToolbar;

    @Inject
    Picasso mPicasso;

    public static void start(Activity context, Shot shot, View view) {
        sharedView = view;
        context.overridePendingTransition(0, 0);
        Intent intent = new Intent(context, DetailActivity.class);
        intent.putExtra(EXTRA_SHOT, shot);
        context.startActivity(intent);

    }

    @Override
    protected int onGetViewId() {
        return R.layout.activity_detail;
    }

    @Override
    protected void onViewCreated() {
        super.onViewCreated();

        AppModule.inject(this);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mToolbar.setTitle("");
        mToolbar.setBackgroundColor(Color.TRANSPARENT);

        mShot = getIntent().getParcelableExtra(EXTRA_SHOT);
        mImageViewFrame = ViewUtil.getFrameInWindow(sharedView);

        mPicasso.load(mShot.getImages().getHidpi()).into(new ImageViewTarget(mImageView) {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                super.onBitmapLoaded(bitmap, from);
                Palette.generateAsync(bitmap, new Palette.PaletteAsyncListener() {
                    @Override
                    public void onGenerated(Palette palette) {
                        Palette.Swatch swatch = palette.getDarkMutedSwatch();
                        if (swatch == null)
                            swatch = palette.getMutedSwatch();
                        if (swatch == null)
                            swatch = palette.getLightMutedSwatch();
                        if (swatch != null && Color.alpha(swatch.getRgb()) != 0) {
                            mContainerView.setBackgroundColor(swatch.getRgb());
                            mUsernameTv.setTextColor(swatch.getTitleTextColor());
                        }
                    }
                });
            }
        });
        mPicasso.load(mShot.getUser().getAvatar_url()).into(mAvatarIv);
        mUsernameTv.setText(mShot.getUser().getName());
        mDescriptionTv.setText(Html.fromHtml(mShot.getDescription()));

        mContainerView.setAlpha(0);
        mContentView.setAlpha(0);

        mImageView.setTranslationY(mImageViewFrame.top - getResources().getDimensionPixelSize(R.dimen.statusMargin));
        mImageView.post(new Runnable() {
            @Override
            public void run() {
                float scale = (float) mImageViewFrame.width() / mImageView.getWidth();
                mImageView.setScaleX(scale);
                mImageView.setScaleY(scale);
                mImageView.animate().translationY(0).scaleX(1).scaleY(1).setDuration(300).setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        mContentView.animate().alpha(1).setDuration(300).start();
                    }
                }).start();
                mContainerView.animate().alpha(1).setDuration(300).start();
            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        sharedView = null;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
