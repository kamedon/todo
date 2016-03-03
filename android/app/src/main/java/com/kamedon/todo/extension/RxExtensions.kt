package com.kamedon.todo.extension

import android.support.design.widget.Snackbar
import android.util.Log
import com.kamedon.todo.entity.api.DeleteTaskResponse
import com.trello.rxlifecycle.ActivityLifecycleProvider
import com.trello.rxlifecycle.components.support.RxAppCompatActivity
import rx.Observable
import rx.Subscriber
import rx.Subscription
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

inline fun <T> ActivityLifecycleProvider.observable(observable: Observable<T>) =
        observable.compose(this.bindToLifecycle<T>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())

inline fun <T> ActivityLifecycleProvider.observable(observable: Observable<T>, subscriber: Subscriber<T>) =
        this.observable(observable).subscribe(subscriber)


