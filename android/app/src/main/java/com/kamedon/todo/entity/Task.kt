package com.kamedon.todo.entity

import java.io.Serializable

/**
 * Created by kamedon on 2/21/16.
 */
data class Task(var id: Int, var body: String, var state: String, var createdAt: String, var updatedAt: String) : Serializable {
    companion object {
        val state_complete = "complete"
        val state_untreated = "untreated"
    }

}

