package com.kamedon.todo.entity.api

import android.content.res.Resources
import com.kamedon.todo.R
import com.kamedon.todo.entity.ApiKey
import com.kamedon.todo.entity.Task
import com.kamedon.todo.entity.User
import java.io.Serializable

/**
 * ユーザ登録データ
 */
data class NewTaskQuery(val body: String) : Serializable {
    fun valid(resources: Resources): Map<String, String> {
        val map = mutableMapOf<String, String>()
        if (body.isBlank()) {
            map["task"] = resources.getString(R.string.error_blank)
        }
        return map
    }
}

/**
 * ユーザ登録のレスとポンス
 *
 * {
 *   "user": {
 *     "id": 2
 *     "username": "kamedon"
 *   },
 *   "api_key": {
 *     "token":  "88848ef4-dea9-11e5-9260-3c970e85851b"
 *   },
 *   "message": "created new user"
 * }
 */
data class NewTaskResponse(val task: Task, val message: String) : Serializable
