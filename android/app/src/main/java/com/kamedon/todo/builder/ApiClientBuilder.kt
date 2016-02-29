package com.kamedon.todo.builder

import com.kamedon.todo.util.XUserAgentAuthorizationUtil
import okhttp3.JavaNetCookieJar
import okhttp3.OkHttpClient

/**
 * Created by kamedon on 2/29/16.
 */
object ApiClientBuilder {
    fun createApi(api_token: String?): OkHttpClient {
        return OkHttpClient.Builder()
                .addInterceptor({
                    chain ->
                    val original = chain.request()
                    val builder = original.newBuilder()
                            .header("X-User-Agent-Authorization", XUserAgentAuthorizationUtil.token())
                            .header("Accept", "application/json")
                            //.header("Content-Type", "application/x-www-form-urlencoded")
                            .header("Content-Type", "application/json")
                            .method(original.method(), original.body());


                    api_token?.let {
                        builder.header("Authorization", it)
                    }
                    chain.proceed(builder.build())
                })
                .build()

    }

}