package com.kamedon.todo

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.design.widget.Snackbar
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.AbsListView
import android.widget.TextView
import com.kamedon.todo.adapter.TaskListAdapter
import com.kamedon.todo.anim.TaskFormAnimation
import com.kamedon.todo.api.TodoApi
import com.kamedon.todo.builder.ApiClientBuilder
import com.kamedon.todo.builder.TodoApiBuilder
import com.kamedon.todo.dialog.EditTaskDialog
import com.kamedon.todo.entity.Task
import com.kamedon.todo.entity.User
import com.kamedon.todo.entity.api.NewTaskQuery
import com.kamedon.todo.entity.api.NewTaskResponse
import com.kamedon.todo.extension.observable
import com.kamedon.todo.service.UserService
import com.kamedon.todo.util.Debug
import com.trello.rxlifecycle.components.support.RxAppCompatActivity
import kotlinx.android.synthetic.main.activity_task.*
import kotlinx.android.synthetic.main.content_task.*
import okhttp3.Response
import rx.Subscriber
import rx.Subscription
import uk.co.senab.actionbarpulltorefresh.library.ActionBarPullToRefresh
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.atomic.AtomicBoolean
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
    lateinit var perf: SharedPreferences

    var subscription: Subscription? = null
    private var next: AtomicBoolean = AtomicBoolean(true)

    private var page: AtomicInteger = AtomicInteger(1);
    private var state: String = Task.state_untreated;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task)
        perf = UserService.createSharedPreferences(applicationContext)
        user = UserService.getUser(perf);

        initToolBar();
        initNavigation();
        initApi();
        taskFormAnimation = TaskFormAnimation(layout_register_form)
        taskFormAnimation.topMargin = resources.getDimension(R.dimen.activity_vertical_margin)
        btn_toggle_task.setOnClickListener {
            taskFormAnimation.toggle();
        }
        inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager;

        /*
         * Task一覧の初期化
         */
        taskListAdapter = TaskListAdapter(layoutInflater, CopyOnWriteArrayList());
        taskListAdapter.onComplete = { view, task, complete ->
            observable(api.edit(task.id, task.body, task.state), object : Subscriber<NewTaskResponse>() {
                override fun onNext(response: NewTaskResponse) {
                    Log.d("response", response.toString());
                    if (!state.equals(response.task.state) && !state.equals(Task.state_all) ) {
                        taskListAdapter.list.remove(task)
                    }
                    Snackbar.make(layout_register_form, response.task.state(resources), Snackbar.LENGTH_LONG).show();
                }

                override fun onCompleted() {
                    taskListAdapter.notifyDataSetChanged()
                }

                override fun onError(e: Throwable?) {
                    Log.d("response", e.toString());
                }
            })
        }
        taskListAdapter.onItemLongClickListener = { position, task ->
            EditTaskDialog(api)
                    .setInputMethodManager(inputMethodManager)
                    .setOnDeleteListener(object : EditTaskDialog.OnDeleteListener {
                        override fun onDelete(task: Task) {
                            taskListAdapter.list.remove(task)
                        }

                        override fun onError(e: Throwable?) {
                        }

                        override fun onComplete() {
                            updateEmptyView();
                            taskListAdapter.notifyDataSetChanged()
                            Snackbar.make(layout_register_form, R.string.complete_delete_task, Snackbar.LENGTH_LONG).setAction("Action", null).show()
                        }

                    }).setOnEditListener(object : EditTaskDialog.OnEditListener {
                override fun onEdit(task: Task) {
                }

                override fun onError(e: Throwable?) {
                }

                override fun onComplete() {
                    val view = list.getChildAt(position);
                    taskListAdapter.getView(position, view, list);
                    //taskListAdapter.notifyDataSetChanged()
                    //list.invalidateViews();
                    Snackbar.make(layout_register_form, R.string.complete_edit_task, Snackbar.LENGTH_LONG).setAction("Action", null).show()
                }
            }).show(this@TaskActivity, task)
        }
        list.adapter = taskListAdapter

        btn_register.setOnClickListener {
            view ->
            inputMethodManager.hideSoftInputFromWindow(layout_register_form.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
            val query = NewTaskQuery(edit_task.text.toString())
            val errors = query.valid(resources)
            if (errors.isEmpty()) {
                observable(api.new(query), object : Subscriber<NewTaskResponse>() {
                    override fun onCompleted() {
                        edit_task.setText("")
                        taskListAdapter.notifyDataSetChanged()
                        updateEmptyView();
                        Snackbar.make(view, R.string.complete_register_task, Snackbar.LENGTH_LONG).setAction("Action", null).show()
                    }

                    override fun onNext(response: NewTaskResponse) {
                        taskListAdapter.list.add(0, response.task)
                    }

                    override fun onError(e: Throwable?) {

                        Log.d("api", "ng:" + e?.message);
                    }
                }) ;
            } else {
                edit_task.error = errors["task"]
            }
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
                    updateList(state, 1, true);
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
                if (isLastItemVisible && process && next.get()) {
                    updateList(state, page.incrementAndGet(), false)
                }
            }
        })
    }

    private fun initApi() {
        val client = ApiClientBuilder.createApi(UserService.getApiKey(perf).token, object : ApiClientBuilder.OnRequestListener {
            override fun onInvalidApiKeyOrNotFoundUser(response: Response) {
                UserService.deleteApiKey(perf.edit());
                val intent = Intent(applicationContext, MainActivity::class.java)
                startActivity(intent);
                finish();
            }
        })
        api = TodoApiBuilder.buildTaskApi(client)
    }

    private fun initNavigation() {
        val navigationView = findViewById(R.id.nav_view) as NavigationView;
        val header = navigationView.getHeaderView(0);
        val textName = header.findViewById(R.id.text_name) as TextView;
        textName.text = user.username
        val textEmail = header.findViewById(R.id.text_email) as TextView;
        textEmail.text = user.email

        navigationView.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.nav_logout -> {
                    UserService.deleteApiKey(perf.edit())
                    startActivity(Intent(applicationContext, MainActivity::class.java))
                    finish()
                }
                R.id.nav_all -> update(Task.state_all, R.string.title_task_all)
                R.id.nav_untreated -> update(Task.state_untreated, R.string.title_task_untreated)
                R.id.nav_complete -> update(Task.state_complete, R.string.title_task_complete)
            }
            false
        }

    }


    private fun initToolBar() {
        val toolbar = findViewById(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)
        val drawer = findViewById(R.id.drawer_layout) as DrawerLayout;

        toolbar.setNavigationIcon(R.drawable.ic_menu_white_24dp)

        toolbar.setNavigationOnClickListener {
            drawer.openDrawer(GravityCompat.START);
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_task, menu);
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        Debug.d("menu", "select: ${item.itemId}")
        when (item.itemId) {
            R.id.nav_all -> update(Task.state_all, R.string.title_task_all)
            R.id.nav_untreated -> update(Task.state_untreated, R.string.title_task_untreated)
            R.id.nav_complete -> update(Task.state_complete, R.string.title_task_complete)
        }
        return true;
    }


    override fun onResume() {
        super.onResume()
        ptr_layout.isRefreshing = true;
        page.set(1);
        updateList(state, page.get(), true);
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        taskFormAnimation.init();
    }


    private fun update(state: String, stringId: Int) {
        next.set(true)
        updateHeader(state, stringId)
        updateList(state, 1, true)
    }

    private fun updateHeader(state: String, stringId: Int) {
        this.state = state;
        toolbar.title = getString(stringId)
        drawer_layout.closeDrawers()
    }

    private fun updateList(state: String, page: Int, clean: Boolean) {
        subscription = observable(api.list(state, page), object : Subscriber<List<Task>>() {
            override fun onCompleted() {
                taskListAdapter.notifyDataSetChanged()
                ptr_layout.setRefreshComplete()
                updateEmptyView();
                if (clean) {
                    updateForm()
                }
            }


            override fun onNext(response: List<Task>) {
                if (clean) {
                    taskListAdapter.list = CopyOnWriteArrayList(response)
                } else {
                    taskListAdapter.list.addAll(taskListAdapter.list.lastIndex, response)
                }
                taskListAdapter.notifyDataSetChanged()
                next.set(response.size >= BuildConfig.PAGE_LIMIT);
            }

            override fun onError(e: Throwable?) {
                ptr_layout.setRefreshComplete()
            }
        }) ;
    }

    private fun updateForm() {
        if (taskListAdapter.isEmpty) {
            taskFormAnimation.show()
        } else {
            taskFormAnimation.hide()
        }

    }

    private fun updateEmptyView() {
        if (taskListAdapter.isEmpty) {
            empty.visibility = View.VISIBLE
            taskFormAnimation.show()
        } else {
            empty.visibility = View.GONE
            layout_register_form.visibility = View.GONE
        }
    }

}

