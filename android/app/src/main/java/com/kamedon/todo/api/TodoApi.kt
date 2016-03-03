package com.kamedon.todo.api

import com.kamedon.todo.entity.Task
import com.kamedon.todo.entity.api.*
import retrofit2.http.*
import rx.Observable

/**
 * Created by kamedon on 2/29/16.
 */
object TodoApi {

    val BASE_URL = "http://kamedon39.xyz";

    interface TaskApi {
        @GET("/api/tasks.json")
        fun list(@Query ("page") page: Int = 1): Observable<List<Task>>

        @GET("/api/tasks/{state}.json")
        fun list(@Path("state") state: String, @Query ("page") page: Int = 1): Observable<List<Task>>

        @POST("/api/tasks.json")
        fun new(@Body user: NewTaskQuery): Observable<NewTaskResponse>

        @FormUrlEncoded
        @PUT("/api/tasks/{id}.json")
        fun edit(@Path("id") id: Int, @Field("body") body: String, @Field("state") state: String): Observable<NewTaskResponse>

        @DELETE("/api/tasks/{id}.json")
        fun delete(@Path("id") id: Int): Observable<DeleteTaskResponse>
    }

    interface UserApi {
        @POST("/api/users.json")
        fun new(@Body user: NewUserQuery): Observable<NewUserResponse>

        @POST("/api/users/logins.json")
        fun login(@Body user: LoginUserQuery): Observable<NewUserResponse>
    }


}
