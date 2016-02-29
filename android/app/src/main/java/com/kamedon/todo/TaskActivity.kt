package com.kamedon.todo

import android.content.Context
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import com.kamedon.todo.adapter.TaskListAdapter
import com.kamedon.todo.anim.TaskFormAnimation
import com.kamedon.todo.api.TodoApi
import com.kamedon.todo.builder.ApiClientBuilder
import com.kamedon.todo.builder.TodoApiBuilder
import com.kamedon.todo.entity.Task
import com.kamedon.todo.entity.api.NewTaskQuery
import com.kamedon.todo.entity.api.NewTaskResponse
import com.kamedon.todo.service.ApiKeyService
import com.trello.rxlifecycle.components.support.RxAppCompatActivity
import kotlinx.android.synthetic.main.activity_task.*
import kotlinx.android.synthetic.main.content_task.*
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import uk.co.senab.actionbarpulltorefresh.library.ActionBarPullToRefresh
import java.util.concurrent.atomic.AtomicInteger

/**
 * Created by kamedon on 2/29/16.
 */
class TaskActivity : RxAppCompatActivity() {
    lateinit var taskFormAnimation: TaskFormAnimation

    lateinit var api: TodoApi.TaskApi
    lateinit var inputMethodManager: InputMethodManager
    lateinit var taskListAdapter: TaskListAdapter
    var page: AtomicInteger = AtomicInteger(1);

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
        taskListAdapter = TaskListAdapter(layoutInflater, mutableListOf());
        list.adapter = taskListAdapter

        btn_register.setOnClickListener {
            view ->
            inputMethodManager.hideSoftInputFromWindow(layout_register_form.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
            api.new(NewTaskQuery(edit_task.text.toString()))
                    .compose (bindToLifecycle<NewTaskResponse>())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(object : Subscriber<NewTaskResponse>() {
                        override fun onCompleted() {
                            edit_task.setText("")
                            Snackbar.make(view, R.string.complete_register_task, Snackbar.LENGTH_LONG).setAction("Action", null).show()
                        }

                        override fun onNext(response: NewTaskResponse) {
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

        ActionBarPullToRefresh.from(this)
                .theseChildrenArePullable(R.id.list)
                .listener { view ->
                    page.set(1)
                    updateList(1, true);
                }
                // Finally commit the setup to our PullToRefreshLayout
                .setup(ptr_layout);
    }


    override fun onResume() {
        super.onResume()
        updateList(page.get(), true);
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        taskFormAnimation.init();
    }

    private fun updateList(page: Int, clean: Boolean) {
        api.list(page)
                .compose(bindToLifecycle<List<Task>>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Subscriber<List<Task>>() {
                    override fun onCompleted() {
                        ptr_layout.setRefreshComplete()
                    }

                    override fun onNext(response: List<Task>) {
                        if (clean) {
                            taskListAdapter.list = response.toMutableList()
                        } else {
                            taskListAdapter.list.addAll(response)
                        }
                        taskListAdapter.notifyDataSetChanged()
                        empty.visibility = if (taskListAdapter.isEmpty) {
                            View.VISIBLE
                        } else {
                            View.GONE
                        }
                    }

                    override fun onError(e: Throwable?) {
                        Log.d("api", "ng:" + e?.message);
                        ptr_layout.setRefreshComplete()
                    }
                }) ;
    }
}
