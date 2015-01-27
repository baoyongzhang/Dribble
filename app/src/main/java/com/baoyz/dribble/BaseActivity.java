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

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;

import com.baoyz.dribble.util.ToastUtil;

import butterknife.ButterKnife;

/**
 * Created by baoyz on 15/1/24.
 */
public class BaseActivity extends ActionBarActivity {

    private Handler mHandler = new Handler();
    protected BaseActivity mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mContext = this;

        int viewId = onGetViewId();

        if (viewId > 0) {
            setContentView(viewId);
            ButterKnife.inject(this);
            onViewCreated();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        ButterKnife.reset(this);
    }

    protected int onGetViewId() {
        return 0;
    }

    protected void onViewCreated() {

    }

    public void startActivity(Class<? extends Activity> activity) {
        Intent intent = new Intent(mContext, activity);
        startActivity(intent);
    }

    public void startActivityAndFinish(Class<? extends Activity> activity) {
        startActivity(activity);
        finish();
    }

    public void startActivityAndFinish(Intent intent) {
        startActivity(intent);
        finish();
    }

    public void startActivityForResult(Class<? extends Activity> activity,
                                       int requestCode) {
        Intent intent = new Intent(mContext, activity);
        startActivityForResult(intent, requestCode);
    }

    public void showToast(String text) {
        ToastUtil.showShort(this, text);
    }

    public void showToast(int strId) {
        ToastUtil.showShort(this, strId);
    }

    public void post(Runnable task) {
        mHandler.post(task);
    }

    public void post(Runnable task, long delay) {
        mHandler.postDelayed(task, delay);
    }

    public void remove(Runnable task) {
        mHandler.removeCallbacks(task);
    }


    protected void addFragment(int containerId, Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(containerId, fragment);
        transaction.commit();
    }

    protected void replaceFragment(int containerId, Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(containerId, fragment);
        transaction.commit();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
}
