package com.kamedon.todo.entity

import android.content.res.Resources
import com.kamedon.todo.R
import java.io.Serializable

/**
 * Created by kamedon on 2/21/16.
 */
data class Task(var id: Int, var body: String, var state: String, var createdAt: String, var updatedAt: String) : Serializable {
    companion object {
        val state_complete = "complete"
        val state_untreated = "untreated"
        //query用
        val state_all = "all"


    }

    fun state(resources: Resources) = when (state) {
        state_complete -> resources.getString(R.string.task_complete)
        state_untreated -> resources.getString(R.string.task_untreated)
        state_all -> resources.getString(R.string.task_all)
        else -> ""
    }


}

