package com.kamedon.todo.builder

import android.util.Log
import com.kamedon.todo.util.XUserAgentAuthorizationUtil
import okhttp3.Interceptor
import okhttp3.JavaNetCookieJar
import okhttp3.OkHttpClient
import okhttp3.Response

/**
 * Created by kamedon on 2/29/16.
 */
object ApiClientBuilder {
    fun createApi(api_token: String?, listener: OnRequestListener?): OkHttpClient {
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

                    Log.d("okhttp", XUserAgentAuthorizationUtil.token());

                    api_token?.let {
                        builder.header("Authorization", it)
                    }
                    var response = chain.proceed(builder.build())
                    Log.d("okhttp", response?.toString());
                    when (response?.code()) {
                        403 -> listener?.onInvalidApiKeyOrNotFoundUser(response)
                    }
                    response
                })
                .build()

    }

    interface OnRequestListener {
        fun onInvalidApiKeyOrNotFoundUser(response: Response);
    }
}