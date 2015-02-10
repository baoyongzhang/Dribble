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
package com.baoyz.dribble.util;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

/**
 * Created by baoyz on 15/1/13.
 */
public class ViewUtil {

    public static Rect getFrameInWindow(View view) {
        int[] location = new int[2];
        view.getLocationInWindow(location);
        return new Rect(location[0], location[1], location[0] + view.getWidth(), location[1] + view.getHeight());
    }

    public static void setTextViewBottomDrawable(TextView tv, int resId) {
        setTextViewBottomDrawable(tv, tv.getContext().getResources().getDrawable(resId));
    }

    public static void setTextViewBottomDrawable(TextView tv, Drawable drawable) {
        if (drawable == null)
            drawable = new ColorDrawable(Color.TRANSPARENT);
        tv.setCompoundDrawablesWithIntrinsicBounds(tv.getCompoundDrawables()[0], tv.getCompoundDrawables()[1], tv.getCompoundDrawables()[2], drawable);
    }

    public static void setTextViewLeftDrawable(TextView tv, int resId) {
        setTextViewLeftDrawable(tv, tv.getContext().getResources().getDrawable(resId));
    }

    public static void setTextViewRightDrawable(TextView tv, int resId) {
        setTextViewRightDrawable(tv, tv.getContext().getResources().getDrawable(resId));
    }

    public static void setTextViewLeftDrawable(TextView tv, Drawable drawable) {
        if (drawable == null)
            drawable = new ColorDrawable(Color.TRANSPARENT);
        tv.setCompoundDrawablesWithIntrinsicBounds(drawable, tv.getCompoundDrawables()[1], tv.getCompoundDrawables()[2], tv.getCompoundDrawables()[3]);
    }

    public static void setTextViewRightDrawable(TextView tv, Drawable drawable) {
        if (drawable == null)
            drawable = new ColorDrawable(Color.TRANSPARENT);
        tv.setCompoundDrawablesWithIntrinsicBounds(tv.getCompoundDrawables()[0], tv.getCompoundDrawables()[1], drawable, tv.getCompoundDrawables()[3]);
    }

    public static View inflateAndMeasure(Context context, int resource) {
        View view = View.inflate(context, resource, null);
        view.measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED),
                MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
        return view;
    }

    /**
     * 隐藏软键盘
     */
    public static void hiddenKeyboard(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm.isActive(view)) {
            // imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0); // 强制隐藏键盘
            // view.clearFocus();
        }
    }

    /**
     * 显示软键盘
     */
    public static void showKeyboard(Context context, View view) {
        view.setFocusable(true);
        view.setFocusableInTouchMode(true);
        InputMethodManager imm = (InputMethodManager) context
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(view, InputMethodManager.RESULT_SHOWN);
//		imm.restartInput(view);
//        imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
    }

    /**
     * 将view更改为处理任何触摸事件
     *
     * @param view
     */
    public static void handleTouch(View view) {
        if (view != null) {
            view.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    return true;
                }
            });
        }
    }

    public static void setText(TextView textView, int color, String str) {
        if (textView != null) {
            if (textView.getCurrentTextColor() != color)
                textView.setTextColor(color);
            textView.setText(str);
        }
    }

    /**
     * set view height
     *
     * @param view
     * @param height
     */
    public static void setViewHeight(View view, int height) {
        if (view == null) {
            return;
        }

        ViewGroup.LayoutParams params = view.getLayoutParams();
        params.height = height;
    }

    /**
     * set view widht
     *
     * @param view
     * @param width
     */
    public static void setViewWidth(View view, int width) {
        if (view == null) {
            return;
        }

        ViewGroup.LayoutParams params = view.getLayoutParams();
        params.width = width;
    }
}
