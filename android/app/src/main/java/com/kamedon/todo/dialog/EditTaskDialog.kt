package com.kamedon.todo.dialog

import android.app.Activity
import android.content.DialogInterface
import android.support.design.widget.Snackbar
import android.support.v7.app.AlertDialog
import android.util.Log
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import com.kamedon.todo.R
import com.kamedon.todo.api.TodoApi
import com.kamedon.todo.entity.Task
import com.kamedon.todo.entity.api.DeleteTaskResponse
import com.kamedon.todo.entity.api.NewTaskResponse
import com.kamedon.todo.extension.observable
import com.trello.rxlifecycle.components.support.RxAppCompatActivity
import rx.Subscriber

/**
 * Created by kamedon on 3/3/16.
 */
class EditTaskDialog(val api: TodoApi.TaskApi) {
    private var onDeleteListener: EditTaskDialog.OnDeleteListener? = null
    private var onEditListener: EditTaskDialog.OnEditListener? = null
    private var inputMethodManager: InputMethodManager? = null

    fun setOnEditListener(listener: OnEditListener): EditTaskDialog {
        onEditListener = listener
        return this
    }

    fun setOnDeleteListener(listener: OnDeleteListener): EditTaskDialog {
        onDeleteListener = listener
        return this
    }

    fun show(activity: RxAppCompatActivity, task: Task) {
        var view = activity.layoutInflater.inflate(R.layout.dialog_edit_task, null)
        val edit_body = view.findViewById(R.id.edit_body) as EditText
        edit_body.setText(task.body.toString());

        val dialog = AlertDialog.Builder(activity)
                .setTitle(R.string.title_edit_task)
                .setView(view)
                .setPositiveButton(R.string.action_edit, null)
                .setNeutralButton(R.string.action_delete, { dialog, which ->
                    activity.observable(api.delete(task.id), object : Subscriber<DeleteTaskResponse>() {
                        override fun onCompleted() {
                            inputMethodManager?.hideSoftInputFromWindow(view.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
                            onDeleteListener?.onComplete();
                        }

                        override fun onNext(response: DeleteTaskResponse) {
                            onDeleteListener?.onDelete(task);
                        }

                        override fun onError(e: Throwable?) {
                            inputMethodManager?.hideSoftInputFromWindow(view.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
                            onDeleteListener?.onError(e);
                        }
                    }) ;

                })
                .setNegativeButton(android.R.string.cancel, null)
                .show();

        dialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener {
            activity.observable(api.edit(task.id, edit_body.text.toString(), task.state), object : Subscriber<NewTaskResponse>() {
                override fun onCompleted() {
                    inputMethodManager?.hideSoftInputFromWindow(view.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
                    onEditListener?.onComplete()
                    dialog.dismiss()
                }

                override fun onNext(response: NewTaskResponse) {
                    task.body = response.task.body
                    onEditListener?.onEdit(response.task)
                }

                override fun onError(e: Throwable?) {
                    inputMethodManager?.hideSoftInputFromWindow(view.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
                    onEditListener?.onError(e)
                }
            })
        }

    }

    interface OnEditListener {
        fun onEdit(task: Task)

        fun onError(e: Throwable?)

        fun onComplete()

    }

    interface OnDeleteListener {
        fun onDelete(task: Task)

        fun onError(e: Throwable?)

        fun onComplete()

    }


    fun setInputMethodManager(inputMethodManager: InputMethodManager): EditTaskDialog{
        this.inputMethodManager = inputMethodManager;
        return this
    }

}
