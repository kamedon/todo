package com.kamedon.todo.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.CheckBox
import android.widget.TextView
import com.kamedon.todo.R
import com.kamedon.todo.entity.Task

/**
 * Created by kamedon on 2/29/16.
 */
class TaskListAdapter(val layoutInflater: LayoutInflater, var list: MutableList<Task>) : BaseAdapter() {
    var onComplete: (View, Task, Boolean) -> Unit = { view, task, complete -> }

    override fun getCount(): Int {
        return list.size
    }

    override fun getItem(position: Int): Task {
        return list[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View? {
        // Viewがリサイクル出来ない場合
        var holder: ViewHolder;
        var layout = if (convertView == null) {
            // Viewにレイアウトの情報を登録する
            val view = layoutInflater.inflate(R.layout.list_task, null);
            // Viewがリサイクル出来る場合はnewする必要がないので、holderはここでnewする
            holder = ViewHolder(view);
            // リサイクルするときのためにタグ付けしておく
            view.tag = holder;
            view
        } else {
            // Viewがリサイクル出来る場合
            // タグ付けしておいた情報を取得する
            holder = convertView.tag as ViewHolder;
            convertView
        }
        val item = getItem(position);
        holder.textBody.text = item.body
        holder.checkComplete.isChecked = !item.state.equals("untreated")
        holder.checkComplete.setOnCheckedChangeListener { compoundButton, b ->
            item.state = if (b) {
                Task.state_complete
            } else {
                Task.state_untreated
            }
            onComplete(compoundButton, item, true)
        }
        return layout
    }


}

private class ViewHolder(var view: View) {
    var textBody: TextView
    var checkComplete: CheckBox

    init {
        textBody = view.findViewById(R.id.text_body) as TextView
        checkComplete = view.findViewById(R.id.checkbox_complete) as CheckBox
    }

}
