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
package com.baoyz.dribble.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by baoyz on 15/1/11.
 */
public class Shot implements Parcelable {

    private String id;
    private String title;
    private String description;
    private Integer width;
    private Integer height;
    private Images images;
    private Integer views_count;
    private Integer likes_count;
    private Integer comments_count;
    private String created_at;
    private String updated_at;
    private List<String> tags;
    private User user;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        if (TextUtils.isEmpty(description))
            return "";
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getWidth() {
        return width;
    }

    public void setWidth(Integer width) {
        this.width = width;
    }

    public Integer getHeight() {
        return height;
    }

    public void setHeight(Integer height) {
        this.height = height;
    }

    public Images getImages() {
        return images;
    }

    public void setImages(Images images) {
        this.images = images;
    }

    public Integer getViews_count() {
        return views_count;
    }

    public void setViews_count(Integer views_count) {
        this.views_count = views_count;
    }

    public Integer getLikes_count() {
        return likes_count;
    }

    public void setLikes_count(Integer likes_count) {
        this.likes_count = likes_count;
    }

    public Integer getComments_count() {
        return comments_count;
    }

    public void setComments_count(Integer comments_count) {
        this.comments_count = comments_count;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }



    public static class Images implements Parcelable {

        private String hidpi;
        private String normal;
        private String teaser;

        public String getHidpi() {
            return hidpi;
        }

        public void setHidpi(String hidpi) {
            this.hidpi = hidpi;
        }

        public String getNormal() {
            return normal;
        }

        public void setNormal(String normal) {
            this.normal = normal;
        }

        public String getTeaser() {
            return teaser;
        }

        public void setTeaser(String teaser) {
            this.teaser = teaser;
        }


        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(this.hidpi);
            dest.writeString(this.normal);
            dest.writeString(this.teaser);
        }

        public Images() {
        }

        private Images(Parcel in) {
            this.hidpi = in.readString();
            this.normal = in.readString();
            this.teaser = in.readString();
        }

        public static final Parcelable.Creator<Images> CREATOR = new Parcelable.Creator<Images>() {
            public Images createFromParcel(Parcel source) {
                return new Images(source);
            }

            public Images[] newArray(int size) {
                return new Images[size];
            }
        };
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.title);
        dest.writeString(this.description);
        dest.writeValue(this.width);
        dest.writeValue(this.height);
        dest.writeParcelable(this.images, 0);
        dest.writeValue(this.views_count);
        dest.writeValue(this.likes_count);
        dest.writeValue(this.comments_count);
        dest.writeString(this.created_at);
        dest.writeString(this.updated_at);
        dest.writeList(this.tags);
        dest.writeParcelable(this.user, flags);
    }

    public Shot() {
    }

    private Shot(Parcel in) {
        this.id = in.readString();
        this.title = in.readString();
        this.description = in.readString();
        this.width = (Integer) in.readValue(Integer.class.getClassLoader());
        this.height = (Integer) in.readValue(Integer.class.getClassLoader());
        this.images = in.readParcelable(Images.class.getClassLoader());
        this.views_count = (Integer) in.readValue(Integer.class.getClassLoader());
        this.likes_count = (Integer) in.readValue(Integer.class.getClassLoader());
        this.comments_count = (Integer) in.readValue(Integer.class.getClassLoader());
        this.created_at = in.readString();
        this.updated_at = in.readString();
        this.tags = new ArrayList<String>();
        in.readList(this.tags, List.class.getClassLoader());
        this.user = in.readParcelable(User.class.getClassLoader());
    }

    public static final Parcelable.Creator<Shot> CREATOR = new Parcelable.Creator<Shot>() {
        public Shot createFromParcel(Parcel source) {
            return new Shot(source);
        }

        public Shot[] newArray(int size) {
            return new Shot[size];
        }
    };
}
