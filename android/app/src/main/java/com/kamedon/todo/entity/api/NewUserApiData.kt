package com.kamedon.todo.entity.api

import android.content.res.Resources
import android.util.Log
import com.kamedon.todo.R
import com.kamedon.todo.entity.ApiKey
import com.kamedon.todo.entity.User
import com.kamedon.todo.extension.between
import com.kamedon.todo.extension.isMail
import java.io.Serializable


/**
 *
 * ユーザ登録データ
 */
data class NewUserQuery(val username: String, val email: String, val plainPassword: String) : Serializable {
    fun valid(resources: Resources ): Map<String, String> {
        val map = mutableMapOf<String, String>()
        if (!username.length.between(3, 16)) {
            map.put("username", resources.getString(R.string.error_length_between, 3, 16))
        }
        if (!plainPassword.length.between(6, 16)) {
            map.put("plainPassword", resources.getString(R.string.error_length_between, 6, 16))
        }
        if (email.isEmpty()) {
            map.put("email", resources.getString(R.string.error_blank))
        }
        if (!email.isMail()) {
            map.put("email", resources.getString(R.string.error_invalid_email))
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
 *     "email": "hogehoge@gmail.com"
 *   },
 *   "api_key": {
 *     "token":  "88848ef4-dea9-11e5-9260-3c970e85851b"
 *   },
 *   "message": "created new user"
 * }
 */
data class NewUserResponse(val code: Int, val user: User, val api_key: ApiKey, val message: String, val errors: Errors) : Serializable

data class Errors(val username: Error?, val email: Error?, val plainPassword: Error?, val other: Error?) : Serializable
data class Error(val errors: List<String>) : Serializable

