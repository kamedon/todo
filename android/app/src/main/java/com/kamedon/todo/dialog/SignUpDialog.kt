package com.kamedon.todo.dialog

import android.content.DialogInterface
import android.support.v7.app.AlertDialog
import android.util.Log
import android.widget.EditText
import com.kamedon.todo.R
import com.kamedon.todo.api.TodoApi
import com.kamedon.todo.entity.api.Errors
import com.kamedon.todo.entity.api.NewUserQuery
import com.kamedon.todo.entity.api.NewUserResponse
import com.kamedon.todo.extension.observable
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
        val edit_password_confirm = view.findViewById(R.id.edit_password_confirm) as EditText

        val dialog = AlertDialog.Builder(activity)
                .setTitle(R.string.title_sign_up)
                .setView(view)
                .setPositiveButton(R.string.action_sign_up, null)
                .setNegativeButton(android.R.string.cancel, null)
                .show();
        dialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener {
            activity.observable(api.new(NewUserQuery(edit_username.text.toString(), edit_email.text.toString(), edit_password.text.toString()))
                    , object : Subscriber<NewUserResponse>() {
                override fun onCompleted() {
                    Log.d("api", "onCompleted");
                    if (UserService.isLogin(UserService.createSharedPreferences(activity.applicationContext))) {
                        onSignUpListener?.onComplete()
                    }
                }

                override fun onNext(response: NewUserResponse) {
                    Log.d("api", "response:${response.toString()}");
                    when (response.code) {
                        400 -> {
                            val errors = response.errors;
                            edit_email.error = errors.email?.let { it.errors[0].toString() } ?: ""
                            edit_username.error = errors.username?.let { it.errors[0].toString() } ?: ""
                            edit_password.error = errors.plainPassword?.let { it.errors[0].toString() } ?: ""
                        }
                        201 -> UserService.update(UserService.createSharedPreferences(activity.applicationContext).edit(), response)
                    }
                }


                override fun onError(e: Throwable?) {
                    Log.d("api", "${e?.message}");

                    onSignUpListener?.onError(e)
                }
            }) ;

        }
    }

    interface OnSignUpListener {
        fun onError(e: Throwable?)
        fun onComplete()
        fun onInvalidQuery(errors: Errors)
    }

}