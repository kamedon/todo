package com.kamedon.todo

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import com.kamedon.todo.builder.ApiClientBuilder
import com.kamedon.todo.builder.TodoApiBuilder
import com.kamedon.todo.dialog.SignUpDialog
import com.kamedon.todo.entity.api.LoginUserApiData
import com.kamedon.todo.entity.api.NewUserResponse
import com.kamedon.todo.extension.buildIntent
import com.kamedon.todo.extension.observable
import com.kamedon.todo.service.UserService
import com.trello.rxlifecycle.components.support.RxAppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import rx.Subscriber

/**
 * Created by kamedon on 2/29/16.
 */
class MainActivity : RxAppCompatActivity() {

    lateinit var perf: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        perf = UserService.createSharedPreferences(applicationContext);
        if (UserService.hasApiKey(perf)) {
            startActivity(Intent(applicationContext, TaskActivity::class.java));
            finish();
            return
        }
        setContentView(R.layout.activity_main);
        val client = ApiClientBuilder.createApi();
        val api = TodoApiBuilder.buildUserApi(client);
        btn_login.setOnClickListener {
            observable(api.login(LoginUserApiData(edit_username.text.toString(), edit_password.text.toString())), object : Subscriber<NewUserResponse>() {
                override fun onCompleted() {
                    val intent = buildIntent(TaskActivity::class.java)
                    intent.putExtra("user", "login");
                    startActivity(intent);
                    finish();
                }

                override fun onNext(response: NewUserResponse) {
                    Log.d("api", "response:${response.toString()}");
                    UserService.update(perf.edit(), response)
                }

                override fun onError(e: Throwable?) {
                    Log.d("api", "ng:" + e?.message);
                }
            })
        }

        btn_signIn.setOnClickListener {
            SignUpDialog(api).show(this@MainActivity, object : SignUpDialog.OnSignUpListener {
                override fun onComplete() {
                    Log.d("api", "onCompleted");
                    val intent = buildIntent(TaskActivity::class.java)
                    intent.putExtra("user", "new");
                    startActivity(intent);
                    finish();
                }

                override fun onError(e: Throwable?) {
                }

            })
        }
    }
}
