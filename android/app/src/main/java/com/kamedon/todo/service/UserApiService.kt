package com.kamedon.todo.service

import com.kamedon.todo.api.UserApi
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Created by kamedon on 2/28/16.
 */
object UserApiService {

    fun createApi(client: OkHttpClient): UserApi.Api {
        val retrofit = Retrofit.Builder()
                .baseUrl(UserApi.BASE_URL)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();


        val api = retrofit.create(UserApi.Api::class.java);
        return api;
    }

}
