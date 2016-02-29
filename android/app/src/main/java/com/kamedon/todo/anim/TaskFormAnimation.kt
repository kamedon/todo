package com.kamedon.todo.anim

import android.animation.Animator
import android.util.Log
import android.view.View

/**
 * Created by kamedon on 2/29/16.
 */
class TaskFormAnimation(val view: View) {
    var layoutFormTop: Float = -1f
    var layoutFormBottom: Float = -1f
    var topMargin: Float = 0f
    var isShow: Boolean = true

    fun init() {
        if (layoutFormTop == -1f) {
            layoutFormTop = view.top.toFloat();
        }
        if (layoutFormBottom == -1f) {
            layoutFormBottom = view.bottom.toFloat();
        }
    }

    fun toggle() {
        if (isShow) {
            Log.d("anime", "hide");
            val y = -layoutFormBottom + topMargin;
            view.animate().translationY(y).setListener(object : Animator.AnimatorListener {
                override fun onAnimationRepeat(p0: Animator?) {
                }

                override fun onAnimationEnd(p0: Animator?) {
                    isShow = false;
                }

                override fun onAnimationCancel(p0: Animator?) {
                }

                override fun onAnimationStart(p0: Animator?) {
                }

            }

            ).start()
        } else {
            Log.d("anime", "hide");
            val y = layoutFormTop - topMargin;
            //                layout_register_form.y = -layoutFormBottom + topMargin;
            view.animate().translationY(y).setListener(object : Animator.AnimatorListener {
                override fun onAnimationRepeat(p0: Animator?) {
                }

                override fun onAnimationEnd(p0: Animator?) {
                    isShow = true;
                }

                override fun onAnimationCancel(p0: Animator?) {
                }

                override fun onAnimationStart(p0: Animator?) {
                }

            }).start()
        }
    }
}