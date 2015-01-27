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
package com.baoyz.dribble.adapter;

import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.baoyz.dribble.AppModule;
import com.baoyz.dribble.DetailActivity;
import com.baoyz.dribble.R;
import com.baoyz.dribble.model.Shot;
import com.baoyz.dribble.widget.CircleImageView;
import com.baoyz.dribble.widget.SwipeHoverLayout;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by baoyz on 15/1/13.
 */
public class FeedAdapter extends BaseAdapter<FeedAdapter.ViewHolder> {

    private List<Shot> mList;

    @Inject
    Picasso mPicasso;

    public FeedAdapter(List<Shot> list) {
        this.mList = list;
        if (mList == null)
            mList = new ArrayList<Shot>();

        AppModule.inject(this);
    }

    public void addList(List<Shot> list) {
        mList.addAll(list);
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_feed, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);

        Shot shot = mList.get(position);
        mPicasso.load(shot.getUser().getAvatar_url()).into(holder.mIvAvatar);
        holder.mTvUsername.setText(shot.getUser().getName());
        holder.mTvComment.setText(shot.getComments_count().toString());
        holder.mTvFavorite.setText(shot.getLikes_count().toString());
        holder.mTvViews.setText(shot.getViews_count().toString());
        mPicasso.load(shot.getImages().getHidpi()).into(holder.mIvImage);
        holder.mTvDescription.setText(Html.fromHtml(shot.getDescription()));
        holder.mHoverLayout.destroyHover();
        holder.mIvImage.setTag(shot);
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public static class ViewHolder extends BaseAdapter.ViewHolder {

        @InjectView(R.id.iv_avatar)
        CircleImageView mIvAvatar;
        @InjectView(R.id.tv_username)
        TextView mTvUsername;
        @InjectView(R.id.iv_image)
        ImageView mIvImage;
        @InjectView(R.id.tv_views)
        TextView mTvViews;
        @InjectView(R.id.tv_comment)
        TextView mTvComment;
        @InjectView(R.id.tv_favorite)
        TextView mTvFavorite;
        @InjectView(R.id.tv_description)
        TextView mTvDescription;
        @InjectView(R.id.hoverLayout)
        SwipeHoverLayout mHoverLayout;

        public ViewHolder(View itemView) {
            super(itemView);
        }

        @OnClick(R.id.iv_image)
        public void onClick(View view) {
            DetailActivity.start(view.getContext(), (Shot) view.getTag());
        }
    }
}
