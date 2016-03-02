package com.kamedon.todo.service

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.kamedon.todo.entity.ApiKey

/**
 * Created by kamedon on 2/28/16.
 */

object ApiKeyService {
    private val key_api_token: String = "key_api_token"
    fun createSharedPreferences(context: Context) = context.getSharedPreferences("todo_app", Context.MODE_PRIVATE)

    fun update(editor: SharedPreferences.Editor, apiKey: ApiKey) {
        editor.putString(key_api_token, Gson().toJson(apiKey))
        editor.apply();
    }

    fun hasApiKey(sharedPreferences: SharedPreferences): Boolean = !sharedPreferences.getString(key_api_token, "").equals("")
    fun getApiKey(sharedPreferences: SharedPreferences): ApiKey = Gson().fromJson(sharedPreferences.getString(key_api_token, "").toString(), ApiKey::class.java)
    fun deleteApiKey(editor: SharedPreferences.Editor) = editor.remove(key_api_token).apply()
}
