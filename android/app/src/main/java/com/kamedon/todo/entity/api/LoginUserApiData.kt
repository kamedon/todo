package com.kamedon.todo.entity.api

import java.io.Serializable

/**
 *
 * ユーザ登録データ
 */
data class LoginUserApiData(val user: String, val password: String) : Serializable
