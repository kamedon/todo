package com.kamedon.todo.service

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.google.gson.Gson
import com.kamedon.todo.entity.ApiKey
import com.kamedon.todo.entity.User
import com.kamedon.todo.entity.api.NewUserResponse

/**
 * Created by h_kamei on 2016/03/02.
 */
object UserService {
    private val key_api_token: String = "key_api_token"
    private val key_user: String = "key_user"

    fun createSharedPreferences(context: Context) = context.getSharedPreferences("todo_app", Context.MODE_PRIVATE)

    fun update(editor: SharedPreferences.Editor, response: NewUserResponse) {
        editor.putString(key_api_token, Gson().toJson(response.api_key))
        editor.putString(key_user, Gson().toJson(response.user))
        editor.apply()
    }

    fun updateApiKey(editor: SharedPreferences.Editor, apiKey: ApiKey) {
        editor.putString(key_api_token, Gson().toJson(apiKey))
        editor.apply();
    }

    fun updateUser(editor: SharedPreferences.Editor, user: User) {
        Log.d("update", "data" + user.toString());
        editor.putString(key_user, Gson().toJson(user))
        editor.apply();
    }

    fun hasApiKey(sharedPreferences: SharedPreferences): Boolean = !sharedPreferences.getString(key_api_token, "").equals("")
    fun getApiKey(sharedPreferences: SharedPreferences): ApiKey = Gson().fromJson(sharedPreferences.getString(key_api_token, "").toString(), ApiKey::class.java)
    fun deleteApiKey(editor: SharedPreferences.Editor) = editor.remove(key_api_token).apply()


    fun getUser(sharedPreferences: SharedPreferences): User = Gson().fromJson(sharedPreferences.getString(key_user, "").toString(), User::class.java)

}
