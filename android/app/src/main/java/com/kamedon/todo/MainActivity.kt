package com.kamedon.todo

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.kamedon.todo.entity.api.NewUserQuery
import com.kamedon.todo.entity.api.NewUserResponse
import com.kamedon.todo.builder.ApiClientBuilder
import com.kamedon.todo.service.ApiKeyService
import com.kamedon.todo.builder.TodoApiBuilder
import kotlinx.android.synthetic.main.activity_main.*
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

/**
 * Created by kamedon on 2/29/16.
 */
class MainActivity : AppCompatActivity() {

    lateinit var perf: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        perf = ApiKeyService.createSharedPreferences(applicationContext);
        if (ApiKeyService.hasApiKey(perf)) {
            startActivity(Intent(applicationContext, TaskActivity::class.java));
            finish();
            return
        }
        setContentView(R.layout.activity_main);
        val client = ApiClientBuilder.createApi(null);
        val api = TodoApiBuilder.buildUserApi(client);
        btn_signIn.setOnClickListener {
            api.new(NewUserQuery(edit_username.text.toString(), edit_email.text.toString(), edit_password.text.toString()))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(object : Subscriber<NewUserResponse>() {
                        override fun onCompleted() {
                            Log.d("api", "onCompleted");
                            val intent = Intent(applicationContext, TaskActivity::class.java)
                            intent.putExtra("user", "new");
                            startActivity(intent);
                            finish();
                        }

                        override fun onNext(response: NewUserResponse) {
                            Log.d("api", "response:${response.toString()}");
                            ApiKeyService.updateApiKey(perf.edit(), response.api_key)
                        }

                        override fun onError(e: Throwable?) {

                            Log.d("api", "ng:" + e?.message);
                        }
                    }) ;

        }
    }
}
