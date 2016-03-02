package com.kamedon.todo.extension

import com.trello.rxlifecycle.components.support.RxAppCompatActivity
import rx.Observable
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

inline fun <T> Observable<T>.buildScheduler(activity: RxAppCompatActivity): Observable<T> =
        this.compose(activity.bindToLifecycle<T>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
