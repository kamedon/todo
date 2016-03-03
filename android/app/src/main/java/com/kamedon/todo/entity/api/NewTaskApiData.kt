package com.kamedon.todo.entity.api

/**
 * ユーザ登録データ
 */
import android.content.res.Resources
import com.kamedon.todo.R
import com.kamedon.todo.entity.ApiKey
import com.kamedon.todo.entity.Task
import com.kamedon.todo.entity.User
import java.io.Serializable

/**
 * @param body String
 * @param state {"untreated","process", "complete"}
 */
data class NewTaskQuery(val body: String, val state: String = "untreated") : Serializable {
    fun valid(resources: Resources): Map<String, String> {
        val map = mutableMapOf<String, String>()
        if (body.isBlank()) {
            map["task"] = resources.getString(R.string.error_blank)
        }
        return map
    }
}

data class NewTaskResponse(val code: Int, val task: Task, val message: String, val errors: Errors) : Serializable
