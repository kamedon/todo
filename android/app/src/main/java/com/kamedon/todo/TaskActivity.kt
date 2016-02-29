package com.kamedon.todo

import android.content.res.Resources
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import com.kamedon.todo.anim.TaskFormAnimation
import kotlinx.android.synthetic.main.activity_task.*
import kotlinx.android.synthetic.main.content_task.*
import kotlinx.android.synthetic.main.content_task.view.*

/**
 * Created by kamedon on 2/29/16.
 */
class TaskActivity : AppCompatActivity() {
    lateinit var taskFormAnimation: TaskFormAnimation

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task)
        val toolbar = findViewById(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)
        taskFormAnimation = TaskFormAnimation(layout_register_form)
        taskFormAnimation.topMargin = resources.getDimension(R.dimen.activity_vertical_margin)
        btn_toggle_task.setOnClickListener {
            taskFormAnimation.toggle();
        }

        btn_register.setOnClickListener{


        }
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        taskFormAnimation.init();
    }

}
