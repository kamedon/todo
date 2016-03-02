package com.kamedon.todo.extension

import android.support.design.widget.Snackbar
import android.util.Log
import com.kamedon.todo.entity.api.DeleteTaskResponse
import com.trello.rxlifecycle.components.support.RxAppCompatActivity
import rx.Observable
import rx.Subscriber
import rx.Subscription
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

inline fun <T> Observable<T>.buildScheduler(activity: RxAppCompatActivity): Observable<T> =
        this.compose(activity.bindToLifecycle<T>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())

inline fun <T> Observable<T>.execute(activity: RxAppCompatActivity, subscriber: Subscriber<T>): Subscription =
        this.buildScheduler(activity).subscribe(subscriber)


