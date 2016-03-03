package com.kamedon.todo.extension

/**
 * Created by kamedon on 3/3/16.
 */

inline fun String.isMail() = this.matches("""[\w\d_.-]+@[\w\d_-]+\.[\w\d._-]+""".toRegex())
