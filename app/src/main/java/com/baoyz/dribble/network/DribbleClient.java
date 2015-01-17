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
package com.baoyz.dribble.network;

import com.baoyz.dribble.model.Shot;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Singleton;

import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;
import timber.log.Timber;

/**
 * Created by baoyz on 15/1/10.
 */
@Singleton
public class DribbleClient {

    private Map<Section, List<Shot>> mShotCache = new HashMap<Section, List<Shot>>();
    private Map<Section, PublishSubject<List<Shot>>> mShotRequestCache = new HashMap<Section, PublishSubject<List<Shot>>>();

    private DribbleApi mApi;

    public DribbleClient(DribbleApi mApi) {
        this.mApi = mApi;
    }

    public Subscription shots(String list, Integer page, Observer<List<Shot>> observer) {

        final Section section = new Section(list, page);

        Timber.e(mShotCache.toString());

        List<Shot> shots = mShotCache.get(section);
        if (shots != null)
            observer.onNext(shots);

        PublishSubject<List<Shot>> request = mShotRequestCache.get(section);
        if (request != null) {
            return request.subscribe(observer);
        }

        request = PublishSubject.create();
        mShotRequestCache.put(section, request);
        request.subscribe(observer);
        request.subscribe(new Observer<List<Shot>>() {
            @Override
            public void onCompleted() {
                mShotRequestCache.remove(section);
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(List<Shot> shots) {
                Timber.e(".........onNext " + shots.toString());
                mShotCache.put(section, shots);
                Timber.e(mShotCache.toString());
            }
        });

        return mApi.shots(list, page)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(request);
    }

    static class Section {

        String list;
        Integer page;

        Section(String list, Integer page) {
            this.list = list;
            this.page = page;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Section section = (Section) o;

            if (list != null ? !list.equals(section.list) : section.list != null) return false;
            if (page != null ? !page.equals(section.page) : section.page != null) return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result = list != null ? list.hashCode() : 0;
            result = 31 * result + (page != null ? page.hashCode() : 0);
            return result;
        }

        @Override
        public String toString() {
            return "Section{" +
                    "list='" + list + '\'' +
                    ", page=" + page +
                    '}';
        }
    }
}
