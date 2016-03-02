package com.kamedon.todo.dialog

import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.support.v7.app.AlertDialog
import android.util.Log
import android.widget.EditText
import com.kamedon.todo.R
import com.kamedon.todo.TaskActivity
import com.kamedon.todo.api.TodoApi
import com.kamedon.todo.entity.api.NewUserQuery
import com.kamedon.todo.entity.api.NewUserResponse
import com.kamedon.todo.extension.execute
import com.kamedon.todo.service.UserService
import com.trello.rxlifecycle.components.support.RxAppCompatActivity
import rx.Subscriber

/**
 * Created by h_kamei on 2016/03/02.
 */
class SignUpDialog(val api: TodoApi.UserApi) {
    fun show(activity: RxAppCompatActivity, onSignUpListener: OnSignUpListener?) {
        var view = activity.layoutInflater.inflate(R.layout.dialog_sign_up, null)
        val edit_username = view.findViewById(R.id.edit_username) as EditText
        val edit_email = view.findViewById(R.id.edit_email) as EditText
        val edit_password = view.findViewById(R.id.edit_password) as EditText

        AlertDialog.Builder(activity).setTitle(R.string.title_sign_up).setView(view).setPositiveButton(R.string.action_sign_up, { dialogInterface: DialogInterface, i: Int ->
            api.new(NewUserQuery(edit_username.text.toString(), edit_email.text.toString(), edit_password.text.toString()))
                    .execute(activity, object : Subscriber<NewUserResponse>() {
                        override fun onCompleted() {
                            onSignUpListener?.onComplete()
                        }

                        override fun onNext(response: NewUserResponse) {
                            UserService.update(UserService.createSharedPreferences(activity.applicationContext).edit(), response)
                        }

                        override fun onError(e: Throwable?) {
                            onSignUpListener?.onError(e)
                        }
                    }) ;


        }).show();

    }

    interface OnSignUpListener {
        fun onError(e: Throwable?)
        fun onComplete()
    }

}