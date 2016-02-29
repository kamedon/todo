package com.kamedon.todo.entity.api

import com.kamedon.todo.entity.ApiKey
import com.kamedon.todo.entity.User
import java.io.Serializable


/**
 * ユーザ登録データ
 */
data class NewUserQuery(val username: String, val email: String, val plainPassword: String) : Serializable

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
data class NewUserResponse(val user: User, val api_key: ApiKey, val message: String) : Serializable
