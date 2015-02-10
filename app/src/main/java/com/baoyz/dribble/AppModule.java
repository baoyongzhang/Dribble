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

import android.app.Application;
import android.net.Uri;

import com.baoyz.dribble.adapter.FeedAdapter;
import com.baoyz.dribble.fragment.FeedFragment;
import com.baoyz.dribble.network.DribbleApi;
import com.baoyz.dribble.network.DribbleClient;
import com.squareup.okhttp.Cache;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.picasso.OkHttpDownloader;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;

import javax.inject.Singleton;

import dagger.Module;
import dagger.ObjectGraph;
import dagger.Provides;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import timber.log.Timber;

/**
 * Created by baoyz on 15/1/12.
 */
@Module(
        injects = {
                FeedFragment.class,
                FeedAdapter.class,
                DetailActivity.class,
        }
)
public class AppModule {

    static final int DISK_CACHE_SIZE = 50 * 1024 * 1024; // 50MB
    private static ObjectGraph objectGraph;

    @Provides
    @Singleton
    Application provideApplication() {
        return App.getInstance();
    }

    @Provides
    @Singleton
    public OkHttpClient provideOkHttpClient(Application app) {
        return createOkHttpClient(app);
    }

    @Provides
    @Singleton
    public Picasso providePicasso(Application app, OkHttpClient client) {
        return new Picasso.Builder(app)
                .indicatorsEnabled(BuildConfig.DEBUG)
                .downloader(new OkHttpDownloader(client))
                .listener(new Picasso.Listener() {
                    @Override
                    public void onImageLoadFailed(Picasso picasso, Uri uri, Exception e) {
                        Timber.e(e, "Failed to load image: %s", uri);
                    }
                })
                .build();
    }

    @Provides
    @Singleton
    public DribbleClient provideDribbleClient(DribbleApi api) {
        return new DribbleClient(api);
    }

    @Provides
    @Singleton
    public DribbleApi provideDribbleApi(RestAdapter adapter) {
        return adapter.create(DribbleApi.class);
    }

    @Provides
    @Singleton
    public RestAdapter provideRestAdapter() {
        return new RestAdapter.Builder().setEndpoint("https://api.dribbble.com/v1")
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .setLog(new RestAdapter.Log() {
                    @Override
                    public void log(String message) {
                        Timber.d(message);
                    }
                }).setRequestInterceptor(new RequestInterceptor() {
                    @Override
                    public void intercept(RequestFacade request) {
                        request.addHeader("Authorization", "Bearer e45ddc3e0eecc44335e796e329ca265dad1cd1a0b7bd2e5459b426f2595e147c");
                    }
                }).build();
    }

    public static void inject(Object target) {
        if (objectGraph == null)
            objectGraph = ObjectGraph.create(new AppModule());
        objectGraph.inject(target);
    }

    static OkHttpClient createOkHttpClient(Application app) {
        OkHttpClient client = new OkHttpClient();

        try {
            File cacheDir = new File(app.getCacheDir(), "http");
            Cache cache = new Cache(cacheDir, DISK_CACHE_SIZE);
            client.setCache(cache);
        } catch (IOException e) {
            Timber.e(e, "Unable to install disk cache.");
        }

        return client;
    }
}
