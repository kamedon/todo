package com.kamedon.todo.service

import com.kamedon.todo.api.TodoApi
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Created by kamedon on 2/28/16.
 */
object TodoApiService {
    private fun createRetrofit(client: OkHttpClient) = Retrofit.Builder()
            .baseUrl(TodoApi.BASE_URL)
            .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()

    fun createTaskApi(client: OkHttpClient): TodoApi.UserApi {
        val retrofit = createRetrofit(client)
        return  retrofit.create(TodoApi.UserApi::class.java);
    }

    fun createUserApi(client: OkHttpClient): TodoApi.UserApi {
        val retrofit = createRetrofit(client)
        return retrofit.create(TodoApi.UserApi::class.java);
    }

}
