package com.kamedon.todo.service

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.kamedon.todo.entity.ApiKey
import com.kamedon.todo.entity.User
import com.kamedon.todo.entity.api.NewUserResponse

/**
 * Created by h_kamei on 2016/03/02.
 */
object UserService {
    private val key_name: String = "key_name"
    private val key_mail: String = "key_mail"
    private val key_id: String = "key_id"

    fun updateApiKey(editor: SharedPreferences.Editor, response: User) {
        editor.putInt(key_id, response.id);
        editor.putString(key_name, response.username);
        editor.putString(key_mail, response.email);
        editor.apply();
    }

    fun getUser(sharedPreferences: SharedPreferences): User {
        return User(sharedPreferences.getInt(key_id, -1), sharedPreferences.getString(key_name, ""), sharedPreferences.getString(key_mail, ""))
    }
}
