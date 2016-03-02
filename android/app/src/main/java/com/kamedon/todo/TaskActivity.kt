package com.kamedon.todo

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.design.widget.Snackbar
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.AbsListView
import android.widget.TextView
import com.kamedon.todo.adapter.TaskListAdapter
import com.kamedon.todo.anim.TaskFormAnimation
import com.kamedon.todo.api.TodoApi
import com.kamedon.todo.builder.ApiClientBuilder
import com.kamedon.todo.builder.TodoApiBuilder
import com.kamedon.todo.entity.Task
import com.kamedon.todo.entity.User
import com.kamedon.todo.entity.api.DeleteTaskResponse
import com.kamedon.todo.entity.api.NewTaskQuery
import com.kamedon.todo.entity.api.NewTaskResponse
import com.kamedon.todo.service.ApiKeyService
import com.kamedon.todo.service.UserService
import com.trello.rxlifecycle.components.support.RxAppCompatActivity
import kotlinx.android.synthetic.main.activity_task.*
import kotlinx.android.synthetic.main.content_task.*
import okhttp3.Response
import rx.Observable
import rx.Subscriber
import rx.Subscription
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import uk.co.senab.actionbarpulltorefresh.library.ActionBarPullToRefresh
import java.util.*
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.atomic.AtomicInteger

/**
 * Created by kamedon on 2/29/16.
 */
class TaskActivity : RxAppCompatActivity() {
    lateinit var taskFormAnimation: TaskFormAnimation

    lateinit var user: User
    lateinit var api: TodoApi.TaskApi
    lateinit var inputMethodManager: InputMethodManager
    lateinit var taskListAdapter: TaskListAdapter
    var page: AtomicInteger = AtomicInteger(1);
    private var next: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val perf = ApiKeyService.createSharedPreferences(applicationContext)
        setContentView(R.layout.activity_task)
        val toolbar = findViewById(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)
        val drawer = findViewById(R.id.drawer_layout) as DrawerLayout;

        toolbar.setNavigationIcon(R.drawable.abc_btn_switch_to_on_mtrl_00001)

        toolbar.setNavigationOnClickListener {
            drawer.openDrawer(GravityCompat.START);
        }

        user = UserService.getUser(perf);
        val navigationView = findViewById(R.id.nav_view) as NavigationView;
        val header = navigationView.getHeaderView(0);
        val textName = header.findViewById(R.id.text_name) as TextView;
        textName.text = user.username
        val textEmail = header.findViewById(R.id.text_email) as TextView;
        textEmail.text = user.email

        Log.d("user", user.toString());

        navigationView.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.nav_logout -> {
                    ApiKeyService.deleteApiKey(perf.edit())
                    startActivity(Intent(applicationContext, MainActivity::class.java))
                    finish()
                }

            }
            false
        }

        taskFormAnimation = TaskFormAnimation(layout_register_form)
        taskFormAnimation.topMargin = resources.getDimension(R.dimen.activity_vertical_margin)
        btn_toggle_task.setOnClickListener {
            taskFormAnimation.toggle();
        }
        val client = ApiClientBuilder.createApi(ApiKeyService.getApiKey(perf).token, object : ApiClientBuilder.OnRequestListener {
            override fun onInvalidApiKeyOrNotFoundUser(response: Response) {
                ApiKeyService.deleteApiKey(perf.edit());
                startActivity(Intent(applicationContext, MainActivity::class.java));
                finish();
            }
        })
        inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager;
        api = TodoApiBuilder.buildTaskApi(client)
        taskListAdapter = TaskListAdapter(layoutInflater, CopyOnWriteArrayList());
        taskListAdapter.onComplete = { view, task, complete ->
            api.delete(task.id)
                    .compose(bindToLifecycle<DeleteTaskResponse>())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(object : Subscriber<DeleteTaskResponse>() {
                        override fun onCompleted() {
                            Snackbar.make(view, R.string.complete_delete_task, Snackbar.LENGTH_LONG).setAction("Action", null).show()
                        }

                        override fun onNext(response: DeleteTaskResponse) {
                            taskListAdapter.list.remove(task)
                            taskListAdapter.notifyDataSetChanged()
                        }

                        override fun onError(e: Throwable?) {
                            Log.d("api", "ng:" + e?.message);
                        }
                    }) ;

        }
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
                            taskListAdapter.notifyDataSetChanged()
                        }

                        override fun onNext(response: NewTaskResponse) {
                            taskListAdapter.list.add(0, response.task)
                        }

                        override fun onError(e: Throwable?) {

                            Log.d("api", "ng:" + e?.message);
                        }
                    }) ;
        }
        when (intent?.extras?.getString("user", "") ?: "") {
            "new" -> Snackbar.make(layout_register_form, R.string.welcome, Snackbar.LENGTH_LONG).setAction("Action", null).show()
            "login" -> Snackbar.make(layout_register_form, R.string.complete_login, Snackbar.LENGTH_LONG).setAction("Action", null).show()
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

        list.setOnScrollListener(object : AbsListView.OnScrollListener {
            override fun onScrollStateChanged(p0: AbsListView?, p1: Int) {
            }

            //            AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount
            override fun onScroll(view: AbsListView?, firstVisibleItem: Int, visibleItemCount: Int, totalItemCount: Int) {
                val process = subscription?.isUnsubscribed ?: false
                val isLastItemVisible = totalItemCount == list.firstVisiblePosition + visibleItemCount;
                if (isLastItemVisible && process && next) {
                    updateList(page.incrementAndGet(), false)
                }
            }
        })
    }


    override fun onResume() {
        super.onResume()
        page.set(1);
        updateList(page.get(), true);
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        taskFormAnimation.init();
    }


    var subscription: Subscription? = null

    private fun updateList(page: Int, clean: Boolean) {
        subscription = api.list(page)
                .compose(bindToLifecycle<List<Task>>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Subscriber<List<Task>>() {
                    override fun onCompleted() {
                        ptr_layout.setRefreshComplete()
                    }


                    override fun onNext(response: List<Task>) {
                        if (clean) {
                            taskListAdapter.list = CopyOnWriteArrayList(response)
                        } else {
                            taskListAdapter.list.addAll(taskListAdapter.list.lastIndex, response)
                        }
                        taskListAdapter.notifyDataSetChanged()
                        empty.visibility = if (taskListAdapter.isEmpty) {
                            View.VISIBLE
                        } else {
                            View.GONE
                        }
                        next = response.size >= 10;
                    }

                    override fun onError(e: Throwable?) {
                        ptr_layout.setRefreshComplete()
                    }
                }) ;
    }
}


