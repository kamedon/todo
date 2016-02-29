package com.kamedon.todo.api

import com.kamedon.todo.entity.*
import com.kamedon.todo.entity.api.NewUserQuery
import com.kamedon.todo.entity.api.NewUserResponse
import retrofit2.http.Body
import retrofit2.http.POST
import rx.Observable

/**
 * Created by kamedon on 2/28/16.
 */
object UserApi {

    val BASE_URL = "http://kamedon39.xyz";

    interface Api {
        @POST("/api/users.json")
        fun new(@Body user: NewUserQuery): Observable<NewUserResponse>
    }

}