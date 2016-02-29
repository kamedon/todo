package com.kamedon.todo.api

import com.kamedon.todo.entity.Task
import com.kamedon.todo.entity.api.NewTaskQuery
import com.kamedon.todo.entity.api.NewTaskResponse
import com.kamedon.todo.entity.api.NewUserQuery
import com.kamedon.todo.entity.api.NewUserResponse
import retrofit2.http.*
import rx.Observable

/**
 * Created by kamedon on 2/29/16.
 */
object TodoApi {

    val BASE_URL = "http://kamedon39.xyz";

    interface TaskApi {
        @POST("/api/tasks.json")
        fun new(@Body user: NewTaskQuery): Observable<NewTaskResponse>

        @GET("/api/tasks/{page}.json")
        fun list(@Path("page") page: Int = 1): Observable<List<Task>>

        @DELETE("/api/tasks/{id}.json")
        fun delete(@Path("id") id: Int): Observable<List<Task>>
    }

    interface UserApi {
        @POST("/api/users.json")
        fun new(@Body user: NewUserQuery): Observable<NewUserResponse>
    }


}
