package com.kamedon.todo.extension

import android.app.Activity
import android.content.Intent
import kotlin.reflect.KClass

/**
 * Created by kamedon on 3/3/16.
 */
inline fun <T : Activity> Activity.buildIntent(targetActivity: Class<T>) =
        Intent(applicationContext, targetActivity)
