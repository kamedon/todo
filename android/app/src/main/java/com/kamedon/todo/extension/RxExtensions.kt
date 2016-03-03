package com.kamedon.todo.extension

import com.trello.rxlifecycle.ActivityLifecycleProvider
import rx.Observable
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

inline fun <T> ActivityLifecycleProvider.observable(observable: Observable<T>) =
        observable.compose(this.bindToLifecycle<T>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())

inline fun <T> ActivityLifecycleProvider.observable(observable: Observable<T>, subscriber: Subscriber<T>) =
        this.observable(observable).subscribe(subscriber)


