package com.kamedon.todo.service

import android.content.SharedPreferences
import com.google.gson.Gson
import com.kamedon.todo.entity.ApiKey

/**
 * Created by kamedon on 2/28/16.
 */

object ApiKeyService {
    val key_api_token: String = "key_api_token"

    fun updateApiKey(editor: SharedPreferences.Editor, apiKey: ApiKey) {
        editor.putString(key_api_token, Gson().toJson(apiKey))
        editor.apply();
    }

    fun getApiKey(sharedPreferences: SharedPreferences): ApiKey = Gson().fromJson(sharedPreferences.getString(key_api_token, "").toString(), ApiKey::class.java)
}
