package com.kamedon.todo

import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.inputmethod.InputMethodManager
import com.kamedon.todo.anim.TaskFormAnimation
import com.kamedon.todo.api.TodoApi
import com.kamedon.todo.builder.ApiClientBuilder
import com.kamedon.todo.builder.TodoApiBuilder
import com.kamedon.todo.entity.Task
import com.kamedon.todo.entity.api.NewTaskQuery
import com.kamedon.todo.entity.api.NewTaskResponse
import com.kamedon.todo.entity.api.NewUserResponse
import com.kamedon.todo.service.ApiKeyService
import kotlinx.android.synthetic.main.activity_task.*
import kotlinx.android.synthetic.main.content_task.*
import kotlinx.android.synthetic.main.content_task.view.*
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

/**
 * Created by kamedon on 2/29/16.
 */
class TaskActivity : AppCompatActivity() {
    lateinit var taskFormAnimation: TaskFormAnimation

    lateinit var api: TodoApi.TaskApi


    lateinit var inputMethodManager: InputMethodManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task)
        val toolbar = findViewById(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)
        taskFormAnimation = TaskFormAnimation(layout_register_form)
        taskFormAnimation.topMargin = resources.getDimension(R.dimen.activity_vertical_margin)
        btn_toggle_task.setOnClickListener {
            taskFormAnimation.toggle();
        }
        val perf = ApiKeyService.createSharedPreferences(applicationContext)
        val client = ApiClientBuilder.createApi(ApiKeyService.getApiKey(perf).token)
        inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager;
        api = TodoApiBuilder.buildTaskApi(client)

        btn_register.setOnClickListener {
            view ->
            inputMethodManager.hideSoftInputFromWindow(layout_register_form.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
            api.new(NewTaskQuery(edit_task.text.toString()))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(object : Subscriber<NewTaskResponse>() {
                        override fun onCompleted() {
                            edit_task.setText("")
                            Snackbar.make(view, R.string.complete_register_task, Snackbar.LENGTH_LONG).setAction("Action", null).show()
                        }

                        override fun onNext(response: NewTaskResponse) {
                            Log.d("api", "response:${response.toString()}");
                        }

                        override fun onError(e: Throwable?) {

                            Log.d("api", "ng:" + e?.message);
                        }
                    }) ;
        }
        when (intent?.extras?.getString("user", "") ?: "") {
            "new" -> Snackbar.make(layout_register_form, R.string.welcome, Snackbar.LENGTH_LONG).setAction("Action", null).show()
            "" -> Snackbar.make(layout_register_form, R.string.hello, Snackbar.LENGTH_LONG).setAction("Action", null).show()
        }
    }

    override fun onStart() {
        super.onStart()
        api.list()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Subscriber<List<Task>>() {
                    override fun onCompleted() {
                    }

                    override fun onNext(response: List<Task>) {
                        response.forEach {
                            Log.d("api", "response:${it.toString()}");
                        }
                    }

                    override fun onError(e: Throwable?) {

                        Log.d("api", "ng:" + e?.message);
                    }
                }) ;
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        taskFormAnimation.init();
    }

}
