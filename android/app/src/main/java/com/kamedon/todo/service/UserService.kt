package com.kamedon.todo.service

import android.content.SharedPreferences
import android.util.Log
import com.google.gson.Gson
import com.kamedon.todo.entity.User

/**
 * Created by h_kamei on 2016/03/02.
 */
object UserService {
    private val key_user: String = "key_user"

    fun update(editor: SharedPreferences.Editor, user: User) {
        Log.d("update","data"+user.toString());
        editor.putString(key_user, Gson().toJson(user))
        editor.apply();
    }

    fun getUser(sharedPreferences: SharedPreferences): User = Gson().fromJson(sharedPreferences.getString(key_user, "").toString(), User::class.java)
}
