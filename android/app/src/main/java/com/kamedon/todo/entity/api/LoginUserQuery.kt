package com.kamedon.todo.entity.api

import android.content.res.Resources
import com.kamedon.todo.R
import com.kamedon.todo.extension.between
import java.io.Serializable

/**
 *
 * ユーザ登録データ
 */
data class LoginUserQuery(val user: String, val password: String) : Serializable {
    fun valid(resources: Resources): Map<String, String> {
        val map = mutableMapOf<String, String>()

        if (password.isBlank()) {
            map.put("password", resources.getString(R.string.error_blank))
        }
        if (user.isBlank()) {
            map.put("user", resources.getString(R.string.error_blank))
        }
        return map
    }
}
